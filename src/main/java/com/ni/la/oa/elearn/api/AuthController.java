package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.auth.*;
import com.ni.la.oa.elearn.domain.RefreshToken;
import com.ni.la.oa.elearn.domain.Role;
import com.ni.la.oa.elearn.domain.User;
import com.ni.la.oa.elearn.repo.RefreshTokenRepository;
import com.ni.la.oa.elearn.repo.UserRepository;
import com.ni.la.oa.elearn.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final RefreshTokenRepository refreshTokens;

    public AuthController(UserRepository users,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtService jwt,
                          RefreshTokenRepository refreshTokens) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
        this.refreshTokens = refreshTokens;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        User u = new User();
        u.setEmail(req.email().toLowerCase().trim());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(Role.STUDENT);
        users.save(u);
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getEmail(), u.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email().toLowerCase().trim(),
                        req.password()
                )
        );

        UserDetails principal = (UserDetails) auth.getPrincipal();

        List<String> roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .toList();

        Map<String, Object> claims = Map.of("roles", roles);

        String access = jwt.generateAccess(principal.getUsername(), claims);
        String refresh = jwt.generateRefresh(principal.getUsername());

        // save refresh token
        User user = users.findByEmail(principal.getUsername()).orElseThrow();
        Instant expiresAt = jwt.parse(refresh).getBody().getExpiration().toInstant();

        RefreshToken rt = new RefreshToken();
        rt.setToken(refresh);
        rt.setUser(user);
        rt.setExpiresAt(expiresAt);
        refreshTokens.save(rt);

        Instant accessExp = jwt.parse(access).getBody().getExpiration().toInstant();
        long expiresIn = Math.max(0, Duration.between(Instant.now(), accessExp).toSeconds());

        return ResponseEntity.ok(new AuthResponse(access, refresh, expiresIn));
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        try {
            Jws<Claims> jws = jwt.parse(req.refreshToken()); // throws if invalid/expired signature
            String email = jws.getBody().getSubject();

            // must exist, not revoked, not expired
            RefreshToken stored = refreshTokens.findByToken(req.refreshToken())
                    .filter(rt -> !rt.isRevoked())
                    .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                    .orElseThrow(); // 404/401 -> handled by catch below

            // issue new pair
            User user = users.findByEmail(email).orElseThrow();
            List<String> roles = List.of(user.getRole().name());
            String newAccess = jwt.generateAccess(email, Map.of("roles", roles));
            String newRefresh = jwt.generateRefresh(email);

            // rotate: revoke old, save new
            stored.setRevoked(true);
            refreshTokens.save(stored);

            RefreshToken newRt = new RefreshToken();
            newRt.setToken(newRefresh);
            newRt.setUser(user);
            newRt.setExpiresAt(jwt.parse(newRefresh).getBody().getExpiration().toInstant());
            refreshTokens.save(newRt);

            Instant accessExp = jwt.parse(newAccess).getBody().getExpiration().toInstant();
            long expiresIn = Math.max(0, Duration.between(Instant.now(), accessExp).toSeconds());

            return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh, expiresIn));
        } catch (Exception ex) {
            return ResponseEntity.status(401).build();
        }
    }



    // TODO now for testing purpose, add validation or remove
    @PreAuthorize("permitAll()")
    @PostMapping("/register-teacher")
    public ResponseEntity<?> registerTeacher(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        User u = new User();
        u.setEmail(req.email().toLowerCase().trim());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(Role.TEACHER);
        users.save(u);
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getEmail(), u.getRole().name()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestBody LogoutRequest req, Authentication auth) {
        // Option A: revoke only the provided refresh token
        if (req != null && req.refreshToken() != null && !req.refreshToken().isBlank()) {
            refreshTokens.findByToken(req.refreshToken()).ifPresent(rt -> {
                if (!rt.isRevoked()) { rt.setRevoked(true); refreshTokens.save(rt); }
            });
        } else {
            // Option B: revoke all refresh tokens for current user
            User me = users.findByEmail(auth.getName()).orElseThrow();
            refreshTokens.findAll().stream()
                    .filter(rt -> rt.getUser().getId().equals(me.getId()) && !rt.isRevoked())
                    .forEach(rt -> { rt.setRevoked(true); refreshTokens.save(rt); });
        }
        return ResponseEntity.ok(new LogoutResponse("Logged out. Refresh token revoked."));
    }

    //Frontend notes (how to use)
    //
    //On login: store accessToken + refreshToken.
    //
    //For each API call: send Authorization: Bearer <accessToken>.
    //
    //If 401 due to expired access: call POST /auth/refresh with the stored refreshToken, then replace both tokens with the response.
    //
    //On logout: call POST /auth/logout with the current refreshToken, then delete tokens locally.

}

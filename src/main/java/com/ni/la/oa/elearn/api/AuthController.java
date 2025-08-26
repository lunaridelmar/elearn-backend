package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.*;
import com.ni.la.oa.elearn.domain.Role;
import com.ni.la.oa.elearn.domain.User;
import com.ni.la.oa.elearn.repo.UserRepository;
import com.ni.la.oa.elearn.security.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthController(UserRepository users,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
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
                        req.email().toLowerCase().trim(), req.password()
                )
        );

        var principal = (UserDetails) auth.getPrincipal();

        // flatten authorities -> ["STUDENT"] or ["TEACHER", ...]
        var roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())        // e.g. "ROLE_STUDENT"
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r) // -> "STUDENT"
                .toList();

        // minimal, portable claims
        Map<String, Object> claims = Map.of("roles", roles);

        String access = jwt.generateAccess(principal.getUsername(), claims);
        String refresh = jwt.generateRefresh(principal.getUsername());
        long expiresIn = jwt.accessExpirySeconds();

        return ResponseEntity.ok(new AuthResponse(access, refresh, expiresIn));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        try {
            var claims = jwt.parse(req.refreshToken()).getBody();
            String email = claims.getSubject();
            var user = users.findByEmail(email).orElseThrow();

            // roles claim
            var roles = List.of(user.getRole().name());
            String newAccess = jwt.generateAccess(email, Map.of("roles", roles));
            String newRefresh = jwt.generateRefresh(email); // optional rotation
            long expiresIn    = jwt.accessExpirySeconds();

            return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh, expiresIn));
        } catch (JwtException e) {
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

}

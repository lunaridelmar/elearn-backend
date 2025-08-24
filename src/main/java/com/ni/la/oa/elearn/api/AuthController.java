package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.RegisterRequest;
import com.ni.la.oa.elearn.api.dto.UserResponse;
import com.ni.la.oa.elearn.domain.Role;
import com.ni.la.oa.elearn.domain.User;
import com.ni.la.oa.elearn.repo.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
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
}

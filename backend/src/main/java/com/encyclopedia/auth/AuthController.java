package com.encyclopedia.auth;

import com.encyclopedia.auth.dto.AuthResponseDto;
import com.encyclopedia.auth.dto.LoginRequestDto;
import com.encyclopedia.auth.dto.RegisterRequestDto;
import com.encyclopedia.common.ApiException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthController(AuthService authService,
                         JwtTokenProvider tokenProvider,
                         UserRepository userRepository) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto dto) {
        String token = authService.authenticate(dto.username(), dto.password());
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> ApiException.unauthorized("Invalid username or password"));
        return new AuthResponseDto(token, user.getUsername(), user.getRole().name());
    }

    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto dto) {
        User user = authService.register(dto.username(), dto.email(), dto.password());
        String token = tokenProvider.generateToken(user);
        return new AuthResponseDto(token, user.getUsername(), user.getRole().name());
    }

    @GetMapping("/me")
    public AuthResponseDto me(@org.springframework.security.core.annotation.AuthenticationPrincipal User user) {
        if (user == null) {
            throw ApiException.unauthorized("Not authenticated");
        }
        return new AuthResponseDto(null, user.getUsername(), user.getRole().name());
    }
}

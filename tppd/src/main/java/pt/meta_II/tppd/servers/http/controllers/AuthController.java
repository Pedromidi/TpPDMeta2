package pt.meta_II.tppd.servers.http.controllers;

import pt.meta_II.tppd.servers.http.security.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

}

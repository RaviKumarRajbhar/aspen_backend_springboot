package com.example.aspen.Security;

import com.example.aspen.Entities.User;
import com.example.aspen.Repository.UserRepository;
import com.example.aspen.Service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public OAuthSuccessHandler(UserRepository userRepository, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();

        String email = googleUser.getAttribute("email");

        String name = googleUser.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);

            return userRepository.save(newUser);
        });

        String userId = user.getId().toString();


        String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());

        refreshTokenService.saveRefreshToken(userId , refreshToken);

        response.setContentType(
                "/application/json"
        );

        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                "accessToken" : "%s",
                "refreshToken" : "%s"
                }
                """.formatted(
                        accessToken,
                refreshToken
        ));


        }

}
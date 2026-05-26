package com.example.aspen.Security.RateLimit;

import com.example.aspen.Service.RateLimiterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;


@Component
public class RateLimitingFilter extends OncePerRequestFilter {

private final RateLimiterService rateLimiterService;

private final ObjectMapper objectMapper;

private static  final  Map<String , RateLimitRule > RATE_LIMIT_RULES =
        Map.of(

                "/auth/login" , new RateLimitRule(5 , 60),

                "/auth/register/initiate" , new RateLimitRule(3 ,60),

                "/auth/register/verify" , new RateLimitRule(3 , 60),

                "/posts" , new RateLimitRule(10 , 60),

                "/comments" , new RateLimitRule(20 , 60),

                "/likes" , new RateLimitRule(100 , 60)
        );

    public RateLimitingFilter(  RateLimiterService rateLimiterService, ObjectMapper objectMapper) {
        this.rateLimiterService = rateLimiterService;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        RateLimitRule rule = null;

        for (Map.Entry<String, RateLimitRule> entry
                : RATE_LIMIT_RULES.entrySet()) {

            if (path.startsWith(entry.getKey())) {

                rule = entry.getValue();

                break;
            }
        }


        if(rule == null){
            filterChain . doFilter(request , response);
            return;
        }

        String identifier = getIdentifier(request);

        String redisKey = "rate_limit:"
                + path.replace("/" , "_")
                + ":"
                +identifier;

        boolean allowed = rateLimiterService.isAllowed(redisKey , rule.getLimit(), rule.getDurationInSeconds());

        if(!allowed) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String body = objectMapper.writeValueAsString(
                    Map.of("message" , "Too many requests. Try again later.")
            );

            response.getWriter().write(body);

            return;
        }

        filterChain.doFilter(request , response);
    }

    private String getIdentifier(
            HttpServletRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            return authentication.getName();
        }

        return request.getRemoteAddr();
    }
}



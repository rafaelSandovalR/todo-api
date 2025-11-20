package com.rsandoval.todo_api.config;

import com.rsandoval.todo_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Infrastructure Bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Get the "Authorization" header from the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check if the header exists and starts with "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Pass the request along (it might be for /auth/login
            return;
        }

        // Extract the token (remove the "Bearer" prefix)
        jwt = authHeader.substring(7);

        // Extract the username from the token
        username = jwtService.extractUsername(jwt);

        // If username exists AND we aren't already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Validate token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Create an Authentication Token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Set the "Green Light" in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}

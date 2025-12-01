package io.github.kacper.weglarz.realtimecollaboration.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Component that handles authorization of HTTP requests
 */
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Autowired
    public JWTFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Filters each HTTP request to validate the JWT token if the authorization header contains a valid Bearer token
     * it sets the security context with the authenticated user otherwise, it returns status 401
     * @param request  incoming HTTP request
     * @param response outgoing HTTP response
     * @param filterChain filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Avoids WebSocket endpoint /ws
        if (request.getRequestURI().startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.isTokenValid(token)) {
                String username = jwtService.getUsername(token);
                    SecurityContext contextHolder = SecurityContextHolder.createEmptyContext();
                    Authentication authentication =
                                                new UsernamePasswordAuthenticationToken
                                                (username, null, new ArrayList<>());
                    contextHolder.setAuthentication(authentication);
                    SecurityContextHolder.setContext(contextHolder);
            } else {
                response.setStatus(401);
            }
        }
        filterChain.doFilter(request, response);
    }
}

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
 * Komponent obsługujący autoryzacje żądań http
 */
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Autowired
    public JWTFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Filtruje każde żądanie HTTP w celu weryfikacji tokena JWT
     * Jeśli nagłówek Authorization zawiera poprawny token Bearer
     * ustawia kontekst bezpieczeństwa z uwierzytelnionym użytkownikiem
     * W przeciwnym razie zwraca status 401.
     *
     * @param request żądanie HTTP
     * @param response odpowiedź HTTP
     * @param filterChain łańcuch filtrów
     * @throws ServletException w przypadku błędu serwletu
     * @throws IOException w przypadku błędu wejścia/wyjścia
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // pomija websocket endpoint /ws
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

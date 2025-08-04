package dev.andrepontde.retailmanager.retail_system.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Authentication Filter that validates JWT tokens on each request.
 * Extracts user information from JWT and sets Spring Security context.
 * 
 * This filter is self-contained and doesn't depend on UserService to avoid circular dependencies.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Extract JWT from Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("Error extracting username from JWT: " + e.getMessage());
            }
        }

        // If we have a username and no existing authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Validate the token
            if (jwtUtil.validateToken(jwt)) {
                try {
                    // Extract user information from JWT
                    Long userId = jwtUtil.extractUserId(jwt);
                    String role = jwtUtil.extractRole(jwt);
                    Long storeId = jwtUtil.extractStoreId(jwt);

                    // Store user information in request attributes for later use by services
                    request.setAttribute("userId", userId);
                    request.setAttribute("username", username);
                    request.setAttribute("userRole", role);
                    request.setAttribute("userStoreId", storeId);

                    // Create Spring Security authentication
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } catch (Exception e) {
                    logger.error("Error setting authentication from JWT: " + e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Skip JWT validation for public endpoints
        return path.startsWith("/api/users/login") || 
               path.startsWith("/api/users/register") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/h2-console/") ||
               path.equals("/");
    }
}

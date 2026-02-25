package Israt.example.Hr.and.payroll.management.system.backend.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        try {
            logger.debug("Incoming request {} {} Authorization-present={}", request.getMethod(), request.getRequestURI(), authorizationHeader != null);
        } catch (Exception ex) { }

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.debug("Extracted username from JWT: {}", username);
            } catch (Exception e) {
                logger.debug("Failed to extract username from JWT: {}", e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    try { logger.debug("JWT auth set for user='{}', authorities={}", username, userDetails.getAuthorities()); } catch (Exception ex) { }
                }
            } catch (RuntimeException ex) {

                try {
                    if (jwt != null && jwtUtil.validateToken(jwt, username)) {
                        String role = null;
                        try { role = jwtUtil.extractRole(jwt); } catch (Exception e) { }
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role != null ? role : "USER");
                        UserDetails fallback = org.springframework.security.core.userdetails.User.builder()
                                .username(username)
                                .password("")
                                .authorities(authority)
                                .build();
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                fallback, null, fallback.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.debug("JWT auth (fallback) set for user='{}', authorities={}", username, fallback.getAuthorities());
                    }
                } catch (Exception e2) {
                    logger.debug("Failed to set fallback JWT auth: {}", e2.getMessage());
                }
            }
        }
        chain.doFilter(request, response);
    }
}

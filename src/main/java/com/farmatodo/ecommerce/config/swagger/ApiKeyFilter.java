package com.farmatodo.ecommerce.config.swagger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String headerName;
    private final String expectedApiKey;

    private static final AntPathMatcher M = new AntPathMatcher();
    private static final String[] WHITELIST = {
            "/ping",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/actuator/health",
            "/h2-console/**"
    };

    public ApiKeyFilter(String headerName, String expectedApiKey) {
        this.headerName = headerName;
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String p : WHITELIST) if (M.match(p, path)) return true;
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String provided = req.getHeader(headerName);
        if (expectedApiKey != null && !expectedApiKey.isBlank() && expectedApiKey.equals(provided)) {
            chain.doFilter(req, res);
        } else {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}

package com.farmatodo.ecommerce.config.swagger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyFilter extends OncePerRequestFilter {
    private final String headerName, expectedValue;
    private final AntPathMatcher m = new AntPathMatcher();

    public ApiKeyFilter(String headerName, String expectedValue) {
        this.headerName = headerName; this.expectedValue = expectedValue;
    }
    @Override protected boolean shouldNotFilter(HttpServletRequest req) {
        String p = req.getServletPath();
        return m.match("/ping", p) ||
                m.match("/v3/api-docs/**", p) ||
                m.match("/swagger-ui/**", p) ||
                m.match("/swagger-ui.html", p);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (expectedValue == null || expectedValue.isBlank()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("API Key not configured"); return;
        }
        if (!expectedValue.equals(response.getHeader(headerName))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid API Key"); return;
        }
        filterChain.doFilter(request, response);
    }

}

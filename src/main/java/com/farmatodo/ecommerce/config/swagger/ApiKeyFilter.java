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
    private final String expectedValue;
    private final AntPathMatcher m = new AntPathMatcher();

    public ApiKeyFilter(String headerName, String expectedValue) {
        this.headerName = headerName;
        this.expectedValue = expectedValue;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String p = req.getServletPath();
        return m.match("/ping", p)
                || m.match("/v3/api-docs/**", p)
                || m.match("/swagger-ui/**", p)
                || m.match("/swagger-ui.html", p);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String provided = Optional.ofNullable(request.getHeader(headerName)).orElse("").trim();
        String expected = Optional.ofNullable(expectedValue).orElse("").trim();

        log.info("API-KEY expected='{}' provided='{}' header='{}' path='{}'",
                expected, provided, headerName, request.getServletPath());

        if (expected.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("API Key not configured");
            return;
        }

        if (!expected.equals(provided)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("Missing or invalid API Key");
            return;
        }

        // ✅ Autenticar la petición para que pase .authenticated()
        var auth = new UsernamePasswordAuthenticationToken(
                "apiKeyUser", null, List.of(new SimpleGrantedAuthority("ROLE_API")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}

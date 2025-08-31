package com.farmatodo.ecommerce.config.swagger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${security.apiKeyHeader:X-API-KEY}")
    private String headerName;

    @Value("${security.apiKey:changeme-dev}")
    private String expected;

    private static final String[] WHITELIST = {
            "/ping", "/swagger-ui", "/v3/api-docs"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        // Permitir rutas públicas sin API Key
        for (String w : WHITELIST) {
            if (path.equals(w) || path.startsWith(w + "/")) {
                chain.doFilter(req, res);
                return;
            }
        }



        // Validar API Key
        String provided = req.getHeader(headerName);
        if (provided == null || !provided.equals(expected)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // ✅ Marcar la petición como autenticada
        var auth = new UsernamePasswordAuthenticationToken(
                "api-key-user",  // principal simbólico
                null,            // credentials
                java.util.List.of(new SimpleGrantedAuthority("ROLE_API_KEY")) // roles opcionales
        );

        // (forma recomendada en Spring Security 6)
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);


        System.err.println("APIKEY CHECK uri="+path+"provided="+provided+" expected="+expected);
        chain.doFilter(req, res);
    }
}

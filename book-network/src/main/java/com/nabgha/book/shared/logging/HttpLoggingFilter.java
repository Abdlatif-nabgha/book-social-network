package com.nabgha.book.shared.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Skip verbose internal swagger / static asset noise if preferred
        boolean isApiRequest = uri.startsWith("/api/v1") || uri.startsWith("/auth") || uri.startsWith("/books") || uri.startsWith("/users") || uri.startsWith("/feedbacks") || uri.startsWith("/admin");

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (isApiRequest) {
                long duration = System.currentTimeMillis() - startTime;
                int status = response.getStatus();
                String method = request.getMethod();

                if (status >= 500) {
                    log.error("[HTTP] {} {} -> {} ({} ms)", method, uri, status, duration);
                } else if (status >= 400) {
                    log.warn("[HTTP] {} {} -> {} ({} ms)", method, uri, status, duration);
                } else {
                    log.info("[HTTP] {} {} -> {} ({} ms)", method, uri, status, duration);
                }
            }
        }
    }
}

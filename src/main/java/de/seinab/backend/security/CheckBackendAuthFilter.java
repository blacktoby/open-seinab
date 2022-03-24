package de.seinab.backend.security;

import de.seinab.form.security.authentication.FormAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CheckBackendAuthFilter extends OncePerRequestFilter {
    @Value("${de.seinab.backend.request.prefix}")
    private String backendPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if(StringUtils.startsWith(request.getRequestURI(), backendPrefix) && authentication != null && authentication.isAuthenticated()
            && authentication instanceof FormAuthenticationToken) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}

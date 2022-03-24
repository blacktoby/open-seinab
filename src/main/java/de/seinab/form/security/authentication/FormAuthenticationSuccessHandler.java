package de.seinab.form.security.authentication;

import de.seinab.form.security.FormUrlParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FormAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${de.seinab.form.request.mvc.pattern}")
    private String formUrlPattern;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        FormUrlParser formUrlParser = new FormUrlParser(request, formUrlPattern);
        String url = formUrlParser.buildNewUrl(formUrlPattern);
        redirectStrategy.sendRedirect(request, response, url);
    }
}

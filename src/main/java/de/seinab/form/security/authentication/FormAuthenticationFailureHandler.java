package de.seinab.form.security.authentication;

import de.seinab.form.security.FormUrlParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FormAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${de.seinab.form.request.mvc.pattern}")
    private String formUrlPattern;
    @Value("${de.seinab.form.request.login.error.url}")
    private String formErrorUrl;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        FormUrlParser formUrlParser = new FormUrlParser(request, formUrlPattern);
        String failureUrl = formUrlParser.buildNewUrl(formErrorUrl);
        super.saveException(request, exception);
        redirectStrategy.sendRedirect(request, response, failureUrl);
    }
}

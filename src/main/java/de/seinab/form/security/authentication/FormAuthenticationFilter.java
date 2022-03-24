package de.seinab.form.security.authentication;

import de.seinab.form.security.FormUrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger log = LoggerFactory.getLogger(FormAuthenticationFilter.class);

    private String formUrlPattern;

    public FormAuthenticationFilter(String formUrlPattern, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        super(new AntPathRequestMatcher(formUrlPattern + "/login", "POST"));
        this.formUrlPattern = formUrlPattern;
        super.setAuthenticationFailureHandler(failureHandler);
        super.setAuthenticationSuccessHandler(successHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        FormUrlParser formUrlParser = new FormUrlParser(request, formUrlPattern);
        String formname = formUrlParser.getFormName();
        String eventGroupName = formUrlParser.getEventGroupName();
        String password = obtainPassword(request);

        if (formname == null) {
            formname = "";
        }

        if(eventGroupName == null) {
            eventGroupName = "";
        }

        if (password == null) {
            password = "";
        }

        formname = formname.trim();

        FormAuthenticationToken authRequest = new FormAuthenticationToken(eventGroupName, formname, password);
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainPassword(HttpServletRequest request) {
        return request.getParameter("password");
    }

    private void setDetails(HttpServletRequest request,
                            FormAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }




}

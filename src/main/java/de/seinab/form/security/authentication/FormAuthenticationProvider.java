package de.seinab.form.security.authentication;

import de.seinab.form.FormService;
import de.seinab.form.entities.Form;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;

@Component
public class FormAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(FormAuthenticationProvider.class);

    @Autowired
    private FormService formService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(FormAuthenticationToken.class, authentication, "Only FormAuthenticationToken is supported");

        FormAuthenticationToken token = (FormAuthenticationToken) authentication;
        Form form = formService.getForm(token.getEventGroupName(), token.getName());
        if (form == null) {
            return throwNotFoundException(token);
        }

        if (StringUtils.isEmpty(form.getPassword())) {
            return createAuthenticationSuccess(token, form);
        }

        if (!passwordEncoder.matches(token.getCredentials().toString(), form.getPassword())) {
            log.debug("Authentication failed: password does not match stored value");
            throw new BadCredentialsException("Bad credentials");
        }

        return createAuthenticationSuccess(token, form);
    }

    private Authentication throwNotFoundException(FormAuthenticationToken token) {
        log.debug("Form with name {} and eventGroup {} not found.", token.getName(), token.getEventGroupName());
        throw new BadCredentialsException("Bad credentials");
    }

    private FormAuthenticationToken createAuthenticationSuccess(FormAuthenticationToken token, Form form) {
        return new FormAuthenticationToken(form.getEventGroup().getName(), form.getName(), token.getCredentials(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + form.getName())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (FormAuthenticationToken.class
                .isAssignableFrom(authentication));
    }
}

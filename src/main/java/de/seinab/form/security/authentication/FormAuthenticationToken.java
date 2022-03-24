package de.seinab.form.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FormAuthenticationToken extends AbstractAuthenticationToken {

    private final String formName;
    private final String eventGroupName;
    private Object credentials;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public FormAuthenticationToken(String eventGroupName, String formName, Object credentials,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.formName = formName;
        this.eventGroupName = eventGroupName;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    public FormAuthenticationToken(String eventGroupName, String formName, Object credentials) {
        super(null);
        this.formName = formName;
        this.eventGroupName = eventGroupName;
        this.credentials = credentials;
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    public String getEventGroupName() {
        return eventGroupName;
    }

    @Override
    public Object getPrincipal() {
        return formName;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}

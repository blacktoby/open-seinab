package de.seinab.form.security.authentication;

import de.seinab.form.FormService;
import de.seinab.form.entities.Form;
import de.seinab.form.security.FormUrlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FormWithoutPasswordFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private FormService formService;

    private String formUrlPattern;

    public FormWithoutPasswordFilter(String formUrlPattern) {
        super(new AntPathRequestMatcher(formUrlPattern));
        this.formUrlPattern = formUrlPattern;
        super.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        FormUrlParser formUrlParser = new FormUrlParser(request, formUrlPattern);
        String formName = formUrlParser.getFormName();
        String eventGroupName = formUrlParser.getEventGroupName();

        if(authenticationNeeded(eventGroupName, formName)) {
            return null;
        }

        return new FormAuthenticationToken(
                eventGroupName, formName, "");
    }


    public boolean authenticationNeeded(String eventGroupName, String formName) {
        Form form = formService.getForm(eventGroupName, formName);
        if(form == null) {
            return true;
        }
        return !StringUtils.isEmpty(form.getPassword());
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}

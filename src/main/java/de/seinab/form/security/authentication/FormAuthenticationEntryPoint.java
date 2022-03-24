package de.seinab.form.security.authentication;

import de.seinab.form.FormService;
import de.seinab.form.entities.Form;
import de.seinab.form.security.FormUrlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FormAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    @Autowired
    private FormService formService;
    @Value("${de.seinab.form.request.mvc.pattern}")
    private String formUrlPattern;
    @Value("${de.seinab.form.request.login.url}")
    private String formLoginUrl;
    @Value("${de.seinab.form.request.nopassword.url}")
    private String formNoPasswordUrl;

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public FormAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        FormUrlParser formUrlParser = new FormUrlParser(request, formUrlPattern);
        String formName = formUrlParser.getFormName();
        String eventGroup = formUrlParser.getEventGroupName();
        String url = formUrlParser.buildNewUrl(formLoginUrl);
        if(!authenticationNeeded(eventGroup, formName)) {
            url = formUrlParser.buildNewUrl(formNoPasswordUrl);
        }
        return url;
    }

    public boolean authenticationNeeded(String eventGroupName, String formName) {
        Form form = formService.getForm(eventGroupName, formName);
        if(form == null) {
            return true;
        }
        return !StringUtils.isEmpty(form.getPassword());
    }
}

package de.seinab.form.security;

import de.seinab.form.FormService;
import de.seinab.form.entities.Form;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CheckFormAuthFilter extends OncePerRequestFilter {
    @Value("${de.seinab.form.request.mvc.pattern}")
    private String formPattern;

    @Autowired
    private FormService formService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        FormUrlParser formUrlParser = new FormUrlParser(request, formPattern);
        if(formUrlParser.pathMatchesPattern() && authentication != null && authentication.isAuthenticated()) {
            String formName = formUrlParser.getFormName();
            String eventGroup = formUrlParser.getEventGroupName();

            if(StringUtils.isNoneEmpty(formName) && StringUtils.isNoneEmpty(eventGroup)) {
                Form form = formService.getForm(eventGroup, formName);

                if(form != null) {
                    String confirmedFormName = form.getName();
                    boolean authenticatedForThisForm =
                            authentication.getAuthorities().stream().anyMatch(auth -> StringUtils.equals(auth.getAuthority(), "ROLE_"+confirmedFormName));

                    if(!authenticatedForThisForm) {
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        }


        filterChain.doFilter(request, response);
    }
}

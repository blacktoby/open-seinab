package de.seinab.form.security;

import de.seinab.form.security.authentication.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(1)
public class FormSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private FormAuthenticationProvider formAuthenticationProvider;
    @Autowired
    private CheckFormAuthFilter checkFormAuthFilter;
    @Autowired
    private FormAuthenticationFailureHandler failureHandler;
    @Autowired
    private FormAuthenticationSuccessHandler successHandler;

    @Value("${de.seinab.form.request.mvc.pattern}")
    private String formPattern;
    @Value("${de.seinab.form.request.login.url}")
    private String formLoginPattern;
    @Value("${de.seinab.form.request.nopassword.url}")
    private String formNoPasswordPattern;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/assets/**", formLoginPattern, "/favicon.ico")
                .permitAll()
                .mvcMatchers("/form/**/logo**")
                .permitAll()
                .mvcMatchers("/form/**")
                .authenticated()
                .and()
                .addFilterBefore(formAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                    .mvcMatcher(formPattern)
                    .authenticationProvider(formAuthenticationProvider)
                    .exceptionHandling().authenticationEntryPoint(formAuthEntryPoint())
                .and()
                .addFilterBefore(formFilter(), FormAuthenticationFilter.class)
                .addFilterBefore(checkFormAuthFilter, FormWithoutPasswordFilter.class)
                .csrf().disable();
    }

    @Bean
    public FormAuthenticationEntryPoint formAuthEntryPoint() {
        return new FormAuthenticationEntryPoint(formLoginPattern);
    }

    @Bean
    public FormWithoutPasswordFilter formFilter() {
        return new FormWithoutPasswordFilter(formNoPasswordPattern);
    }

    @Bean
    public FormAuthenticationFilter formAuthFilter() {
        return new FormAuthenticationFilter(formPattern, successHandler, failureHandler);
    }
}

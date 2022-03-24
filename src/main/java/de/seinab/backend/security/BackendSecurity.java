package de.seinab.backend.security;

import de.seinab.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(2)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class BackendSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private CheckBackendAuthFilter checkBackendAuthFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/backend/login", "/backend/dologin**")
                    .permitAll()
                .mvcMatchers("/backend/{eventGroupname}/{formName}/formfee")
                    .access("@formPermissionAccessControl.checkingFormfeePermission(authentication,#eventGroupname, #formName)")
                .mvcMatchers("/backend/api/{eventGroupName}/**", "/backend/{eventGroupName}/**")
                    .access("@eventGroupAccessControl.checkEventGroup(authentication,#eventGroupName)")
                .mvcMatchers("/backend/**").hasRole("BACKEND")
                .and()
                .formLogin()
                    .loginPage("/backend/login")
                    .loginProcessingUrl("/backend/dologin")
                .and()
                .addFilterBefore(checkBackendAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .userDetailsService(userDetailsService())
                .logout()
                    .logoutUrl("/backend/logout")
                    .logoutSuccessUrl("/backend");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

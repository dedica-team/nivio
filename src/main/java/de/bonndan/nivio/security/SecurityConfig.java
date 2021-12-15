package de.bonndan.nivio.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${nivio.loginType}")
    private String loginType;

//    private final CustomOAuth2UserService userService;
//
//    public SecurityConfig(CustomOAuth2UserService userService) {
//        this.userService = userService;
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if ("required".equalsIgnoreCase(loginType)) {
            configureForRequired(http);
        }
        if ("optional".equalsIgnoreCase(loginType)) {
            configureForOptional(http);
        }
        if ("none".equalsIgnoreCase(loginType)) {
            configureForNone(http);
        }
    }

    protected void configureForOptional(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().oauth2Login().defaultSuccessUrl("/")
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }


    protected void configureForRequired(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/login/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login().defaultSuccessUrl("/")
                .loginPage("/login")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").permitAll();

    }

    protected void configureForNone(HttpSecurity http) throws Exception {
    }

}

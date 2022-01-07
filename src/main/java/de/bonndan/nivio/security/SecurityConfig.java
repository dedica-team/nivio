package de.bonndan.nivio.security;

import de.bonndan.nivio.config.NivioConfigProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN_MODE_REQUIRED = "required";
    public static final String LOGIN_MODE_OPTIONAL = "optional";
    public static final String LOGIN_MODE_NONE = "none";

    private final String loginMode;

    public SecurityConfig(NivioConfigProperties properties) {
        loginMode = properties.getLoginMode();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (LOGIN_MODE_REQUIRED.equalsIgnoreCase(loginMode)) {
            configureForRequired(http);
        }
        if (LOGIN_MODE_OPTIONAL.equalsIgnoreCase(loginMode)) {
            configureForOptional(http);
        }
        if (LOGIN_MODE_NONE.equalsIgnoreCase(loginMode)) {
            configureForNone(http);
        }
    }

    private void configureForOptional(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().oauth2Login().defaultSuccessUrl("/")
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }


    private void configureForRequired(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/login/**", "/icons/svg/nivio.svg", "/icons/svg/github.svg").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login().defaultSuccessUrl("/")
                .loginPage("/login")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login").permitAll();

    }

    protected void configureForNone(HttpSecurity http) {
    }

}

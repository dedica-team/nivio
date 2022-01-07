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
    public static final String LOGIN_PATH = "/login";
    public static final String LOGOUT_PATH = "/logout";
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
                .and().oauth2Login().defaultSuccessUrl("/").loginPage(LOGIN_PATH)
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_PATH))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }


    private void configureForRequired(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_PATH + "/**", "/icons/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login().defaultSuccessUrl("/")
                .loginPage(LOGIN_PATH)
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_PATH))
                .logoutSuccessUrl(LOGIN_PATH).permitAll();

    }

    protected void configureForNone(HttpSecurity http) {
    }

}

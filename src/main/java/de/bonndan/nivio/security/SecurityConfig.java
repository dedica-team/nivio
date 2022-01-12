package de.bonndan.nivio.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN_MODE_REQUIRED = "required";
    public static final String LOGIN_MODE_OPTIONAL = "optional";
    public static final String LOGIN_MODE_NONE = "none";
    public static final String LOGIN_PATH = "/login";
    public static final String LOGOUT_PATH = "/logout";
    public static final String ALL_ORIGINS_HTTP = "http://*";
    public static final String ALL_ORIGINS_HTTPS = "https://*";

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    private final AuthConfigProperties properties;

    public SecurityConfig(AuthConfigProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        LOGGER.info("login mode: {}", properties.getLoginMode());
        if (LOGIN_MODE_NONE.equalsIgnoreCase(properties.getLoginMode())) {
            configureForNone(http);
        }
        if (LOGIN_MODE_REQUIRED.equalsIgnoreCase(properties.getLoginMode())) {
            configureForRequired(http);
        }
        if (LOGIN_MODE_OPTIONAL.equalsIgnoreCase(properties.getLoginMode())) {
            configureForOptional(http);
        }
    }

    private void configureForOptional(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .headers(configurer -> {
                    configurer.frameOptions().disable();
                    configurer.addHeaderWriter(new SameSiteHeaderWriter());
                })
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().oauth2Login()
                .defaultSuccessUrl("/")
                .loginPage(LOGIN_PATH)
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_PATH))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }


    private void configureForRequired(HttpSecurity http) throws Exception {

        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .headers(configurer -> {
                    configurer.frameOptions().disable();
                    configurer.addHeaderWriter(new SameSiteHeaderWriter());
                })
                .authorizeRequests()
                .antMatchers(LOGIN_PATH + "/**", "/icons/**", "/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                //.clientRegistrationRepository(clientRegistrationRepository())
                .defaultSuccessUrl("/")
                .loginPage(LOGIN_PATH)
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_PATH))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl(LOGIN_PATH).permitAll();

    }

    private CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOriginPatterns = properties.getAllowedOriginPatterns();
        if (allowedOriginPatterns == null || allowedOriginPatterns.isEmpty()) {
            allowedOriginPatterns = List.of(ALL_ORIGINS_HTTP, ALL_ORIGINS_HTTPS);
        }
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        //in case authentication is enabled this flag MUST be set, otherwise CORS requests will fail
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    protected void configureForNone(HttpSecurity http) {
    }

    /**
     * This header writer just adds "SameSite=Lax;" to the Set-Cookie response header
     * This is important for local development at least.
     */
    static class SameSiteHeaderWriter implements HeaderWriter {

        private static final String SAME_SITE = "SameSite";
        private static final String SAME_SITE_LAX = "SameSite=Lax";

        private static final String SECURE = "Secure";

        @Override
        public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {

            if (!response.containsHeader(SET_COOKIE)) {
                return;
            }

            var setCookie = response.getHeader(SET_COOKIE);
            var toAdd = new ArrayList<String>();
            toAdd.add(setCookie);

            if (!setCookie.contains(SAME_SITE)) {
                toAdd.add(SAME_SITE_LAX);
            }

            response.setHeader(SET_COOKIE, String.join("; ", toAdd));

        }

    }
}

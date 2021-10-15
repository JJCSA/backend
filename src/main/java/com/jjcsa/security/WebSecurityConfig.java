package com.jjcsa.security;

import com.jjcsa.model.enumModel.UserRole;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.jjcsa.util.KeycloakUtil;

import lombok.NonNull;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@KeycloakConfiguration
public class WebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Value("${security.enabled:true}")
    private boolean isSecurityEnabled;

    @Value("${cors.allowed-origin-patterns}")
    String[] allowedOriginPatterns;

    @Autowired
    public void configureGlobal(@NonNull final AuthenticationManagerBuilder auth) {
        final KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * This method is used to configure APIs that need authentication based on roles.
     */
    @Override
    protected void configure(@NonNull final HttpSecurity http) throws Exception {
        super.configure(http);
        if(isSecurityEnabled) {
            http.authorizeRequests()
                    .antMatchers("/api/users/login", "/api/users/register", "/actuator/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**")
                    .permitAll()
                    .antMatchers("/api/super-admin/**").hasRole(UserRole.SUPER_ADMIN.getRoleText())
                    .antMatchers("/api/admin/**").hasRole(UserRole.ADMIN.getRoleText())
                    .antMatchers("/api/user/**").hasRole(UserRole.USER.getRoleText())
                    .anyRequest().authenticated();
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.csrf().disable();

            // Enable cors
            http.cors();
        } else {
            http.cors().and().csrf().disable()
                    .authorizeRequests().antMatchers("/**").permitAll();
        }

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList(allowedOriginPatterns));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }

}

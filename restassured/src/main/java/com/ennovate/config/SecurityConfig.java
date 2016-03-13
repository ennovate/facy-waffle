package com.ennovate.config;


import com.ennovate.config.facebookUtil.FacebookSignedRequestVerifier;
import com.ennovate.util.TimeSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;

import javax.servlet.Filter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    @Autowired
    TimeSource timeSource;


    @Value("${facebook.app.secret:}")
    private String faceBookAppSecret;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/index.html").permitAll()
                .anyRequest().authenticated()
                .and().addFilterBefore(oAuthAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(new PreAuthenticatedGrantedAuthoritiesUserDetailsService());

        auth.authenticationProvider(authenticationProvider);

    }

    public Filter oAuthAuthenticationProcessingFilter() throws  Exception{

        final OAuthAuthenticationProcessingFilter oAuthAuthenticationProcessingFilter = new OAuthAuthenticationProcessingFilter();
        oAuthAuthenticationProcessingFilter.setAuthenticationManager(new FacebookTokenAuthenticationManager(faceBookSignedRequestVerifier()));
        return oAuthAuthenticationProcessingFilter;
    }

    @Bean
    public FacebookSignedRequestVerifier faceBookSignedRequestVerifier() throws Exception{
        return new FacebookSignedRequestVerifier(faceBookAppSecret, timeSource);

    }
}

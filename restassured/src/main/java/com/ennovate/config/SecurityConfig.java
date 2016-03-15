package com.ennovate.config;


import com.ennovate.config.facebookUtil.FacebookSignedRequestVerifier;
import com.ennovate.util.TimeSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

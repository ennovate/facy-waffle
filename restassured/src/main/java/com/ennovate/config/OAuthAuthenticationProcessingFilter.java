package com.ennovate.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.util.Assert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class OAuthAuthenticationProcessingFilter implements Filter, InitializingBean {


    private final static Log logger = LogFactory.getLog(OAuthAuthenticationProcessingFilter.class);

    private AuthenticationManager authenticationManager;

    private TokenExtractor tokenExtractor = new BearerTokenExtractor();


    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    public void setTokenExtractor(TokenExtractor tokenExtractor) {
        this.tokenExtractor = tokenExtractor;
    }


    public void afterPropertiesSet() {
        Assert.state(authenticationManager != null, "AuthenticationManager is required");
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        try {

            Authentication authentication = tokenExtractor.extract(request);

            if (authentication == null) {
                logger.debug("No token in request, will continue chain.");

            } else {

                Authentication authResult = authenticationManager.authenticate(authentication);

                logger.debug("Authentication success: " + authResult);

                SecurityContextHolder.getContext().setAuthentication(authResult);

            }
        } catch (OAuth2Exception failed) {
            SecurityContextHolder.clearContext();


            return;
        }

        chain.doFilter(request, response);
    }


    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }


}

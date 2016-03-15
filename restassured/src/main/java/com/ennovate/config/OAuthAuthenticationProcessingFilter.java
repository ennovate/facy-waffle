package com.ennovate.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


public class OAuthAuthenticationProcessingFilter implements Filter, InitializingBean {


    private final static Log logger = LogFactory.getLog(OAuthAuthenticationProcessingFilter.class);
    public static final String BEARER_TOKEN_TYPE = "bearer";

    private AuthenticationManager authenticationManager;

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void afterPropertiesSet() {
        Assert.state(authenticationManager != null, "AuthenticationManager is required");
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        try {
            String token = extractHeaderToken(request);

            if (token == null) {
                logger.debug("No token in request, will continue chain.");
            } else {
                Authentication authResult = authenticationManager.authenticate(new PreAuthenticatedAuthenticationToken(token,""));
                logger.debug("Authentication success: " + authResult);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        } catch (Exception failed) {
            SecurityContextHolder.clearContext();
            return;
        }
        chain.doFilter(request, response);
    }


    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    protected String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("Authorization");
        while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(BEARER_TOKEN_TYPE))) {
                String authHeaderValue = value.substring(BEARER_TOKEN_TYPE.length()).trim();
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }

        return null;
    }


}

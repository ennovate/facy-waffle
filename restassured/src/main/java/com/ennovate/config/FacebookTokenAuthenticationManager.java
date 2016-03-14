package com.ennovate.config;

import com.ennovate.config.facebookUtil.FacebookSignedRequestVerifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;
import java.util.UUID;


public class FacebookTokenAuthenticationManager implements AuthenticationManager {


    private FacebookSignedRequestVerifier facebookSignedRequestVerifier;

    public FacebookTokenAuthenticationManager(FacebookSignedRequestVerifier facebookSignedRequestVerifier) {
        this.facebookSignedRequestVerifier = facebookSignedRequestVerifier;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = authentication.getPrincipal().toString();
        if (facebookSignedRequestVerifier.verify(accessToken)) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
            return new PreAuthenticatedAuthenticationToken(UUID.randomUUID().toString(), "", Arrays.asList(grantedAuthority));
        }
        return null;
    }
}

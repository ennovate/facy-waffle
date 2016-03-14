package com.ennovate.config;

import com.ennovate.config.facebookUtil.FacebookSignedRequestVerifier;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FacebookTokenAuthenticationManagerTest {

    FacebookTokenAuthenticationManager facebookTokenAuthenticationManager;
    private FacebookSignedRequestVerifier facebookSignedRequestVerifier;


    @Before
    public void setUp() throws Exception {
        facebookSignedRequestVerifier = mock(FacebookSignedRequestVerifier.class);
        facebookTokenAuthenticationManager = new FacebookTokenAuthenticationManager(facebookSignedRequestVerifier);
    }

    @Test
    public void authenticate_returnsPreAuthenticatedToken() throws Exception {
        final String facebook_signed_request = "facebook_signed_request";
        when(facebookSignedRequestVerifier.verify(facebook_signed_request)).thenReturn(true);
        PreAuthenticatedAuthenticationToken preAuthenticatedUser = new PreAuthenticatedAuthenticationToken(facebook_signed_request, null, null);

        final Authentication authenticatedUser = facebookTokenAuthenticationManager.authenticate(preAuthenticatedUser);
        PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = (PreAuthenticatedAuthenticationToken) authenticatedUser;

        assertThat(authenticatedUser.getAuthorities(), hasSize(1));
        assertThat(preAuthenticatedAuthenticationToken.getAuthorities(), hasItem(new SimpleGrantedAuthority("ROLE_USER")));
        assertThat(preAuthenticatedAuthenticationToken.getName(),is(notNullValue()));

    }

    @Test
    public  void authenticate_returnsNullToken_forInvalidFacebookSignedRequest() throws Exception{

        final String facebook_signed_request = "some_junk_value";
        when(facebookSignedRequestVerifier.verify(facebook_signed_request)).thenReturn(false);
        PreAuthenticatedAuthenticationToken preAuthenticatedUser = new PreAuthenticatedAuthenticationToken(facebook_signed_request, null, null);


        final Authentication authenticatedUser = facebookTokenAuthenticationManager.authenticate(preAuthenticatedUser);
        assertNull(authenticatedUser);


    }




}
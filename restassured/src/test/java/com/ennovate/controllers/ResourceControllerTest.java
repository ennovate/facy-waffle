package com.ennovate.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


public class ResourceControllerTest {

    ResourceController subject;

    MockMvc mockMvc;
    Principal stephanie;

    @Before
    public void setUp() throws Exception {

        subject = new ResourceController();
        mockMvc = standaloneSetup(subject).build();
        stephanie = new PreAuthenticatedAuthenticationToken("Stephanie", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void getUser_returnsLoggedInPrincipal() throws Exception {

        mockMvc.perform(get("/user").principal(stephanie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details", is(nullValue())))
                .andExpect(jsonPath("$.authorities.[0].authority", is("ROLE_USER")))
                .andExpect(jsonPath("$.authenticated", is(true)))
                .andExpect(jsonPath("$.principal", is("Stephanie")))
                .andExpect(jsonPath("$.credentials", is(nullValue())))
                .andExpect(jsonPath("$.name", is("Stephanie")))
        ;

    }

}
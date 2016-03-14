package com.ennovate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OAuthSampleApplication.class)
@WebAppConfiguration()

public class OauthsampleApplicationTests {


	@Resource
	private WebApplicationContext webApplicationContext;

	@Autowired
	FilterChainProxy filterChainProxy;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(filterChainProxy).
				build();
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void getUser_forAnonymousUser_returns403() throws Exception{

		mockMvc.perform(get("/user")).andExpect(status().isForbidden());
	}

}

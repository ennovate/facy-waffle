package com.ennovate.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ResourceController {

    @RequestMapping("/user")
    public Principal authenticated(Principal principal)
    {
        return principal;
    }

}

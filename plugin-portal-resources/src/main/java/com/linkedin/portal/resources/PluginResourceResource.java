package com.linkedin.portal.resources;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.io.IOException;

@Service
@RequestMapping("/api/v1/resource")
public class PluginResourceResource {

    @RequestMapping(method = RequestMethod.GET)
    public String getRepos(Model model) throws IOException {
        UriComponents build = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/")
                .build();
        model.addAttribute("path", build.toUriString());
        return "/settings_gradle.txt";
    }
}

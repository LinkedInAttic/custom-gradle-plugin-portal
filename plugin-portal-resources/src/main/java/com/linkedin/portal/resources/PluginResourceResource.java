/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.resources;

import com.linkedin.portal.model.RepositoryType;
import com.linkedin.portal.resources.dao.repository.ArtifactRepositoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestMapping("/api/v1/resource")
public class PluginResourceResource {

    private static final Logger LOG = LoggerFactory.getLogger(PluginResourceResource.class);

    @Autowired
    private ArtifactRepositoryRepository artifactRepositoryRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @RequestMapping(method = RequestMethod.GET)
    public String getRepos(Model model) throws IOException {
        UriComponents build = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/")
                .build();

        List<String> repoList = artifactRepositoryRepository.findAll().stream().map(repo -> {
            Context context = new Context();
            context.setVariable("path", repo.getUrl());
            context.setVariable("ivyPath", repo.getIvy());
            context.setVariable("artifactPath", repo.getArtifact());
            context.setVariable("m2compatible", repo.isM2Compatible());

            if (RepositoryType.IVY.name().equals(repo.getType())) {
                if (repo.getArtifact() == null) {
                    return templateEngine.process("/ivy.txt", context);
                } else {
                    return templateEngine.process("/ivy_pattern.txt", context);
                }
            } else if (RepositoryType.MAVEN.name().equals(repo.getType())) {
                return templateEngine.process("/maven.txt", context);
            } else {
                return "";
            }
        }).collect(Collectors.toList());

        LOG.trace("Available repos: {}", repoList);

        model.addAttribute("path", build.toUriString());
        model.addAttribute("repos", repoList);
        return "/settings_gradle.txt";
    }
}

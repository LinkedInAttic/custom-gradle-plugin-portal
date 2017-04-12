package com.linkedin.portal.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedin.portal.model.PluginIdContainer;
import com.linkedin.portal.model.PluginManifest;
import com.linkedin.portal.model.PluginVersion;
import com.linkedin.portal.model.RepositoryDefinitions;
import com.linkedin.portal.model.RepositoryType;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.plugin.management.PluginResolutionStrategy;
import org.gradle.plugin.repository.PluginRepositoriesSpec;
import org.gradle.plugin.use.PluginId;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurePluginRepositories implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {
        String pluginHost = getPortalHost(settings);

        PluginManifest pluginManifest = getAvailablePlugins(pluginHost);


        configureKnownPlugins(settings.getPluginManagement().getResolutionStrategy(), pluginManifest.getPlugins());
        configureRepositories(settings.getPluginManagement().getRepositories(), pluginManifest.getRepos());
    }

    private void configureKnownPlugins(PluginResolutionStrategy resolutionStrategy, Map<String, PluginIdContainer> plugins) {
        resolutionStrategy.eachPlugin(details -> {
            PluginId id = details.getRequested().getId();
            String requestedVersion = details.getRequested().getVersion();

            System.out.println("requested version: " + requestedVersion);



            if (plugins.containsKey(id.getId())) {

                PluginIdContainer pluginIdContainer = plugins.get(id.getId());
                if (pluginIdContainer.getVersions().containsKey(requestedVersion)) {
                    PluginVersion pluginVersion = pluginIdContainer.getVersions().get(requestedVersion);
                    details.useModule(pluginVersion.getDependencyNotation());
                } else if (null == requestedVersion) {
                    details.useVersion(pluginIdContainer.getDefaultVersion());
                }
            }
        });
    }

    private void configureRepositories(PluginRepositoriesSpec pluginRepositories, List<RepositoryDefinitions> repos) {
        for (RepositoryDefinitions repositoryDefinitions : repos) {

            if (repositoryDefinitions.getType() == RepositoryType.GRADLE_PORTAL) {
                pluginRepositories.gradlePluginPortal();
            } else if (repositoryDefinitions.getType() == RepositoryType.MAVEN) {
                pluginRepositories.maven(repo -> repo.setUrl(repositoryDefinitions.getUrl()));
            } else if (repositoryDefinitions.getType() == RepositoryType.IVY) {
                pluginRepositories.ivy(ivy -> {
                    ivy.setUrl(repositoryDefinitions.getUrl());

//                    IvyLayout ivyLayout = repositoryDefinitions.getIvyLayout();
//                    if (ivyLayout != null) {
//                        ((IvyArtifactRepository) ivy).layout("pattern", layout -> {
//                            ((IvyPatternRepositoryLayout) layout).ivy(ivyLayout.getIvy());
//                            ((IvyPatternRepositoryLayout) layout).artifact(ivyLayout.getArtifact());
//                            ((IvyPatternRepositoryLayout) layout).setM2compatible(ivyLayout.isM2compatible());
//                        });
//                    }
                });
            }
        }
    }

    private PluginManifest getAvailablePlugins(String pluginHost) {
        try {
            Response response = Request.Get(pluginHost + "/api/v1/manifest")
                    .connectTimeout(1000)
                    .socketTimeout(1000)
                    .execute();

            String pluginJson = response.returnContent().asString();
            return new ObjectMapper().readValue(pluginJson, PluginManifest.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String getPortalHost(Settings settings) {
        Map<String, String> projectProperties = settings.getGradle().getStartParameter().getProjectProperties();

        if (projectProperties.containsKey("plugin.portal.host")) {
            return projectProperties.get("plugin.portal.host");
        }

        throw new GradleException("Property plugin.portal.host is unset!");
    }


}

package com.linkedin.portal.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedin.portal.model.IvyLayout;
import com.linkedin.portal.model.PluginIdContainer;
import com.linkedin.portal.model.PluginManifest;
import com.linkedin.portal.model.PluginVersion;
import com.linkedin.portal.model.RepositoryDefinition;
import com.linkedin.portal.model.RepositoryType;
import groovy.util.Eval;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.repositories.IvyPatternRepositoryLayout;
import org.gradle.api.initialization.Settings;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.plugin.management.PluginResolutionStrategy;
import org.gradle.plugin.repository.PluginRepositoriesSpec;
import org.gradle.plugin.use.PluginId;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class ConfigurePluginRepositories implements Plugin<Settings> {

    private static final Logger LOG = Logging.getLogger(ConfigurePluginRepositories.class);

    @Override
    public void apply(Settings settings) {
        String pluginHost = getPortalHost(settings);

        PluginManifest pluginManifest = getAvailablePlugins(settings, pluginHost);

        try {
            configureKnownPlugins(settings.getPluginManagement().getResolutionStrategy(), pluginManifest.getPlugins());
            configureRepositories(settings.getPluginManagement().getRepositories(), pluginManifest.getRepos());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void configureKnownPlugins(PluginResolutionStrategy resolutionStrategy, Map<String, PluginIdContainer> plugins) {
        resolutionStrategy.eachPlugin(details -> {
            PluginId id = details.getRequested().getId();
            String requestedVersion = details.getRequested().getVersion();

            LOG.info("Plugin {} requested version {}", id.getName(), requestedVersion);

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

    /**
     * Add all the defined repositories into Gradle to get settings.
     * @throws MalformedURLException when the URL is malformed.
     */
    private void configureRepositories(PluginRepositoriesSpec pluginRepositories, List<RepositoryDefinition> repos) throws MalformedURLException {
        for (RepositoryDefinition repositoryDefinition : repos) {

            if (repositoryDefinition.getType() == RepositoryType.GRADLE_PORTAL) {
                pluginRepositories.gradlePluginPortal();

            } else if (repositoryDefinition.getType() == RepositoryType.MAVEN) {
                pluginRepositories.maven(repo -> repo.setUrl(evaluateRepositoryUrl(repositoryDefinition)));

            } else if (repositoryDefinition.getType() == RepositoryType.IVY) {
                pluginRepositories.ivy(repo -> {
                    repo.setUrl(evaluateRepositoryUrl(repositoryDefinition));
                    if (repositoryDefinition.getIvyLayout() != null) {
                        repo.layout("pattern", config -> {
                            IvyLayout ivyLayout = repositoryDefinition.getIvyLayout();
                            IvyPatternRepositoryLayout layout = (IvyPatternRepositoryLayout) config;
                            layout.setM2compatible(ivyLayout.isM2compatible());
                            layout.ivy(ivyLayout.getIvy());
                            layout.artifact(ivyLayout.getArtifact());
                        });
                    }
                });
            }
        }
    }

    /**
     * Given a repositoryDefinition, evaluate any expression that may be in the String.
     * @param repositoryDefinition definition
     * @return String of the evaluated expression or null.
     */
    private String evaluateRepositoryUrl(RepositoryDefinition repositoryDefinition) {
        String repositoryDefinitionsUrl = repositoryDefinition.getUrl();
        if (repositoryDefinitionsUrl != null) {
            repositoryDefinitionsUrl = Eval.me("\"" + repositoryDefinitionsUrl + "\"").toString();
        }

        return repositoryDefinitionsUrl;
    }

    /**
     * Download the manifest, store it into a local cache, then parse it.
     */
    private PluginManifest getAvailablePlugins(Settings settings, String pluginHost) {
        File pluginManifest = new File(settings.getRootDir(), ".gradle/plugin-manifest");
        String pluginJson = null;
        if (pluginManifest.exists()) {
            try {
                pluginJson = new String(Files.readAllBytes(pluginManifest.toPath()), Charset.defaultCharset());
            } catch (IOException e) {
                LOG.warn("Unable to read {}", pluginManifest);
            }
        }

        if (!settings.getStartParameter().isOffline()) {
            try {
                Response response = Request.Get(pluginHost + "/api/v1/manifest")
                        .connectTimeout(1000)
                        .socketTimeout(1000)
                        .execute();

                pluginJson = response.returnContent().asString();

                try {
                    Files.write(pluginManifest.toPath(), pluginJson.getBytes());
                } catch (IOException e) {
                    LOG.info("Unable to write cache");
                }

            } catch (IOException e) {
                LOG.warn("Unable to communicate with plugin portal, falling back to cache");
            }
        }

        if (pluginJson == null) {
            throw new GradleException("Unable to retrieve plugin manifests");
        }

        try {
            return new ObjectMapper().readValue(pluginJson, PluginManifest.class);
        } catch (IOException e) {
            throw new GradleException("Unable to parse json", e);
        }
    }

    /**
     * @return The plugin portal host.
     */
    private String getPortalHost(Settings settings) {
        Map<String, String> projectProperties = settings.getGradle().getStartParameter().getProjectProperties();

        if (projectProperties.containsKey("plugin.portal.host")) {
            return projectProperties.get("plugin.portal.host");
        }

        throw new GradleException("Property plugin.portal.host is unset!");
    }


}

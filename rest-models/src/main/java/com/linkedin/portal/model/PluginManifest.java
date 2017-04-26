package com.linkedin.portal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class PluginManifest {

    private static final String PLUGINS = "plugins";
    private static final String REPOS = "repos";

    private final Map<String, PluginIdContainer> plugins;
    private final List<RepositoryDefinition> repos;

    public PluginManifest(@JsonProperty(value = PLUGINS, required = true) Map<String, PluginIdContainer> plugins,
                          @JsonProperty(value = REPOS, required = true) List<RepositoryDefinition> repos) {
        this.plugins = plugins;
        this.repos = repos;
    }

    @JsonProperty(PLUGINS)
    public Map<String, PluginIdContainer> getPlugins() {
        return plugins;
    }

    @JsonProperty(REPOS)
    public List<RepositoryDefinition> getRepos() {
        return repos;
    }
}

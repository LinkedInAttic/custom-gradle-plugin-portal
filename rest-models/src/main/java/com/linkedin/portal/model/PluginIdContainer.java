package com.linkedin.portal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class PluginIdContainer {

    private static final String VERSIONS = "versions";
    private static final String PLUGIN_ID = "pluginId";
    private static final String DEFAULT_VERSION = "defaultVersion";

    private final Map<String, PluginVersion> versionMap;
    private final String pluginId;
    private final String defaultVersion;

    @JsonCreator
    public PluginIdContainer(@JsonProperty(value = VERSIONS, required = true) Map<String, PluginVersion> versionMap,
                             @JsonProperty(value = PLUGIN_ID, required = true) String pluginId,
                             @JsonProperty(value = DEFAULT_VERSION, required = true) String defaultVersion) {
        this.versionMap = versionMap;
        this.pluginId = pluginId;
        this.defaultVersion = defaultVersion;
    }

    @JsonProperty(VERSIONS)
    public Map<String, PluginVersion> getVersions() {
        return versionMap;
    }

    @JsonProperty(PLUGIN_ID)
    public String getPluginId() {
        return pluginId;
    }

    @JsonProperty(DEFAULT_VERSION)
    public String getDefaultVersion() {
        return defaultVersion;
    }
}

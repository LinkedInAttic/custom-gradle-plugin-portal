/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.plugin;

import com.linkedin.portal.model.PluginManifest;
import com.linkedin.portal.plugin.internal.ManifestRetriever;
import com.linkedin.portal.plugin.internal.RepositoryConfigurationAction;
import com.linkedin.portal.plugin.internal.ResolutionStrategyAction;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

import java.util.Map;

public class ConfigurePluginRepositories implements Plugin<Settings> {

    @Override
    public void apply(Settings settings) {
        String pluginHost = getPortalHost(settings);

        PluginManifest pluginManifest = new ManifestRetriever(settings, pluginHost).getAvailablePlugins();

        settings.getPluginManagement().resolutionStrategy(new ResolutionStrategyAction(pluginManifest));
        settings.getPluginManagement().repositories(new RepositoryConfigurationAction(pluginManifest));
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

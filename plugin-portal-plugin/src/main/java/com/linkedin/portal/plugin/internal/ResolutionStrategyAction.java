/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.plugin.internal;

import com.linkedin.portal.model.PluginIdContainer;
import com.linkedin.portal.model.PluginManifest;
import com.linkedin.portal.model.PluginVersion;
import org.gradle.api.Action;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.plugin.management.PluginResolutionStrategy;
import org.gradle.plugin.use.PluginId;

import java.util.Map;

/**
 * Adds a callback via the {@link PluginResolutionStrategy#eachPlugin(Action)} call to listen to when a plugin is
 * applied, then if it's one of the plugins provided by the {@link PluginManifest}, add extra details about
 * the plugin. These details may either be
 * <ul>
 *     <li>Version of the plugin to apply</li>
 *     <li>Coordinates for the jar containing the plugin</li>
 * </ul>
 */
public class ResolutionStrategyAction implements Action<PluginResolutionStrategy> {

    private static final Logger LOG = Logging.getLogger(ResolutionStrategyAction.class);

    private final Map<String, PluginIdContainer> plugins;

    public ResolutionStrategyAction(PluginManifest pluginManifest) {
        this.plugins = pluginManifest.getPlugins();
    }

    @Override
    public void execute(PluginResolutionStrategy resolutionStrategy) {
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
}

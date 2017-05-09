/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.plugin.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedin.portal.model.PluginManifest;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.gradle.api.GradleException;
import org.gradle.api.initialization.Settings;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class ManifestRetriever {

    private static final Logger LOG = Logging.getLogger(ManifestRetriever.class);
    private static final int ONE_SECOND_TIMEOUT = 1000;

    private final Settings settings;
    private final String pluginHost;

    public ManifestRetriever(Settings settings, String pluginHost) {
        this.settings = settings;
        this.pluginHost = pluginHost;
    }

    /**
     * Download the manifest, store it into a local cache, then parse it.
     */
    public PluginManifest getAvailablePlugins() {
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
                        .connectTimeout(ONE_SECOND_TIMEOUT)
                        .socketTimeout(ONE_SECOND_TIMEOUT)
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
}

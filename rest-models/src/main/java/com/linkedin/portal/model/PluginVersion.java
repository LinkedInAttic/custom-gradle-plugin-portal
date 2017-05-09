/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class PluginVersion {

    private static final String VERSION = "version";
    private static final String DEPENDENCY_NOTATION = "dependencyNotation";

    private final String version;
    private final Map<String, String> dependencyNotation;

    @JsonCreator
    public PluginVersion(@JsonProperty(value = VERSION, required = true) String version,
                         @JsonProperty(value = DEPENDENCY_NOTATION, required = true) Map<String, String> dependencyNotation) {
        this.version = version;
        this.dependencyNotation = dependencyNotation;
    }

    @JsonProperty(VERSION)
    public String getVersion() {
        return version;
    }

    @JsonProperty(DEPENDENCY_NOTATION)
    public Map<String, String> getDependencyNotation() {
        return dependencyNotation;
    }
}

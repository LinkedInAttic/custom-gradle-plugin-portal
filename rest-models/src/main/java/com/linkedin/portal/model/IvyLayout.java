/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IvyLayout {

    private static final String IVY = "ivy";
    private static final String ARTIFACT = "artifact";
    private static final String M2_COMPATIBLE = "m2compatible";

    private final String ivy;
    private final String artifact;
    private final boolean m2compatible;

    public IvyLayout(@JsonProperty(value = IVY, required = true) String ivy,
                     @JsonProperty(value = ARTIFACT, required = true) String artifact,
                     @JsonProperty(value = M2_COMPATIBLE, defaultValue = "false") Boolean m2compatible) {
        this.ivy = ivy;
        this.artifact = artifact;
        this.m2compatible = m2compatible;
    }

    @JsonProperty(IVY)
    public String getIvy() {
        return ivy;
    }

    @JsonProperty(ARTIFACT)
    public String getArtifact() {
        return artifact;
    }

    @JsonProperty(M2_COMPATIBLE)
    public boolean isM2compatible() {
        return m2compatible;
    }
}

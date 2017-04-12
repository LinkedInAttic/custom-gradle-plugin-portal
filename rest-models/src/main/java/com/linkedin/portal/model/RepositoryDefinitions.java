package com.linkedin.portal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepositoryDefinitions {

    private static final String TYPE = "type";
    private static final String URL = "url";
    private static final String LAYOUT = "layout";

    private final RepositoryType type;
    private final String url;
    private final IvyLayout ivyLayout;

    @JsonCreator
    public RepositoryDefinitions(@JsonProperty(value = TYPE, required = true) RepositoryType type,
                                 @JsonProperty(value = URL) String url,
                                 @JsonProperty(LAYOUT) IvyLayout ivyLayout) {
        this.type = type;
        this.url = url;
        this.ivyLayout = ivyLayout;
    }

    public RepositoryDefinitions(RepositoryType type, String url) {
        this(type, url, null);
    }

    @JsonProperty(TYPE)
    public RepositoryType getType() {
        return type;
    }

    @JsonProperty(URL)
    public String getUrl() {
        return url;
    }

    @JsonProperty(LAYOUT)
    public IvyLayout getIvyLayout() {
        return ivyLayout;
    }

}

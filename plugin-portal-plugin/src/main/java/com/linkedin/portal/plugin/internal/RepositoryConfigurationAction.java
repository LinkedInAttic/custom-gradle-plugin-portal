package com.linkedin.portal.plugin.internal;

import com.linkedin.portal.model.IvyLayout;
import com.linkedin.portal.model.PluginManifest;
import com.linkedin.portal.model.RepositoryDefinition;
import com.linkedin.portal.model.RepositoryType;
import groovy.util.Eval;
import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.IvyPatternRepositoryLayout;
import org.gradle.plugin.repository.PluginRepositoriesSpec;

import java.util.List;

/**
 * Adds repositories defined in a {@link PluginManifest} to an existing project.
 */
public class RepositoryConfigurationAction implements Action<PluginRepositoriesSpec> {

    private final List<RepositoryDefinition> repos;

    public RepositoryConfigurationAction(PluginManifest pluginManifest) {
        this.repos = pluginManifest.getRepos();
    }

    /**
     * Add all the defined repositories into Gradle to get settings.
     */
    @Override
    public void execute(PluginRepositoriesSpec pluginRepositories) {
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
}

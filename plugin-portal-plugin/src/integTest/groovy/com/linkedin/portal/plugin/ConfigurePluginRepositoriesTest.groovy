package com.linkedin.portal.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.linkedin.portal.model.PluginIdContainer
import com.linkedin.portal.model.PluginManifest
import com.linkedin.portal.model.PluginVersion
import com.linkedin.portal.model.RepositoryDefinition
import com.linkedin.portal.model.RepositoryType
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

class ConfigurePluginRepositoriesTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    @Rule
    WireMockRule wireMockRule = new WireMockRule(0)

    def 'can specify plugin to apply'() {
        given:
        def repoFolder = temporaryFolder.newFolder("repo")
        def pluginBuilder = new PluginBuilder(temporaryFolder.newFolder('plugin-project'))
        def projectDir = temporaryFolder.newFolder('test-project')

        buildPlugin(pluginBuilder, repoFolder)
        configureProjectFiles(projectDir)

        PluginManifest manifest = createManifest(repoFolder)
        def response = new ObjectMapper().writeValueAsString(manifest)
        wireMockRule
                .stubFor(get(urlEqualTo('/api/v1/manifest'))
                .willReturn(aResponse().withBody(response)))
        println repoFolder.listFiles()

        when:
        def result = GradleRunner.create().withProjectDir(projectDir).withArguments( "-Pplugin.portal.host=http://localhost:${wireMockRule.port()}/", "hello").build()

        then:
        result.output.contains('world')
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private PluginManifest createManifest(File repoFolder) {
        Map<String, PluginIdContainer> plugins = new HashMap<>()
        def pluginVersion = new PluginVersion('1.2.3', [group: 'com', name: 'foo', version: '1.2.3'])
        plugins['test-plugin'] = new PluginIdContainer(['1.2.3': pluginVersion], 'test-plugin', null)

        List<RepositoryDefinition> repos = new ArrayList<>()
        repos.add(new RepositoryDefinition(RepositoryType.MAVEN, repoFolder.absolutePath))
        return new PluginManifest(plugins, repos)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void configureProjectFiles(File projectDir) {
        new File(projectDir, 'settings.gradle').text = """
buildscript {
    dependencies {
        classpath files("${System.getProperty("plugin.jar.file")}")
        classpath files('${System.getProperty("jar.dependencies").split(":").join("', '")}')
    }
}
apply plugin: com.linkedin.portal.plugin.ConfigurePluginRepositories
"""
        new File(projectDir, 'build.gradle').text = """
plugins {
    id 'test-plugin' version '1.2.3'
}
"""
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void buildPlugin(PluginBuilder pluginBuilder, File repoFolder) {
        pluginBuilder.addPluginWithPrintlnTask('hello', 'world')
        pluginBuilder.publishTo('com:foo:1.2.3', repoFolder)
    }
}

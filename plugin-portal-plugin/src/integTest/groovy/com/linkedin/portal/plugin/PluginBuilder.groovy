/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.portal.plugin

import com.google.common.base.Splitter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.TextUtil

/**
 * Taken from github.com/gradle/gradle
 */
class PluginBuilder {

    final File projectDir

    String packageName = "org.gradle.test"

    final Map<String, String> pluginIds = [:]

    PluginBuilder(File projectDir) {
        this.projectDir = projectDir
    }

    File file(String path) {
        def file = new File(projectDir, path)
        file.parentFile.mkdirs()
        return file
    }

    File groovy(String path) {
        file("src/main/groovy/${packageName.replaceAll("\\.", "/")}/$path")
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    String generateManagedBuildScript() {
        """
            apply plugin: "groovy"
            dependencies {
              compile localGroovy()
              compile gradleApi()
            }
        """
    }

    String generateBuildScript(String additions = "") {
        file("build.gradle").text = (generateManagedBuildScript() + additions)
    }

    void publishTo(File testFile) {
        generateBuildScript """
            jar {
                archiveName = "$testFile.name"
                destinationDir = file("${TextUtil.escapeString(testFile.parentFile.absolutePath)}")
            }
        """

        writePluginDescriptors(pluginIds)
        GradleRunner.create().withProjectDir(projectDir).withArguments("jar").build()
    }

    void publishTo(String coordinates, File mavenDirectory) {
        List<String> omr = Splitter.on(":").splitToList(coordinates)
        generateBuildScript """
            apply plugin: 'maven-publish'
            
            publishing {
                publications {
                    mavenJava(MavenPublication) {
                        from components.java
                        groupId '${omr[0]}'
                        artifactId '${omr[1]}'
                        version '${omr[2]}'
                    }
                }
                repositories {
                    maven {
                        // change to point to your repo, e.g. http://my.org/repo
                        url "${mavenDirectory.absolutePath}"
                    }
                }
            }
        """

        writePluginDescriptors(pluginIds)
        GradleRunner.create().withProjectDir(projectDir).withArguments("publish").build()
    }

    void generateForBuildSrc() {
        generateBuildScript()
        writePluginDescriptors(pluginIds)
    }

    protected void writePluginDescriptors(Map<String, String> pluginIds) {
        descriptorsDir.deleteDir()
        descriptorsDir.mkdirs()
        pluginIds.each { id, className ->
            new File(descriptorsDir, "${id}.properties").text = "implementation-class=${packageName}.${className}"
        }
    }

    File getDescriptorsDir() {
        file("src/main/resources/META-INF/gradle-plugins")
    }

    private addPluginSource(String id, String className, String impl) {
        pluginIds[id] = className

        groovy("${className}.groovy") << impl
    }

    PluginBuilder addPlugin(String impl, String id = "test-plugin", String className = "TestPlugin") {
        addPluginSource(id, className, """
            package $packageName

            class $className implements $Plugin.name<$Project.name> {
                void apply($Project.name project) {
                    $impl
                }
            }
        """)
        this
    }

    PluginBuilder addPluginWithPrintlnTask(String taskName, String message, String id = "test-plugin", String className = "TestPlugin") {
        addPlugin("project.task(\"$taskName\") { doLast { println \"$message\" } }", id, className)
        this
    }
}

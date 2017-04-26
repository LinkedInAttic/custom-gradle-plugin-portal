# Gradle Plugin Portal

## Why would you want a plugin portal?

Inside a company using Gradle, there can be an explosion of plugins. There are a few ways to manage them, but it can 
be hard find useful peaces of information about the available plugins:

- What plugins are available?
- Where can I find docs about a plugin?
- What versions of the plugin should I use?

This plugin portal aims to solve all of these issues.

## What does it do?

This project consists of two parts: 

1. A service that provides a 'manifest' of plugins and repositories.
2. A plugin to make gradle consume the services API and provide a way to resolve plugins though the service.

## How does it work?

First you need to make Gradle aware of the plugins. To do this you can add something like (changing the hostname as required) 

```
apply from: 'http://localhost:8080/api/v1/resource'
```

To the top of your `settings.gradle`. Curling this address will return something like 

```
buildscript {
  repositories {
      
ivy {
    url "${System.getProperty("user.home")}/local-repo"
    layout 'pattern', {
      ivy '[organisation]/[module]/[revision]/[module]-[revision].ivy'
      artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
      m2compatible = true
    }
}
maven {
    url "http://repo.example.com/m2"
}
      
    }
    dependencies {
      classpath 'com.linkedin.gradle.portal:plugin-portal-plugin:+'
    }
}

gradle.getStartParameter().getProjectProperties()['plugin.portal.host'] = 'http://localhost:8080/'

apply plugin: com.linkedin.portal.plugin.ConfigurePluginRepositories
```

That blob configures some repositories (the same one the service will serve), configures a property, then applies a 
plugin (located in the `plugin-portal-plugin` module).

The `ConfigurePluginRepositories` plugin, when applied, will hit `<host>/api/v1/manifest` to get the listing of all
plugins and repositories. This way only one request is needed, and it is easy to cache.

The plugin then registers a [eachPlugin](https://docs.gradle.org/current/javadoc/org/gradle/plugin/management/PluginResolutionStrategy.html#eachPlugin(org.gradle.api.Action)) callback that will inform Gradle about the coordinates of plugins that manifest contains.

When Gradle exits the initialization phase, the `plugins {}` will then be able to use plugins provided by the service.

## What Endpoints Exist?

```
| Method |                 Endpoint                | Auth Required |
|:------:|:---------------------------------------:|:-------------:|
|   GET  |             /api/v1/manifest            |       NO      |
|   GET  |             /api/v1/resource            |       NO      |
|   GET  |             /api/v1/resource            |       NO      |
|   GET  |         /api/v1/manifest/plugins        |       NO      |
|  POST  |         /api/v1/manifest/plugins        |       YES     |
|   GET  |      /api/v1/manifest/plugins/{id}      |       NO      |
| DELETE | /api/v1/manifest/plugins/{id}/{version} |       YES     |
|  POST  |      /api/v1/manifest/plugins/{id}      |       YES     |
|   GET  | /api/v1/manifest/plugins/{id}/{version} |       NO      |
|   GET  |       /api/v1/manifest/repository       |       NO      |
|  POST  |       /api/v1/manifest/repository       |       YES     |
|   GET  |     /api/v1/manifest/repository/{id}    |       NO      |
| DELETE |     /api/v1/manifest/repository/{id}    |       YES     |
```
# Gradle Plugin Portal

## Why would you want a plugin portal?

Inside a company using Gradle, there can be an explosion of plugins. There are a few ways to manage them, but it can
be hard to find useful pieces of information about the available plugins:

- What plugins are available?
- Where can I find docs about a plugin?
- What versions of the plugin should I use?

This plugin portal aims to solve all of these issues.

## What does it do?

This project consists of two parts:

1. A service that provides a 'manifest' of plugins and repositories.
2. A plugin to make gradle consume the services API and provide a way to resolve plugins though the service.

## How does it work?

First you need to make Gradle aware of the plugins. To do this you can add something like (changing the URL are appropriate). To the top of your `settings.gradle`.

```
apply from: 'http://localhost:8080/api/v1/resource'
```

Curling this address will return something like

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

The `buildscript.repositories` entry is generated from repositories that are defined in `/api/v1/manifest/repository`.

The `gradle.getStartParameter().getProjectProperties()['plugin.portal.host'] = 'http://localhost:8080/'` line is also auto
generated. The URL is based on the URL used in your settings.gradle file. This allows the plugin that gets applied to
know which host to talk to.

The above code snippet configures some repositories (the same one the service will serve), configures a property, then applies a
plugin (located in the `plugin-portal-plugin` module).

### `ConfigurePluginRepositories` Plugin

The `ConfigurePluginRepositories` plugin, when applied, will hit `<host>/api/v1/manifest` to get the listing of all
plugins and repositories. This will generate a single request, and will be cached when offline.

After the request has been made, the repositories contained in the manifest will be registers. To see how this is done
check out `com.linkedin.portal.plugin.internal.RepositoryConfigurationAction`. When the repositories are registered
then plugin information will be provided via the `com.linkedin.portal.plugin.internal.ResolutionStrategyAction` class.

For more details on how either of these classes work, please review their respective documentation.

When Gradle exits the initialization phase, the `plugins {}` will then be able to use plugins registered with the service.

### What Endpoints Exist?

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
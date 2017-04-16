package com.linkedin.portal.resources;

import com.linkedin.portal.model.PluginIdContainer;
import com.linkedin.portal.model.PluginVersion;
import com.linkedin.portal.resources.dao.entity.PluginEntity;
import com.linkedin.portal.resources.dao.entity.PluginVersionEntity;
import com.linkedin.portal.resources.dao.repository.PluginRepository;
import com.linkedin.portal.resources.dao.repository.VersionRepository;
import com.linkedin.portal.resources.transform.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequestMapping("/api/v1/manifest/plugins")
public class PluginsResource {

    @Autowired
    private PluginRepository pluginRepository;

    @Autowired
    private VersionRepository versionRepository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<String, PluginIdContainer>> getPlugins() {
        Map<String, PluginIdContainer> collect = pluginRepository.findAll().stream()
                .map(Transformer::fromPluginEntity)
                .collect(Collectors.toMap(PluginIdContainer::getPluginId, item -> item));

        return ResponseEntity.ok(collect);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> createPlugin(@RequestBody PluginIdContainer pluginIdContainer) {
        PluginEntity pluginEntity = new PluginEntity(pluginIdContainer.getPluginId(), pluginIdContainer.getDefaultVersion(), new ArrayList<>());
        pluginEntity = pluginRepository.save(pluginEntity);

        for (PluginVersion pluginVersion : pluginIdContainer.getVersions().values()) {
            pluginEntity.getVersions().add(Transformer.fromPluginVersion(pluginEntity, pluginVersion));
        }

        pluginRepository.save(pluginEntity);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(pluginIdContainer.getPluginId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @RequestMapping("/{id:.+}")
    public ResponseEntity<PluginIdContainer> getPlugin(@PathVariable("id") String id) {
        PluginEntity plugin = pluginRepository.findByPluginNameEquals(id);

        if (null == plugin) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(Transformer.fromPluginEntity(plugin));
    }

    @RequestMapping(value = "/{id:.+}/{version:.+}", method = RequestMethod.GET)
    public ResponseEntity<PluginVersion> getVersion(@PathVariable("id") String id,
                                                    @PathVariable("version") String version) {
        PluginEntity plugin = pluginRepository.findByPluginNameEquals(id);

        if (null == plugin) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PluginVersionEntity versionEntity = versionRepository.findByPluginEntityAndPluginVersionEquals(plugin, version);
        if (null == versionEntity) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(Transformer.fromPluginVersionEntity(versionEntity));
    }

    @RequestMapping(value = "/{id:.+}/{version:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteVersion(@PathVariable("id") String id,
                                              @PathVariable("version") String version) {
        PluginEntity plugin = pluginRepository.findByPluginNameEquals(id);

        if (null == plugin) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PluginVersionEntity versionEntity = versionRepository.findByPluginEntityAndPluginVersionEquals(plugin, version);
        if (null == versionEntity) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        plugin.getVersions().remove(versionEntity);
        pluginRepository.save(plugin);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id:.+}", method = RequestMethod.POST)
    public ResponseEntity<Void> addVersion(@PathVariable("id") String id,
                                           @RequestBody PluginVersion version) {
        PluginEntity plugin = pluginRepository.findByPluginNameEquals(id);

        if (null == plugin) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PluginVersionEntity versionEntity = Transformer.fromPluginVersion(plugin, version);
        plugin.getVersions().add(versionEntity);
        pluginRepository.save(plugin);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(version.getVersion()).toUri();

        return ResponseEntity.created(uri).build();
    }
}

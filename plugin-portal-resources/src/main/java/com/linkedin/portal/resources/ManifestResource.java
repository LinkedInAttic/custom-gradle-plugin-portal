package com.linkedin.portal.resources;

import com.linkedin.portal.model.PluginIdContainer;
import com.linkedin.portal.model.PluginManifest;
import com.linkedin.portal.model.RepositoryDefinition;
import com.linkedin.portal.resources.dao.repository.ArtifactRepositoryRepository;
import com.linkedin.portal.resources.dao.repository.PluginRepository;
import com.linkedin.portal.resources.transform.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequestMapping("/api/v1/manifest")
public class ManifestResource {

    @Autowired
    private PluginRepository pluginRepository;

    @Autowired
    private ArtifactRepositoryRepository artifactRepositoryRepository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PluginManifest> getManifest() {
        Map<String, PluginIdContainer> collect = pluginRepository.findAll().stream()
                .map(Transformer::fromPluginEntity)
                .collect(Collectors.toMap(PluginIdContainer::getPluginId, item -> item));

        List<RepositoryDefinition> repos = artifactRepositoryRepository.findAll().stream()
                .map(Transformer::fromRepositoryEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PluginManifest(collect, repos));
    }
}

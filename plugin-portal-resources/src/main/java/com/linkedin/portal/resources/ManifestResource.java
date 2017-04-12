package com.linkedin.portal.resources;

import com.linkedin.portal.model.PluginIdContainer;
import com.linkedin.portal.model.PluginManifest;
import com.linkedin.portal.model.PluginVersion;
import com.linkedin.portal.model.RepositoryDefinitions;
import com.linkedin.portal.resources.dao.entity.PluginEntity;
import com.linkedin.portal.resources.dao.entity.PluginVersionEntity;
import com.linkedin.portal.resources.dao.repository.ArtifactRepositoryRepository;
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

        List<RepositoryDefinitions> repos = artifactRepositoryRepository.findAll().stream()
                .map(Transformer::fromRepositoryEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PluginManifest(collect, repos));
    }
}

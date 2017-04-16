package com.linkedin.portal.resources;

import com.linkedin.portal.model.RepositoryDefinitions;
import com.linkedin.portal.resources.dao.repository.ArtifactRepositoryRepository;
import com.linkedin.portal.resources.transform.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestMapping("/api/v1/manifest/repository")
public class RepositoryResource {

    @Autowired
    private ArtifactRepositoryRepository artifactRepositoryRepository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> createRepo(@RequestBody RepositoryDefinitions definitions) {

        artifactRepositoryRepository.save(Transformer.fromRepositoryDefinitions(definitions));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<RepositoryDefinitions>> getRepos() {
        return ResponseEntity.ok(artifactRepositoryRepository.findAll().stream()
                .map(Transformer::fromRepositoryEntity)
                .collect(Collectors.toList()));
    }
}

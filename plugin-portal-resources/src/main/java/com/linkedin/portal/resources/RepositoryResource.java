/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.resources;

import com.linkedin.portal.model.RepositoryDefinition;
import com.linkedin.portal.resources.dao.entity.RepositoryEntity;
import com.linkedin.portal.resources.dao.repository.ArtifactRepositoryRepository;
import com.linkedin.portal.resources.transform.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequestMapping("/api/v1/manifest/repository")
public class RepositoryResource {

    @Autowired
    private ArtifactRepositoryRepository artifactRepositoryRepository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> createRepo(@RequestBody RepositoryDefinition definitions) {

        artifactRepositoryRepository.save(Transformer.fromRepositoryDefinitions(definitions));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<Long, RepositoryDefinition>> getRepos() {
        return ResponseEntity.ok(artifactRepositoryRepository.findAll().stream()
                .collect(Collectors.toMap(RepositoryEntity::getId, Transformer::fromRepositoryEntity)));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<RepositoryDefinition> getRepo(@PathVariable("id") Long id) {
        RepositoryEntity repo = artifactRepositoryRepository.getOne(id);

        if(repo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(Transformer.fromRepositoryEntity(repo));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepo(@PathVariable("id") Long id) {
        if(!artifactRepositoryRepository.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        artifactRepositoryRepository.delete(id);

        return ResponseEntity.ok().build();
    }
}

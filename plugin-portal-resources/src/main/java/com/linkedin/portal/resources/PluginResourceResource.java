package com.linkedin.portal.resources;

import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Service
@RequestMapping("/api/v1/resource")
public class PluginResourceResource {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> getRepos() throws IOException {
        InputStream resourceAsStream = PluginResourceResource.class.getClassLoader().getResourceAsStream("settings_gradle_blob.txt");
        return ResponseEntity.ok(IOUtils.toString(resourceAsStream, Charset.defaultCharset()));
    }
}

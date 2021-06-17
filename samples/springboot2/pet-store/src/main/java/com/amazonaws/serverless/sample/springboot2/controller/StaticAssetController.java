package com.amazonaws.serverless.sample.springboot2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
public class StaticAssetController {
    private static final Logger logger = LoggerFactory.getLogger(StaticAssetController.class);

    @RequestMapping(path = "/**", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getStaticAsset(HttpServletRequest request) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        String requestURI = request.getRequestURI();
        Resource r = resourceLoader.getResource("public" + requestURI);
        try (InputStream fileData = r.getInputStream()) {
            HttpHeaders headers = new HttpHeaders();
            if (requestURI.endsWith(".html")) {
                headers.setContentType(MediaType.TEXT_HTML);
            } else if (requestURI.endsWith(".js")) {
                headers.setContentType(MediaType.APPLICATION_JSON);
            } else if (requestURI.endsWith(".css")) {
                headers.setContentType(new MediaType("text", "css"));
            } else if (requestURI.endsWith(".png")) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else {
                // Default to text/plain.
                logger.warn("Defaulting to text/plain for " + requestURI);
                headers.setContentType(MediaType.TEXT_PLAIN);
            }
            byte[] bytes = fileData.readAllBytes();
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            // Assume that any error above is really a "not found" (404) error.
            byte[] bytes = ("Resource " + requestURI + " not found").getBytes(UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.NOT_FOUND);
        }
    }
}

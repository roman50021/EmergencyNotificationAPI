package com.fedkoroma.security.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EmailTemplateReader {

    public static String readEmailTemplate(String filePath, String name, String link) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return template.replace("{name}", name).replace("{link}", link);
    }
}

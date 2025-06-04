package gov.iti.jet.ewd.ecom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${spring.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the URL path /images/** to the file system path
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("file:" + uploadPath.toString() + "/");
    }
}
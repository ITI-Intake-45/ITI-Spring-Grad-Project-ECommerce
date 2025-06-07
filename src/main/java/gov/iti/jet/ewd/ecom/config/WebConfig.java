package gov.iti.jet.ewd.ecom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

   @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS to all endpoints
                .allowedOrigins("http://localhost:4200") // Allow requests from Angular frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Include OPTIONS for preflight requests
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow cookies/session (required for your session-based auth)
    }

}

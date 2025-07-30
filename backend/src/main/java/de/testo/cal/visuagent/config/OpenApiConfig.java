package de.testo.cal.visuagent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for API documentation.
 *
 * @author GitHub Copilot
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VisuAgent API")
                        .version("1.0.0")
                        .description("REST API for VisuAgent (Video stream, measurement extraction)")
                );
    }
}

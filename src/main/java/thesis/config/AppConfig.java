package thesis.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the application.
 * Scans for Spring components in the 'thesis' package.
 */
@Configuration
@ComponentScan(basePackages = "thesis")
public class AppConfig {
}


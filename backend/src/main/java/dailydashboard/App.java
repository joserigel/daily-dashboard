package dailydashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dailydashboard.Database.SQL;
import jakarta.annotation.PreDestroy;

@SpringBootApplication
public class App 
{
    static Logger logger = LoggerFactory.getLogger(App.class);

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }   
        };
    }

    public static void main( String[] args )
    {
        try {
            SQL.init();
            SpringApplication.run(App.class, args);
        } catch(Exception e) {
            logger.error(e.getMessage());
        }
    }

    @PreDestroy
    public static void destroy() {
        try {
            SQL.closeConnection();
            logger.info("Closed SQL Connection");
        } catch(Exception e) {
            logger.error(e.getMessage());
        }
    }
}

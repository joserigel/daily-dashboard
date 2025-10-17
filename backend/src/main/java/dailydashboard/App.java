package dailydashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dailydashboard.Database.SQL;
import jakarta.annotation.PreDestroy;

@SpringBootApplication
public class App 
{
    static Logger logger = LoggerFactory.getLogger(App.class);

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

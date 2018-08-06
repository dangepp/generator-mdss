package <%= config.packages.base %>;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    /**
    * Main method, used to run the application.
    *
    * @param args the command line arguments
    */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
package gov.iti.jet.ewd.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EntityScan("gov.iti.jet.ewd.ecom.entity") // Adjust package if needed
//@EnableJpaRepositories("gov.iti.jet.ewd.ecom.repository") // Adjust package if needed
@SpringBootApplication
public class EcomApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomApplication.class, args);
    }

}

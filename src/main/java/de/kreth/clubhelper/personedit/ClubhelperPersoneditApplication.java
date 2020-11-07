package de.kreth.clubhelper.personedit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableCaching
public class ClubhelperPersoneditApplication {

    public static void main(String[] args) {
	SpringApplication.run(ClubhelperPersoneditApplication.class, args);
    }

}

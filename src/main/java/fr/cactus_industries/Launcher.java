package fr.cactus_industries;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Launcher {
    
    public static void main(String[] args) {
        log.info("Lancement Spring...");
        SpringApplication.run(Launcher.class, args);
    }
    
}

package fr.cactus_industries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Launcher {
    
    public static void main(String[] args) {
        System.out.println("Lancement Spring...");
        SpringApplication.run(Launcher.class, args);
    }
    
}

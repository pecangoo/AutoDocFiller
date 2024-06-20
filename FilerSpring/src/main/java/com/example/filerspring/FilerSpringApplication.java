package com.example.filerspring;

import com.example.filerspring.constans.Routes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilerSpringApplication {

    public static void main(String[] args) {
        try {
            System.out.println("Argument: "  + args[0]);
            Routes.PATH_TO_ORIGIN_FORM = args[0];
        } catch (Exception ex){
            System.out.println("No path to origin template in args");
            System.exit(1);
        }
        SpringApplication.run(FilerSpringApplication.class, args);
    }

}

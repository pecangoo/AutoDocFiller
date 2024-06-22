package com.example.filerspring;

import com.example.filerspring.constans.Constans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FilerSpringApplication {

    public static void main(String[] args) {


        try {
            log.info("Argument: "  + args[0]);
            Constans.PATH_TO_ORIGIN_FORM = args[0];
            // TODO: Можно проверить путь на наличие файла
            // TODO: Можно проверить путь на корректность файла
        } catch (Exception ex){
            log.error("No path to origin template in args");
            Constans.PATH_TO_ORIGIN_FORM = "/Users/svetislavdobromirov/working/AutoDocFiller/FilerSpring/src/main/resources/static/standartTemplate.json";
            //System.exit(1);
        }
        SpringApplication.run(FilerSpringApplication.class, args);
    }

}

package com.example.filerspring.constans;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;


public class Constans {
    public static final String NAME_ORIGIN_FORM = "Стандартная форма";
    public static URL PATH_TO_ORIGIN_FORM;

    static {
        try {
            PATH_TO_ORIGIN_FORM = new ClassPathResource("static/" + "standardTemplate" + ".json").getURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

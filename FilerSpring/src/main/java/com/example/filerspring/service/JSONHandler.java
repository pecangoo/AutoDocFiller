package com.example.filerspring.service;

import com.example.filerspring.constans.Routes;
import com.example.filerspring.model.FormField;
import com.example.filerspring.model.ListFormField;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

//public class Types {
//    public static final String form_path = "data/form_path.json";
//    public static final String template_path = "data/templates.json";
//}

public class JSONHandler {
    private String file_name;
    private Gson gson;

    public JSONHandler() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public FormField[] readJsonToDict() throws IOException {



//        ClassLoader classLoader = getClass().getClassLoader();
//        URL resource = classLoader.getResource("static/standartTemplate.json");
//        String filePath  = "";
//        if (resource == null) {
//            throw new IllegalArgumentException("File not found!");
//        } else {
//            File file = new File(resource.getFile());
//            filePath = file.getAbsolutePath();
//
//            System.out.println("Path to file: " + filePath);
//        }

        String fName = Routes.PATH_TO_ORIGIN_FORM;
        try (FileReader reader = new FileReader(fName)) {
            FormField[] formDataDTOList =  gson.fromJson(reader, FormField[].class);
            return formDataDTOList;
           // System.out.println(formDataDTOList);
        }

    }

    public void writeDictToJson(JsonObject data) throws IOException {
        try (FileWriter writer = new FileWriter(this.file_name)) {
            writer.write(gson.toJson(data));
        }
    }
}

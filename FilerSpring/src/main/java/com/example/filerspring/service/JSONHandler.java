package com.example.filerspring.service;

import com.example.filerspring.constans.Constans;
import com.example.filerspring.model.FormField;
import com.example.filerspring.model.ListFormField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.service.invoker.UrlArgumentResolver;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

//public class Types {
//    public static final String form_path = "data/form_path.json";
//    public static final String template_path = "data/templates.json";
//}

@Service
@RequiredArgsConstructor
public class JSONHandler {
    private final ObjectMapper objectMapper;
    private String file_name;
   // private Gson gson;

//    public JSONHandler() {
//
//        this.gson = new GsonBuilder().setPrettyPrinting().create();
//    }

    public List<FormField> readJsonToDict() throws IOException {



//        ClassLoader classLoader = getClass().getClassLoader();
//        URL resource = classLoader.getResource("static/standardTemplate.json");
//        String filePath  = "";
//        if (resource == null) {
//            throw new IllegalArgumentException("File not found!");
//        } else {
//            File file = new File(resource.getFile());
//            filePath = file.getAbsolutePath();
//
//            System.out.println("Path to file: " + filePath);
//        }

       URL url = Constans.PATH_TO_ORIGIN_FORM;

        var formDataDTOList1 = objectMapper.readValue(url.openStream(), ListFormField.class);


      //   FormField[] formDataDTOList = objectMapper.readValue(url, FormField[].class);
      //  FormField[] formDataDTOList = (FormField[]) formDataDTOList1.name_template.toArray();

        //objectMapper.readTree(url).toString();

//        try (FileReader reader = new FileReader(fName)) {
//            FormField[] formDataDTOList =  gson.fromJson(reader, FormField[].class);

        //FormField[] formDataDTOList = ;
        return  formDataDTOList1.name_template;

           // System.out.println(formDataDTOList);
//        }

    }

    public void writeDictToJson(JsonObject data) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
      //  objectMapper.writeValue();
        try (FileWriter writer = new FileWriter(this.file_name)) {
            writer.write(gson.toJson(data));
        }
    }
}

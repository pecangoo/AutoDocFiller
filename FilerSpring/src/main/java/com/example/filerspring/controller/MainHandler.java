package com.example.filerspring.controller;


import com.example.filerspring.constans.Routes;
import com.example.filerspring.mappers.FormDataMapper;
import com.example.filerspring.model.FormDataDTO;
import com.example.filerspring.mappers.FromFormDataDTOToFormFieldMapper;
import com.example.filerspring.service.AutoFiller;
import com.example.filerspring.service.JSONHandler;
import com.example.filerspring.service.RepositoryService;
import com.example.filerspring.utils.Utils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

//TODO: константы на путях
@Controller
public class MainHandler {

    @PostConstruct
    private void init(){
        // Проверяем наличие шаблона по умолчанию в базе данных.
        // Если нет - добавляем

        try {
            var origTemplate = FormDataMapper
                    .mapToFormDataDTO(new JSONHandler().readJsonToDict(),
                            Routes.NameOriginForm);

            repositoryService.addNewForm(origTemplate);
        } catch (Exception es) {
            System.out.println("EXEXEXEXEXEXEXEXEEXE:" +  es);
        }
    }
    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("/v1/index")
    public String getIndex(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            Model model
    ) {


        if (type == null || name == null) {
            // TODO: Предусмотреть "Стандартный" шаблон
            var formToTemplate =
                    FromFormDataDTOToFormFieldMapper
                            .mapping(
                                    repositoryService
                                            .getFormByName("Стандартная форма")
                            );
            model.addAttribute("data", formToTemplate);
            model.addAttribute("name_form", name);
        } else {
            if (type.equals("form")) {
                var formToTemplate =
                        FromFormDataDTOToFormFieldMapper
                                .mapping(
                                        repositoryService
                                                .getFormByName(name)
                                );
                model.addAttribute("data", formToTemplate);
                model.addAttribute("name_form", name);
             //   System.out.println(model.asMap());
            } else if (type.equals("template")) {
                System.out.println("Tempplate to FRONT: " +
                        repositoryService.getTemplateByName(name));
               model.addAttribute("data", repositoryService.getTemplateByName(name));
            }
        }

        return "index.html";
    }


    @GetMapping("/v1/create-form")
    public String formsCreate() {
        return "create-form.html";
    }

    @GetMapping("/v1/test")
    public String test() {

        return "Ok";
    }

    @PostMapping("/v1/save-template/{form}/{temp}")
    public ResponseEntity<String> saveTemplate(@PathVariable String form,
                                               @PathVariable String temp,
                                               @RequestParam Map<String, String> formData) {
        {
           // System.out.println("FormData:  " + formData.keySet().toString());
            repositoryService.addNewTemplate(form, temp, formData);
            return  ResponseEntity.ok("Success");
        }
    }


    @GetMapping("v1/dropAll")
    public void dropAll() {
        repositoryService.dropALL();

    }
    @GetMapping("/")
    public String mainRedirect() {
        String redirectUrl = "/v1/index";
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/v1/del")
    public String del(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name
    ) {
        if (type.equals("template")) {
            repositoryService.deleteTemplateByName(name);
        } else if (type.equals("form")) {
            if (!name.equals(Routes.NameOriginForm))
                repositoryService.deleteFormByName(name);
        }
        return "redirect:" + "templates";
    }



    @PostMapping(value = "/v1/submit")
    public ResponseEntity<String> saveForm(
            @RequestBody FormDataDTO formDataDTO) {
        System.out.println("qwe");
        System.out.println(formDataDTO);
        repositoryService.addNewForm(formDataDTO);
        return  ResponseEntity.ok("Success");
    }




    @PostMapping("/v1/filldoc")
    public ResponseEntity<Resource>
    fillDocument(@RequestParam("file")
                 MultipartFile file,
                 @RequestParam Map<String, String> formData) {

        String pathFile = "";
        if (file.isEmpty()) {
            System.out.println( "Выберите файл для загрузки.");
        } else {
            System.out.println("Кажется файл передался");
        }

        System.out.println("File formData: " + formData.keySet().toString());
        try {


            var newFile = Utils.convertMultipartFileToFile(file);

            var autoFiller = new AutoFiller(newFile,
                    formData);
            autoFiller.replaceAllInDocx();
            pathFile = autoFiller.getTempFile().getAbsolutePath();
            System.out.println("Fill should be");

            Path path = Paths.get(pathFile);
            Resource resource = new InputStreamResource(Files.newInputStream(path));
            System.out.println(resource);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "newDoc.docx" + "\"")
                    .body(resource);


        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }


//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "newDoc" + "\"")
//                .body(null);

      // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при обработке запроса");

    }

//    @GetMapping("/v1/testgetAll")
//    public String getAll(){
//
//        return "templates";
//    }

    @GetMapping("/v1/templates")
    public String showTemplatesAndForms(Model model) {
        var listNames = repositoryService.getAllForms();

        var listTemplates = repositoryService.getALlTemplates();

        System.out.println(listNames);
        model.addAttribute("namesForm", listNames);
        model.addAttribute("templates", listTemplates);
        return "templates.html";
    }

    @PostMapping("/v1/deleteForm/{form}")
    public String deleteForm(
            @PathVariable String form) {
        repositoryService.deleteFormByName(form);

        // TODO: Запрос нового списка форм и повторная загрузка страницы
        return "create-form.html";

    }

    @PostMapping("/v1/deleteTemplate/{form}/{template}")
    public String deleteTemplate(
            @PathVariable String form,
            @PathVariable String template
    ) {


        // TODO: Запрос нового списка форм и повторная загрузка страницы
        return "create-form.html";

    }
}

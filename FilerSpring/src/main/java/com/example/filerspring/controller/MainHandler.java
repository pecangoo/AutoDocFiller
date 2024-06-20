package com.example.filerspring.controller;


import com.example.filerspring.constans.Routes;
import com.example.filerspring.mappers.FormDataMapper;
import com.example.filerspring.model.FormDataDTO;
import com.example.filerspring.mappers.FromFormDataDTOToFormFieldMapper;
import com.example.filerspring.model.FormField;
import com.example.filerspring.service.AutoFiller;
import com.example.filerspring.service.JSONHandler;
import com.example.filerspring.service.RepositoryService;
import com.example.filerspring.utils.MultipartUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//TODO: константы на путях
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class MainHandler {

    private final RepositoryService repositoryService;

    @PostConstruct
    private void init() {
        // Проверяем наличие шаблона по умолчанию в базе данных.
        // Если нет - добавляем
        try {
            var origTemplate = FormDataMapper.mapToFormDataDTO(new JSONHandler().readJsonToDict(), Routes.NAME_ORIGIN_FORM);
            repositoryService.addNewForm(origTemplate);
        } catch (Exception es) {
            System.out.println("EXEXEXEXEXEXEXEXEEXE:" + es);
        }
    }

    @GetMapping("/index")
    public String getIndex(@RequestParam(required = false) String type,
                           @RequestParam(required = false) String name,
                           Model model) {

        if (Objects.isNull(type) || Objects.isNull(name)) {
            // TODO: Предусмотреть "Стандартный" шаблон
            var formToTemplate = FromFormDataDTOToFormFieldMapper.
                    mapping(repositoryService.getFormByName("Стандартная форма"));
            model.addAttribute("data", formToTemplate);
            model.addAttribute("name_form", name);
        } else {
            if ("form".equals(type)) {
                var formToTemplate = FromFormDataDTOToFormFieldMapper.mapping(repositoryService.getFormByName(name));
                model.addAttribute("data", formToTemplate);
                model.addAttribute("name_form", name);
                //   System.out.println(model.asMap());
            } else if ("template".equals(type)) {
                List<FormField> templateByName = repositoryService.getTemplateByName(name);
                log.info("Tempplate to FRONT: {}", templateByName);
                model.addAttribute("data", templateByName);
            }
        }
        return "index.html";
    }


    @GetMapping("/create-form")
    public String formsCreate() {
        return "create-form.html";
    }

    @GetMapping("/test")
    public String test() {
        return "Ok";
    }

    @PostMapping(value = "/save-template/{form}/{temp}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveTemplate(@PathVariable String form,
                                               @PathVariable String temp,
                                               @RequestParam Map<String, String> formData) {
        // System.out.println("FormData:  " + formData.keySet().toString());
        repositoryService.addNewTemplate(form, temp, formData);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }


    @GetMapping("v1/dropAll")
    public void dropAll() {
        repositoryService.dropALL();

    }

    @GetMapping("/")
    public String mainRedirect() {
        String redirectUrl = "/index";
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/del")
    public String del(@RequestParam(required = false) String type,
                      @RequestParam(required = false) String name) {
        if ("template".equals(type)) {
            repositoryService.deleteTemplateByName(name);
        } else if ("form".equals(type) && !Routes.NAME_ORIGIN_FORM.equals(name)) {
            repositoryService.deleteFormByName(name);
        }

        return "redirect:" + "templates";
    }


    @PostMapping(value = "/submit")
    public ResponseEntity<String> saveForm(@RequestBody FormDataDTO formDataDTO) {
        System.out.println("qwe");
        System.out.println(formDataDTO);
        repositoryService.addNewForm(formDataDTO);
        return ResponseEntity.ok("Success");
    }


    @PostMapping("/filldoc")
    public ResponseEntity<Resource>
    fillDocument(@RequestParam("file") MultipartFile file,
                 @RequestParam Map<String, String> formData) {

        String pathFile;
        if (file.isEmpty()) {
            System.out.println("Выберите файл для загрузки.");
        } else {
            System.out.println("Кажется файл передался");
        }

        System.out.println("File formData: " + formData.keySet());
        try {
            var newFile = MultipartUtils.convertToFile(file);
            var autoFiller = new AutoFiller(newFile, formData);
            autoFiller.replaceAllInDocx();
            pathFile = autoFiller.getTempFile().getAbsolutePath();
            System.out.println("Fill should be");

            Path path = Paths.get(pathFile);
            Resource resource = new InputStreamResource(Files.newInputStream(path));
            System.out.println(resource);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "newDoc.docx" + "\"")
                    .body(resource);
        } catch (IOException ex) {
            log.warn("Description exception {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "newDoc" + "\"")
//                .body(null);

        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при обработке запроса");

    }

//    @GetMapping("/testgetAll")
//    public String getAll(){
//
//        return "templates";
//    }

    @GetMapping("/templates")
    public String showTemplatesAndForms(Model model) {
        var listNames = repositoryService.getAllForms();
        var listTemplates = repositoryService.getALlTemplates();

        System.out.println(listNames);
        model.addAttribute("namesForm", listNames);
        model.addAttribute("templates", listTemplates);
        return "templates.html";
    }

    @PostMapping("/deleteForm/{form}")
    public String deleteForm(@PathVariable String form) {
        repositoryService.deleteFormByName(form);
        // TODO: Запрос нового списка форм и повторная загрузка страницы
        return "create-form.html";

    }

    @PostMapping("/deleteTemplate/{form}/{template}")
    public String deleteTemplate(@PathVariable String form,
                                 @PathVariable String template) {
        // TODO: Запрос нового списка форм и повторная загрузка страницы
        return "create-form.html";
    }
}

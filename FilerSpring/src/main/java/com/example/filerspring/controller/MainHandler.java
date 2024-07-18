package com.example.filerspring.controller;


import com.example.filerspring.constans.Constans;
import com.example.filerspring.mappers.FormDataMapper;
import com.example.filerspring.mappers.FromFormDataDTOToFormFieldMapper;
import com.example.filerspring.model.FormDataDTO;
import com.example.filerspring.service.AutoFiller;
import com.example.filerspring.service.JSONHandler;
import com.example.filerspring.service.RepositoryService;
import com.example.filerspring.utils.MultiPartUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
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
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/v1")
public class MainHandler {
    private final JSONHandler jsonHandler;
    private final RepositoryService repositoryService;

    @PostConstruct
    private void init() {
        // Проверяем наличие шаблона по умолчанию в базе данных.
        // Если нет - добавляем

        try {
            var origTemplate = FormDataMapper
                    .mapToFormDataDTO(jsonHandler
                            .readJsonToDict(), Constans.NAME_ORIGIN_FORM);

            repositoryService.addNewForm(origTemplate);
        } catch (Exception es) {
            es.printStackTrace();
            log.error("Ошибка при инициализации стандартного шаблона");
            System.exit(1);
        }
    }


    @GetMapping("/index")
    public String getIndex(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            Model model
    ) {
        if (Objects.isNull(type) || Objects.isNull(name)) {
            var formToTemplate =
                    FromFormDataDTOToFormFieldMapper.mapping(repositoryService
                            .getFormByName(Constans.NAME_ORIGIN_FORM)
                    );
            model.addAttribute("data", formToTemplate);
            model.addAttribute("name_form", name);
        } else {
            if ("form".equals(type)) {
                var formToTemplate =
                        FromFormDataDTOToFormFieldMapper.mapping(
                                repositoryService.getFormByName(name)
                        );
                model.addAttribute("data", formToTemplate);
                model.addAttribute("name_form", name);
            } else if ("template".equals(type)) {
                var listToFront = repositoryService.getTemplateByName(name);
                model.addAttribute("data", listToFront);
            }
        }

        return "index.html";
    }


    @GetMapping("/create-form")
    public String formsCreate() {
        return "create-form.html";
    }


    @PostMapping(value = "/save-template/{form}/{temp}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveTemplate(@PathVariable String form,
                                               @PathVariable String temp,
                                               @RequestParam Map<String, String> formData) {

        repositoryService.addNewTemplate(form, temp, formData);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }


    @GetMapping("/dropAll")
    public void dropAll() {
        repositoryService.dropALL();
    }

    @GetMapping("/")
    public String mainRedirect() {
        String redirectUrl = "/index";
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/del")
    public String del(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name
    ) {
        if ("template".equals(type)) {
            repositoryService.deleteTemplateByName(name);
        } else if ("form".equals(type)) {
            if (!Constans.NAME_ORIGIN_FORM.equals(name))
                repositoryService.deleteFormByName(name);
        }
        return "redirect:" + "templates";
    }


    @PostMapping(value = "/submit")
    public ResponseEntity<String> saveForm(
            @RequestBody FormDataDTO formDataDTO) {
        repositoryService.addNewForm(formDataDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }


    @PostMapping("/filldoc")
    public ResponseEntity<Resource> fillDocument(@RequestParam("file") MultipartFile file,
                 @RequestParam Map<String, String> formData) {

        try {
            var newFile = MultiPartUtils.convertMultipartFileToFile(file);

            var autoFiller = new AutoFiller(newFile, formData);
            autoFiller.replaceAllInDocx();
            String pathFile = autoFiller.getTempFile().getAbsolutePath();
            Path path = Paths.get(pathFile);
            Resource resource = new InputStreamResource(Files.newInputStream(path));
            System.out.println(resource);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "newDoc.docx" + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/templates")
    public String showTemplatesAndForms(Model model) {
        var listNames = repositoryService.getAllForms();

        var listTemplates = repositoryService.getALlTemplates();

        model.addAttribute("namesForm", listNames);
        model.addAttribute("templates", listTemplates);
        return "templates.html";
    }

    @PostMapping("/deleteForm/{form}")
    public String deleteForm(
            @PathVariable String form) {
        repositoryService.deleteFormByName(form);

        // TODO: Запрос нового списка форм и повторная загрузка страницы
        return "create-form.html";

    }

    @PostMapping("/deleteTemplate/{form}/{template}")
    public String deleteTemplate(
            @PathVariable String form,
            @PathVariable String template
    ) {


        // TODO: Запрос нового списка форм и повторная загрузка страницы
        return "create-form.html";

    }
}

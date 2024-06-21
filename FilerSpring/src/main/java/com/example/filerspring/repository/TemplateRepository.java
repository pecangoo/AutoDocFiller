package com.example.filerspring.repository;

import com.example.filerspring.model.FormAndTemplate;
import com.example.filerspring.model.FormField;

import java.util.List;

public interface TemplateRepository {

    List<FormField> getTemplateByName(String nameTemplate);

    void addNewTemplate(String nameForm, String nameTemplate, List<FormField> listFormField);

    void dropALL();

    //    List<FormField> getAllTemplates();
    List<FormAndTemplate> getNamesAllTemplates();

    void deleteTemplateByName(String fullTempName);
}

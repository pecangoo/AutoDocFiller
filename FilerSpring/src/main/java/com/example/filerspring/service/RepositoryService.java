package com.example.filerspring.service;

import com.example.filerspring.model.FormAndTemplate;
import com.example.filerspring.model.FormDataDTO;
import com.example.filerspring.mappers.FromFormDataDTOToFormFieldMapper;
import com.example.filerspring.model.FormField;
import com.example.filerspring.repository.FormRepository;
import com.example.filerspring.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RepositoryService
{
    @Autowired
    private FormRepository formRepository;

    @Autowired
    private TemplateRepository templateRepository;

   // private MongoClient mongoClient

    public void dropALL(){
        formRepository.dropALL();
        templateRepository.dropALL();
    }
    public void addNewForm(FormDataDTO formDataDTO){
        formRepository.addNewForm(formDataDTO);
    }
    public void deleteFormByName(String formName){
        formRepository.deleteFormByName(formName);
    }
    public FormDataDTO getFormByName(String nameForm){
        return formRepository.getFormByName(nameForm);
    }

    public List<FormField> getTemplateByName(String nameTemplate){
        return templateRepository.getTemplateByName(nameTemplate);
    }

    public List<String> getAllForms(){
        return formRepository.getAllListNamesForms();
    }
    public List<FormAndTemplate> getALlTemplates() {
        return templateRepository.getNamesAllTemplates();
    }

    public void addNewTemplate(String nameForm,
                               String nameTemplate,
                               Map<String, String> formData){

        // добываем форму по названию.
        // Заполняем форму?
        System.out.println("56::::" + formData.toString());
        var listFormField = FromFormDataDTOToFormFieldMapper.mapping(formRepository.getFormByName(nameForm));

        System.out.println("60::::" + listFormField.toString()); //Все SOP менять на log.

        listFormField.forEach(el -> el.setValue_field(formData.get(el.getTag_field())));

        System.out.println("65::::" + listFormField.toString());
        templateRepository.addNewTemplate(nameForm, nameTemplate, listFormField);
    }

    public void deleteTemplateByName(String fullTempName){
        templateRepository.deleteTemplateByName(fullTempName);
    }
}

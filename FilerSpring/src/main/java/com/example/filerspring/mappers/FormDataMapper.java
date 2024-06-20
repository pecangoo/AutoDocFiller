package com.example.filerspring.mappers;

import com.example.filerspring.model.FormDataDTO;
import com.example.filerspring.model.FormField;

import java.util.ArrayList;
import java.util.List;

public class FormDataMapper {

    public static FormDataDTO mapToFormDataDTO(FormField[] formFields, String nameForm) {
        FormDataDTO formDataDTO = new FormDataDTO();
        formDataDTO.setNameForm(nameForm);

        List<String> nameList = new ArrayList<>();
        List<String> tagList = new ArrayList<>();
        List<String> hintList = new ArrayList<>();

        for (FormField field : formFields) {
            nameList.add(field.getName_field());
            tagList.add(field.getTag_field());
            hintList.add(field.getHint());
        }

//        System.out.println("NAMELIST: " + nameList);
//        System.out.println("TAGLIST: " + tagList);
//        System.out.println("HINTLIST: " + hintList);
        formDataDTO.setNameList(nameList);
        formDataDTO.setTagList(tagList);
        formDataDTO.setHintList(hintList);

        return formDataDTO;
    }
}
package com.example.filerspring.repository;


import com.example.filerspring.model.FormDataDTO;

import java.util.List;

public interface FormRepository {

    void addNewForm(FormDataDTO data);

    FormDataDTO getFormByName(String nameForm);

    List<String> getAllListNamesForms();

    void dropALL();

    void deleteFormByName(String nameForm);
}

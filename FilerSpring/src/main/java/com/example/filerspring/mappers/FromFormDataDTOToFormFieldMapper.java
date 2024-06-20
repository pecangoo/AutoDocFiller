package com.example.filerspring.mappers;

import com.example.filerspring.model.FormDataDTO;
import com.example.filerspring.model.FormField;

import java.util.ArrayList;
import java.util.List;

public class FromFormDataDTOToFormFieldMapper {

    // Добавить аналогичный MA
    public static List<FormField> mapping(FormDataDTO data) {
//        System.out.println("Start Mapping Data " + data);
        var resultList = new ArrayList<FormField>();
        for (int i = 0; i < data.getNameList().size(); i++) {
            String value = "";

            resultList.add(new FormField(
                            data.getNameList().get(i),
                            data.getTagList().get(i),
                    "",
                            data.getHintList().get(i)
                    )

            );
        }
//        System.out.println("Finish Data result List" + resultList);
        return  resultList;
    }

}

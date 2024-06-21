package com.example.filerspring.mappers;

import com.example.filerspring.model.FormDataDTO;
import com.example.filerspring.model.FormField;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class FromFormDataDTOToFormFieldMapper {

    // Добавить аналогичный MA
    public static List<FormField> mapping(FormDataDTO data) {
        log.debug("Start Mapping Data: {}", data);

        return IntStream.range(0, data.getNameList().size())
                .mapToObj(i -> FormField.builder()
                        .name_field(data.getNameList().get(i))
                        .tag_field(data.getTagList().get(i))
                        .value_field("")
                        .hint(data.getHintList().get(i))
                        .build())
                .collect(Collectors.toList());
    }
}

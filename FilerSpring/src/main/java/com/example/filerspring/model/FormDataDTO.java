package com.example.filerspring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.invoke.StringConcatException;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormDataDTO {
    @JsonProperty("name_form")
    private String nameForm;
    @JsonProperty("name_list")
    private List<String> nameList;
    @JsonProperty("tag_list")
    private List<String> tagList;
    @JsonProperty("hint_list")
    private List<String> hintList;

}



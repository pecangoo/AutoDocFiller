package com.example.filerspring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListFormField {

    @JsonProperty("list_fields")
    public ArrayList<FormField> name_template;
}

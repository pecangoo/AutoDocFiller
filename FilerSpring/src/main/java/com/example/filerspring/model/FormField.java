package com.example.filerspring.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormField {
    private String name_field;
    private String tag_field;
    private String value_field;
    private String hint;
}

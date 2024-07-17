package com.example.filerspring.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormField {
    private String name_field;
    private String tag_field;
    private String value_field;
    private String hint;
}

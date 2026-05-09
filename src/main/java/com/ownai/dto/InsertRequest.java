package com.ownai.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InsertRequest {

    private String title;
    private String text;
}
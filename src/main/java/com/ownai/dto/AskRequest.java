package com.ownai.dto;

import lombok.Data;

@Data
public class AskRequest {

    private String question;
    private int k;
}
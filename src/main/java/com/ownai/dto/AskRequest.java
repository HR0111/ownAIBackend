package com.ownai.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AskRequest {

    private String question;
    private int k;
}
package com.distributed.systems.dom_judge.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WinnerDto {

    private String username;
    private Date sentDate;
}

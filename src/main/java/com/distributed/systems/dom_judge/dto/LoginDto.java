package com.distributed.systems.dom_judge.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginDto {

    private String token;
    private String role;
}

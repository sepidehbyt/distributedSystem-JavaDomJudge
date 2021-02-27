package com.distributed.systems.dom_judge.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionDto {

    private Long id;
    private String title;
    private String description;
    private String inputPath;
    private String outputPath;
    private Date startDate;
    private Date endDate;
}

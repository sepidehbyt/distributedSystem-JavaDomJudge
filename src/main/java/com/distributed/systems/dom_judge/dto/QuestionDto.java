package com.distributed.systems.dom_judge.dto;

import lombok.*;

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
    private String startDate;
    private String endDate;
}

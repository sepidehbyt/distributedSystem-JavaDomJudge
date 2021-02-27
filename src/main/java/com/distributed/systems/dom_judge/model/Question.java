package com.distributed.systems.dom_judge.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Question extends Audible<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String inputPath;
    private String outputPath;

    private Date startDate;
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User admin;
}

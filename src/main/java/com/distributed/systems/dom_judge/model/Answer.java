package com.distributed.systems.dom_judge.model;

import com.distributed.systems.dom_judge.enumuration.AnswerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends Audible<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @Enumerated(EnumType.STRING)
    private AnswerStatus status;

    private String codePath;
}

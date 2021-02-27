package com.distributed.systems.dom_judge.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class User extends Audible<String> {

    private static final long serialVersionUID = -6315667530248935018L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private boolean enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
}
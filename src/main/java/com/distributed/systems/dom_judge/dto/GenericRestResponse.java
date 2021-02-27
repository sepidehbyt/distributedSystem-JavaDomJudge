package com.distributed.systems.dom_judge.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GenericRestResponse<T> {

    public interface STATUS {
        int SUCCESS = 0;
        int FAILURE = 1;
    }

    public GenericRestResponse(int status) {
        this.status = status;
    }

    private int status;
    private T data;
}


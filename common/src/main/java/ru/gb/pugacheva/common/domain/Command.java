package ru.gb.pugacheva.common.domain;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Command implements Serializable {

    private String commandName;
    private Object[] args;

}

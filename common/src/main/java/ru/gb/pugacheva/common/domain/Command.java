package ru.gb.pugacheva.common.domain;

import lombok.*;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Command implements Serializable {

    private String commandName;
    private Object [] args;

//    public String getCommandName() {
//        return commandName;
//    }
//
//    public String[] getArgs() {
//        return args;
//    }
//
//    public Command(String commandName, String[] args) {
//        this.commandName = commandName;
//        this.args = args;
//    }
}

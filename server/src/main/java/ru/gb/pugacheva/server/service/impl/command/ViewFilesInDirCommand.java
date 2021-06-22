package ru.gb.pugacheva.server.service.impl.command;

import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.server.service.CommandService;

import java.io.File;

public class ViewFilesInDirCommand  implements CommandService <String>{ // TODO: это ненужная команда

    @Override
    public String processCommand(Command command) {
        final int requirementCountCommandArgs = 1;
        //String [] actualCommandParts = command.split("\\s");
        if(command.getArgs().length !=requirementCountCommandArgs){
            throw new IllegalArgumentException("Command " + getCommand() + "is not correct");

        }
        return process(command.getArgs()[0]);
    }

    private String process (String dirPath){
        File directory = new File(dirPath);

        if(!directory.exists()){
            return "Directory does not exist";
        }

        StringBuilder builder = new StringBuilder();
        for (File childFile : directory.listFiles()) {
            String typeFile = getTypeFile (childFile);
            builder.append(childFile.getName()).append(" | ").append(typeFile).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String getTypeFile(File childFile) {
        return childFile.isDirectory() ? "DIR" : "FILE";
    }

    @Override
    public String getCommand() {
        return "ls";
    }
}

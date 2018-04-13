package ru.spbau.mit.command;

import ru.spbau.mit.execute.Scope;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Pwd outputs the current working directory path and nothing more
 */
public class Pwd extends Command{

    public Pwd(List<String> arguments) {
        super(arguments);
    }

    /**
     * Retuns the current working directory path
     * @param scope
     * @param inStream
     * @return
     */
    @Override
    public String execute(Scope scope, String inStream) {
        Path currentRelativePath = Paths.get(System.getProperty("user.dir"));
        return currentRelativePath.toAbsolutePath().toString();
    }

}

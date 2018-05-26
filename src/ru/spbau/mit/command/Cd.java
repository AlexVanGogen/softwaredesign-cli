package ru.spbau.mit.command;

import ru.spbau.mit.execute.Scope;

import java.io.IOException;
import java.util.List;

import static ru.spbau.mit.command.FileUtils.getAppropriateFilename;

/**
 * Provides command that change current user directory.
 */
public class Cd extends Command {

    public Cd(List<String> arguments) throws IOException {
        super(arguments);

        if (arguments.size() > 1) {
            throw new IOException(String.format("Expected at most 1 argument, but %d arguments found.", arguments.size()));
        }
    }

    /**
     * Changes current directory of working process to the directory given by relative path.
     * If no arguments were given, moves to home directory.
     * If one or more arguments were given, moves to the directory according to the first argument
     * (other arguments are ignored)
     * @param scope variables created by user during session
     * @param inStream user input
     * @return null: cd in bash doesn't print anything
     * @throws IOException when I/O error occurs
     */
    @Override
    public String execute(Scope scope, String inStream) throws IOException {

        String newWorkingDirectory = "";

        if (arguments.isEmpty()) {
            newWorkingDirectory = System.getProperty("user.home");
        } else {
            newWorkingDirectory = getAppropriateFilename(arguments.get(0), "cd");
        }

        System.setProperty("user.dir", newWorkingDirectory);
        return null;
    }
}

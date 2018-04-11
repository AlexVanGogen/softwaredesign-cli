package ru.spbau.mit.command;

import ru.spbau.mit.execute.Scope;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class Cd extends Command {

    public Cd(List<String> arguments) {
        super(arguments);
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
        if (arguments.isEmpty() || arguments.get(0).equals("~")) {
            newWorkingDirectory = System.getProperty("user.home");
        } else if (arguments.get(0).startsWith(System.getProperty("file.separator"))) {
            newWorkingDirectory = arguments.get(0);
        } else {
            newWorkingDirectory = System.getProperty("user.dir") + System.getProperty("file.separator") + arguments.get(0);
        }
        newWorkingDirectory = optimizePath(newWorkingDirectory);
        File file = new File(newWorkingDirectory);
        if (!file.exists()) {
            throw new NoSuchFileException(String.format("cd: %s: no such file or directory", arguments.get(0)));
        } else if (!file.isDirectory()) {
            throw new NotDirectoryException(String.format("cd: %s: Not a directory", arguments.get(0)));
        }
        System.setProperty("user.dir", newWorkingDirectory);
        return null;
    }

    /**
     * Deletes useless transitions such as "[directory_name]/../" from given path.
     * @param newWorkingDirectory string representation of path that needs to be optimized
     * @return string representation of path without useless transitions
     */
    private String optimizePath(String newWorkingDirectory) {
        final String[] pathElements = newWorkingDirectory.split(System.getProperty("file.separator"));
        final Deque<String> optimizedSetOfPathElements = new LinkedList<>();
        for (final String pathElement : pathElements) {
            if (pathElement.equals("..")) {
                optimizedSetOfPathElements.pollLast();
            } else {
                optimizedSetOfPathElements.offerLast(pathElement);
            }
        }
        if (optimizedSetOfPathElements.isEmpty()) {
            return System.getProperty("file.separator");
        }
        StringJoiner optimizedPath = new StringJoiner(System.getProperty("file.separator"));
        optimizedSetOfPathElements.forEach(optimizedPath::add);
        return optimizedPath.toString();
    }
}

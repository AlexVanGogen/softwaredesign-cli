package ru.spbau.mit.command;

import ru.spbau.mit.execute.Scope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.spbau.mit.command.FileUtils.getAppropriateFilename;

/**
 * Provides command that returns all directories and files for each given directory.
 */
public class Ls extends Command {

    public Ls(List<String> arguments) {
        super(arguments);
    }

    /**
     * Returns list of names of all directories and files for each given directory
     * (or only for current directory if arguments were not passed).
     * @param scope variables created by user during session
     * @param inStream input stream
     * @return result of command execution
     * @throws IOException when I/O error occurs
     */
    @Override
    public String execute(Scope scope, String inStream) throws IOException {
        String content;
        if (arguments.isEmpty()) {
            content = printFilenamesInSingleDirectory(System.getProperty("user.dir"));
        } else if (arguments.size() == 1 ) {
            content = printFilenamesInSingleDirectory(getAppropriateFilename(arguments.get(0), "ls"));
        } else {
            StringJoiner resultMaker = new StringJoiner(System.getProperty("line.separator"));
            for (final String nextDirectoryName : arguments) {
                try {
                    final String nextDirectoryContent = printFilenamesInSingleDirectory(getAppropriateFilename(nextDirectoryName, "ls"));
                    resultMaker.add(nextDirectoryName + ":").add(nextDirectoryContent);
                } catch (IOException e) {
                    resultMaker.add(e.getMessage());
                }
                resultMaker.add("");
            }
            content = resultMaker.toString();
        }
        return content;
    }

    /**
     * Returns list of names of all directories and files for directory with given name
     * @param directoryAbsoluteName absolute name of directory
     * @throws IOException when I/O error occurs
     */
    private String printFilenamesInSingleDirectory(final String directoryAbsoluteName) throws IOException {

        try (Stream<Path> paths = Files.list(Paths.get(directoryAbsoluteName))){
            return paths
                    .filter(path -> {
                        try {
                            return !Files.isHidden(path);
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .map(Path::toFile)
                    .map(File::getName)
                    .sorted()
                    .collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException e) {
            throw new IOException(String.format("ls: %s: no such file or directory", directoryAbsoluteName));
        }
    }
}

package ru.spbau.mit.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;

public class FileUtils {

    public static String getAppropriateFilename(String givenRelativePath, String commandName) throws IOException {
        String newWorkingDirectory = "";
        String osName = System.getProperty("os.name");
        if (givenRelativePath.startsWith("~") && (osName.startsWith("Unix") || osName.startsWith("Mac")) ) {
            newWorkingDirectory = givenRelativePath;
            newWorkingDirectory = newWorkingDirectory.replaceFirst("~", System.getProperty("user.home"));
        } else {

            File file = new File(givenRelativePath);

            if (file.isAbsolute()) {
                newWorkingDirectory = givenRelativePath;
            } else {
                newWorkingDirectory = System.getProperty("user.dir") + System.getProperty("file.separator") + givenRelativePath;
            }
        }

        try {
            File file = Paths.get(newWorkingDirectory).toRealPath().toFile().getCanonicalFile();

            if (!file.exists()) {
                throw new NoSuchFileException(String.format("%s: %s: no such file or directory", commandName, givenRelativePath));
            } else if (!file.isDirectory()) {
                throw new NotDirectoryException(String.format("%s: %s: Not a directory", commandName, givenRelativePath));
            }

            return file.getAbsolutePath();
        } catch (NoSuchFileException e) {
            throw new NoSuchFileException(String.format("%s: %s: no such file or directory", commandName, givenRelativePath));
        }

    }
}

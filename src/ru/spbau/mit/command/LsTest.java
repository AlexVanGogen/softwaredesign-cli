package ru.spbau.mit.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.execute.Scope;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LsTest {

    private Ls lsCommand;
    private List<Path> temporaryDirectories = new ArrayList<>();
    private Path directoriesPath;
    private Scope scope = new Scope();
    private int numberOfCreatedDirectories = 10;

    {
        try {
            directoriesPath = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), "tmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < numberOfCreatedDirectories; i++) {
            temporaryDirectories.add(Files.createTempDirectory(directoriesPath, "tmp"));
        }
    }

    @After
    public void tearDown() throws Exception {
        Files.walk(directoriesPath).forEach((path) -> path.toFile().deleteOnExit());
    }

    @Test
    public void testListInOneDirectory() throws IOException {
        List<Path> files = Arrays.asList(
                Files.createTempFile(directoriesPath, "file", ".txt"),
                Files.createTempFile(directoriesPath, "file", ".txt")
        );
        lsCommand = new Ls(Collections.singletonList(directoriesPath.toFile().getName()));
        String[] result = lsCommand.execute(null, null).split("\n");
        assertEquals(numberOfCreatedDirectories + 2, result.length);
        for (String s : result) {
            assertTrue(s.startsWith("tmp") || s.startsWith("file"));
        }
    }

    @Test
    public void testListInTwoDirectories() throws IOException {
        List<Path> files = Arrays.asList(
                Files.createTempFile(temporaryDirectories.get(0), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(0), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(1), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(1), "file", ".txt")
        );
        lsCommand = new Ls(Arrays.asList(
                temporaryDirectories.get(0).toString(),
                temporaryDirectories.get(1).toString())
        );
        String[] result = lsCommand.execute(null, null).split("\n");
        List<String> onlyFilenameLines = Arrays.stream(result).filter((name) -> name.startsWith("file")).collect(Collectors.toList());
        assertEquals(files.size(), onlyFilenameLines.size());
        for (String s : onlyFilenameLines) {
            assertTrue(s.startsWith("file"));
        }
    }

    @Test
    public void testIgnoreHiddenFiles() throws IOException {
        List<Path> files = Arrays.asList(
                Files.createTempFile(temporaryDirectories.get(0), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(0), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(0), ".file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(1), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(1), "file", ".txt")
        );
        lsCommand = new Ls(Arrays.asList(
                temporaryDirectories.get(0).toString(),
                temporaryDirectories.get(1).toString())
        );
        String[] result = lsCommand.execute(null, null).split("\n");
        List<String> onlyFilenameLines = Arrays.stream(result).filter((name) -> name.startsWith("file")).collect(Collectors.toList());
        assertEquals(files.size() - 1, onlyFilenameLines.size());
        for (String s : onlyFilenameLines) {
            assertTrue(s.startsWith("file"));
        }
    }

    @Test
    public void testLsWithNonexistentDirectories() throws IOException {
        List<Path> files = Arrays.asList(
                Files.createTempFile(temporaryDirectories.get(0), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(0), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(0), ".file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(1), "file", ".txt"),
                Files.createTempFile(temporaryDirectories.get(1), "file", ".txt")
        );
        lsCommand = new Ls(Arrays.asList(
                temporaryDirectories.get(0).toString(),
                "nonexistent_dir_1",
                temporaryDirectories.get(1).toString(),
                "nonexistent_dir_2")
        );
        String[] result = lsCommand.execute(null, null).split("\n");
        List<String> onlyFilenameLines = Arrays.stream(result).filter((name) -> name.startsWith("file")).collect(Collectors.toList());
        List<String> exceptionMessageLines = Arrays.stream(result).filter((name) -> name.startsWith("ls:")).collect(Collectors.toList());
        assertEquals(files.size() - 1, onlyFilenameLines.size());
        for (String s : onlyFilenameLines) {
            assertTrue(s.startsWith("file"));
        }
        assertEquals(2, exceptionMessageLines.size());
    }
}

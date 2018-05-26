package ru.spbau.mit.command;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CdTest {

    private Cd cdCommand;
    private Ls lsCommand;
    private List<Path> temporaryDirectories = new ArrayList<>();
    private Path directoriesPath;
    private int numberOfCreatedDirectories = 10;

    public void setUp() throws IOException {
        directoriesPath = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), "tmp");
        for (int i = 0; i < numberOfCreatedDirectories; i++) {
            temporaryDirectories.add(Files.createTempDirectory(directoriesPath, "tmp" + i + "_"));
        }
        for (Path temporaryDirectory : temporaryDirectories) {
            Files.createTempFile(temporaryDirectory, "file" + getDirectoryOrderedNumber(temporaryDirectory) + "_", ".txt");
        }
    }

    public void tearDown() throws IOException {
        Files.walk(directoriesPath).forEach((path) -> path.toFile().deleteOnExit());
    }

    @After
    public void returnOriginalWorkingDirectory() throws Exception {
        System.setProperty("user.dir", Paths.get(".").toString());
    }

    @Test
    public void testMoveToEverySubdirectoryAndBack() throws IOException {

        setUp();

        /* Move to tmp directory, that contains only 10 subdirectories and nothing more */
        cdCommand = new Cd(Collections.singletonList(directoriesPath.toFile().getName()));
        cdCommand.execute(null, null);
        lsCommand = new Ls(Collections.emptyList());
        String[] result = lsCommand.execute(null, null).split(System.getProperty("line.separator"));
        assertEquals(numberOfCreatedDirectories, result.length);
        for (String s : result) {
            assertTrue(s.startsWith("tmp"));
        }

        File[] subdirectories = directoriesPath.toFile().listFiles();

        /* Move to every subdirectory, each of it contains one file */
        for (File subdirectory : Objects.requireNonNull(subdirectories)) {
            if (subdirectory.isDirectory()) {
                cdCommand = new Cd(Collections.singletonList(subdirectory.getName()));
                cdCommand.execute(null, null);
                lsCommand = new Ls(Collections.emptyList());
                result = lsCommand.execute(null, null).split(System.getProperty("line.separator"));

                assertEquals(1, result.length);
                assertTrue(result[0].startsWith("file" + getDirectoryOrderedNumber(subdirectory.toPath())));

                cdCommand = new Cd(Collections.singletonList(".." + System.getProperty("file.separator")));
                cdCommand.execute(null, null);
                lsCommand = new Ls(Collections.emptyList());
                result = lsCommand.execute(null, null).split(System.getProperty("line.separator"));

                assertEquals(numberOfCreatedDirectories, result.length);
                for (String s : result) {
                    assertTrue(s.startsWith("tmp"));
                }
            }
        }

        tearDown();
    }

    @Test
    public void testMoveToHomeDirectory() throws IOException {
        if (System.getProperty("os.name").startsWith("Win")) {
            return;
        }
        cdCommand = new Cd(Collections.singletonList("~"));
        cdCommand.execute(null, null);
        lsCommand = new Ls(Collections.emptyList());
        String[] result1 = lsCommand.execute(null, null).split(System.getProperty("line.separator"));

        cdCommand = new Cd(Collections.emptyList());
        cdCommand.execute(null, null);
        lsCommand = new Ls(Collections.emptyList());
        String[] result2 = lsCommand.execute(null, null).split(System.getProperty("line.separator"));
        for (int i = 0; i < result1.length; i++) {
            assertTrue(result1[i].equals(result2[i]));
        }

        File homeDir = Paths.get(System.getProperty("user.home")).toFile();
        List<String> homeDirVisibleContent = Arrays.stream(homeDir.listFiles())
                .filter(file -> !file.isHidden())
                .map(File::toString)
                .map(str -> {
                    String[] elements = str.split(System.getProperty("file.separator"));
                    return elements[elements.length - 1];
                })
                .sorted()
                .collect(Collectors.toList());
        assertEquals(homeDirVisibleContent.size(), result1.length);
        for (int i = 0; i < result1.length; i++) {
            assertEquals(homeDirVisibleContent.get(i), result1[i]);
        }
    }

    @Test
    public void testMoveToRootDirectory() throws IOException {
        if (System.getProperty("os.name").startsWith("Win")) {
            return;
        }
        cdCommand = new Cd(Collections.singletonList(System.getProperty("file.separator")));
        cdCommand.execute(null, null);
        lsCommand = new Ls(Collections.emptyList());
        String[] result = lsCommand.execute(null, null).split(System.getProperty("line.separator"));

        File rootDir = Paths.get(System.getProperty("file.separator")).toFile();
        List<String> rootDirVisibleContent = Arrays.stream(rootDir.listFiles())
                .filter(file -> !file.isHidden())
                .map(File::toString)
                .map(str -> {
                    String[] elements = str.split(System.getProperty("file.separator"));
                    return elements[elements.length - 1];
                })
                .sorted()
                .collect(Collectors.toList());
        assertEquals(rootDirVisibleContent.size(), result.length);
        for (int i = 0; i < result.length; i++) {
            assertEquals(rootDirVisibleContent.get(i), result[i]);
        }
    }

    @Test(expected = NoSuchFileException.class)
    public void testMoveToNonexistentFolder() throws IOException {
        cdCommand = new Cd(Collections.singletonList("$$$"));
        cdCommand.execute(null, null);
    }

    @Test(expected = NotDirectoryException.class)
    public void testTryMoveNotToDirectory() throws IOException {
        System.setProperty("user.dir", Paths.get(".").toString());
        File tmpFile = Files.createTempFile(Paths.get("."), "file", ".txt").toFile();
        tmpFile.deleteOnExit();
        cdCommand = new Cd(Collections.singletonList(tmpFile.getName()));
        cdCommand.execute(null, null);
    }

    private char getDirectoryOrderedNumber(Path path) {
        return path.subpath(path.getNameCount() - 1, path.getNameCount()).toString().charAt(3);
    }
}

package ru.spbau.mit.command;

import ru.spbau.mit.execute.Scope;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Wc is an ad-hoc implementation of wc(1) that counts lines, words and size in bytes in the given file/input stream
 */
public class Wc extends Command{

    public Wc(List<String> arguments) {
        super(arguments);
    }

    /**
     * See Command.
     *
     * Wc implementation details: when invoked without arguments, wc tries to count lines, words and size in bytes in
     * its input stream (either passed explicitly or stdin). Otherwise it interprets the first arguments as a file path
     * and count lines, words and size in bytes in this file (if exists).
     * @param scope
     * @param inStrem
     * @return
     * @throws Exception
     */
    @Override
    public String execute(Scope scope, String inStrem)throws Exception{

        String result = "";

        if(arguments.size() == 0 && inStrem.equals("")){

            Scanner scanner = new Scanner(System.in);
            StringBuilder sb = new StringBuilder();
            while(scanner.hasNext())
            {
                sb.append(scanner.nextLine());
            }
            result = sb.toString();

        }else if(arguments.size() == 0){

            result = inStrem;

        }else if (arguments.size() == 1){

            Path path = Paths.get(arguments.get(0));
            if (!path.isAbsolute()) {
                path = Paths.get(System.getProperty("user.dir") + "/" + path.toString());
            } if (Files.exists(path)){
                result = new String(Files.readAllBytes(path));
            } else {
                throw new Exception("no such file " + arguments.get(0));
            }

        }else{

            throw new Exception("cat: too many arguments");
        }

        final byte[] utf8Bytes = result.getBytes("UTF-8");
        int countOfByte = utf8Bytes.length;
        StringTokenizer st = new StringTokenizer(result);
        int countOfWords = st.countTokens();
        st = new StringTokenizer(result, "\n");
        int countOfLine = st.countTokens();

        return "" + countOfLine + " " + countOfWords + " " + countOfByte;
    }
}

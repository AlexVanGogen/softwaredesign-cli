package ru.spbau.mit.command;

import ru.spbau.mit.execute.Scope;
import ru.spbau.mit.parse.CommandBuilder;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Cat prints either a contents of the given file passed as argument or anything passed as input
 */
public class Cat extends Command{

    public Cat(List<String> arguments)  {
           super(arguments);
    }

    /**
     * See Command.
     *
     * Cat implementation details: when executing Cat without any arguments it tries to print whatever comes to its
     * input stream (either provided beforehand or from stdin). Otherwise the first argument is treated as a filename
     * that is loaded into memory and which contents gets printed to the output.
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
            }
            if (Files.exists(path)) {
                result = new String(Files.readAllBytes(path));
            } else {
                throw new Exception("no such file " + arguments.get(0));
            }


        }else{

            throw new Exception("cat: too many arguments");
        }
        return result;
    }
}

package ru.spbau.mit;

import ru.spbau.mit.command.Command;
import ru.spbau.mit.execute.Executor;
import ru.spbau.mit.execute.Scope;
import ru.spbau.mit.parse.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class CLI {

    /**
     * The main workflow method of this utility
     * @param args - ignored
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        CommandBuilder cb = new CommandBuilder();
        Executor ex = new Executor();
        Scope scope = new Scope();

        while (true) {
            String command = scanner.nextLine();
            try
            {
                List<Command> lc = cb.buildChainOfCommand(scope, command);
                String result = ex.exec(scope, lc);
                if (result != null) {
                    System.out.println(result);
                }
            }catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

        }
    }
}

package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.IRepository;
import interpreter.repository.Repository;

import java.util.Scanner;

public class TextMenu {
    private IStmt selectedProgram = Examples.EX1;

    public void show() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Toy Language Interpreter ===");
            System.out.println("Currently selected: " + selectedProgram);
            System.out.println("1. Select a program");
            System.out.println("2. Run the selected program");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> selectProgram(scanner);
                case "2" -> runSelectedProgram();
                case "0" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void selectProgram(Scanner scanner) {
        System.out.println("\nAvailable programs:");
        System.out.println("1: " + Examples.EX1);
        System.out.println("2: " + Examples.EX2);
        System.out.println("3: " + Examples.EX3);
        System.out.print("Enter program number (1-3): ");

        String progChoice = scanner.nextLine().trim();
        switch (progChoice) {
            case "1" -> selectedProgram = Examples.EX1;
            case "2" -> selectedProgram = Examples.EX2;
            case "3" -> selectedProgram = Examples.EX3;
            default -> System.out.println("Invalid program number.");
        }
    }

    private void runSelectedProgram() {
        PrgState state = new PrgState(
                new MyStack<>(),
                new MyDictionary<>(),
                new MyList<>(),
                selectedProgram
        );
        IRepository repo = new Repository(state);
        Controller controller = new Controller(repo, true);

        try {
            controller.allStep();
        } catch (MyException e) {
            System.out.println("Execution error: " + e.getMessage());
        }
    }
}
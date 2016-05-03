package de.schattney.app;

import java.util.Scanner;
import java.util.concurrent.Executors;

public class Application {

    public static void main(String[] args){

        IMaterialConsumer materialConsumer = new MessageConsumer();
        MaterialServerExecutor serverExecutor = new MaterialServerExecutor(args, materialConsumer);
        Executors.newSingleThreadExecutor().submit(serverExecutor);

        Scanner scanner = new Scanner(System.in);

        while(true){
            printMenu();
            int procedureId = scanner.nextInt();
            switch(procedureId){
                case 1:
                    materialConsumer.refillMaterial();
                    break;
            }
        }

    }

    private static void printMenu() {
        System.out.println("(1) Refill Material");
    }

}

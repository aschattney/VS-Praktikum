package de.hochschuledarmstadt.controlpanel.app;

import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.model.PrintJob;
import de.hochschuledarmstadt.model.request.ColorStatusRequest;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserInputProcessor implements OnPrintJobEndedCallback {

    // User input codes
    private static final int PRINT_NAIL = 1;
    private static final int PRINT_HAMMER = 2;
    private static final int MATERIAL_STATUS = 3;

    private static final String NAIL_JSON_FILE = "nail.json";
    private static final String HAMMER_JSON_FILE = "hammer.json";

    // Plans
    private static Map<Integer, String> printPlanFileNames = new HashMap<>();

    // Insert print plans
    static {
        printPlanFileNames.put(1, NAIL_JSON_FILE);
        printPlanFileNames.put(2, HAMMER_JSON_FILE);
    }

    private final PrintJobExecutor printJobExecutor;
    private final ISocketClient materialClient;

    private final InputStream inputStream;
    private final PrintStream outputStream;

    public UserInputProcessor(InputStream inputStream, OutputStream outputStream, PrintJobExecutor printJobExecutor, ISocketClient materialClient) {
        this.inputStream = inputStream;
        this.outputStream = new PrintStream(outputStream, true);
        this.printJobExecutor = printJobExecutor;
        this.materialClient = materialClient;
    }

    /**
     * This method won't return until the application exits
     */
    public void processSync() {
        // scanner for watching inputstream to get next instruction from user
        Scanner scanner = new Scanner(inputStream);
        boolean running = true;
        while (running) {
            try {
                printMenu();
                int result = getNextInstruction(scanner);
                if (isPrintJobInstruction(result)) {
                    addPrintJobToQueue(result);
                } else if (isGetMaterialStatusInstruction(result)) {
                    sendMaterialStatusRequest(materialClient);
                }
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }

    }

    private void addPrintJobToQueue(int result) throws IOException {
        String printPlanFileName = printPlanFileNames.get(result);
        PrintJob printJob = PrintPlanJSONFileReader.readFile(printPlanFileName);
        String message = (!printJobExecutor.isPrinting()) ? "Druckauftrag gestartet" : "Druckauftrag in die Warteschlange eingereiht";
        for (int i = 0; i < 20000; i++) {
            printJobExecutor.addPrintJob(printJob);
            /*if (printJobExecutor.addPrintJob(printJob))
                outputStream.println(message);*/
        }
        start = System.currentTimeMillis();
    }

    private boolean isGetMaterialStatusInstruction(int result) {
        return result == MATERIAL_STATUS;
    }

    private boolean isPrintJobInstruction(int result) {
        return result == PRINT_NAIL || result == PRINT_HAMMER;
    }

    private int getNextInstruction(Scanner scanner) {
        return scanner.nextInt();
    }

    private void printMenu() {
        outputStream.println("(1) Print Nail");
        outputStream.println("(2) Print Hammer");
        outputStream.println("(3) Material Status Request");
    }

    private JSONObject sendMaterialStatusRequest(ISocketClient materialClient) throws IOException {
        ColorStatusRequest materialStatusRequest = new ColorStatusRequest();
        JSONObject materialStatusResponse = materialClient.sendMessage(materialStatusRequest.toJSON());
        outputStream.println(materialStatusResponse);
        return materialStatusResponse;
    }

    private int printJobsFinished;
    private long start;
    private long end;

    @Override
    public void onPrintJobEnded(PrintJob printJob) {
        printJobsFinished++;
        if (printJobsFinished % 1000 == 0)
            System.out.println(printJobsFinished);
        if (printJobsFinished % 20000 == 0){
            end = System.currentTimeMillis();
            System.out.println(String.valueOf((end-start) / 1000.0) + " seconds");
        }
    }

}

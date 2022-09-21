import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Server {
    public static void main (String[] args) {
    int tcpPort;
    int udpPort;
    if (args.length != 3) {
        System.out.println("ERROR: Provide 3 arguments");
        System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
        System.out.println("\t(2) <udpPort>: the port number for UDP connection");
        System.out.println("\t(3) <file>: the file of inventory");

        System.exit(-1);
    }
    tcpPort = Integer.parseInt(args[0]);
    udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];

    Map<String, Integer> inventory = new HashMap<>();
    try {
        File file = new File(fileName);

        Scanner fileScanner = new Scanner(file);
        while (fileScanner.hasNextLine()) {
            String[] item = fileScanner.nextLine().strip().split(" ");
            // Only parce the line if we have a key and value
            if (item.length == 2) {
                inventory.put(item[0], Integer.parseInt(item[1]));
            }
        }
    } catch(FileNotFoundException e) {
        System.out.println("Unable to process the inventry, verify filepath");
    } catch(Exception e) {
        System.out.println("An unknown error occured");
    }

    // Inventory Loaded

    // TODO: handle request from clients
    }
}

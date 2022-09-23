import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.*;

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

        // Common inventory to be used by all the server threads
        Inventory inventory = new Inventory(fileName);

        // Set up the TCP Port
        new Thread(() -> startTCPServer(inventory, tcpPort)).start();
        new Thread(() -> startUDPServer(inventory, udpPort)).start();
    }

    public static void startTCPServer(Inventory inventory, int tcpPort) {
        try {
    		ServerSocket listener = new ServerSocket(tcpPort);
    		Socket s;
    		while ( (s = listener.accept()) != null) {
    			Thread t = new TCPServerThread(inventory, s);
    			t.start();
    		}
    	} catch (IOException e) {
    		System.err.println("Server aborted:" + e);
    	}
    }

    public static void startUDPServer(Inventory inventory, int udpPort) {
        try {
            DatagramPacket datapacket, returnpacket;
            int len = 1024;

            DatagramSocket datasocket = new DatagramSocket(udpPort);
            byte[] buf = new byte[len];

            while(true) {
                try {
                    datapacket = new DatagramPacket(buf, buf.length);
                    datasocket.receive(datapacket);
                    ArrayList<String> command = new ArrayList<String>(
                        Arrays.asList(
                            new String(datapacket.getData(), 0, datapacket.getLength()).split(" ")
                        )
                    );
                    System.out.println("UDP: " + String.join(" ", command));

                    String returnMessage = "";
                    if (command.get(0).equals("purchase")) {
                        returnMessage = inventory.purchase(
                            command.get(1),
                            command.get(2),
                            Integer.parseInt(command.get(3))
                        );
                    } else if (command.get(0).equals("cancel")) {
                        returnMessage = inventory.cancel(
                            Integer.parseInt(command.get(1))
                        );
                    } else if (command.get(0).equals("search")) {
                        for (String message : inventory.search(command.get(1))) {
                            returnMessage += message + "\r\n";
                        }
                    } else if ((command.get(0)).equals("list")) {
                        for (String message : inventory.list()) {
                            returnMessage += message + "\r\n";
                        }
                    }

                    // Return the message to sender
                    returnpacket = new DatagramPacket(
                        returnMessage.getBytes(StandardCharsets.UTF_8),
                        returnMessage.getBytes(StandardCharsets.UTF_8).length,
                        datapacket.getAddress(),
                        datapacket.getPort()
                    );
                    datasocket.send(returnpacket);
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}

public class Inventory {
    Map<String, Integer> inventoryMap = new HashMap<>();
    Map<Integer, String> orderMap = new HashMap<>();
    Integer nextOrderNumber = 1;
    public Inventory(String fileToLoadFrom) {
        try {
            File file = new File(fileToLoadFrom);

            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String[] item = fileScanner.nextLine().strip().split(" ");
                // Only parce the line if we have a key and value
                if (item.length == 2) {
                    inventoryMap.put(item[0], Integer.parseInt(item[1]));
                }
            }
            System.out.println("Inventory Loaded.");
        } catch(FileNotFoundException e) {
            System.out.println("Unable to process the inventry, verify filepath");
        } catch(Exception e) {
            System.out.println("An unknown error occured");
        }
    }

    public ArrayList<String> list() {
        List<String> listOfProducts = new ArrayList<>(inventoryMap.keySet());
        Collections.sort(listOfProducts);

        ArrayList<String> listMessage = new ArrayList<String>();

        for (String product : listOfProducts) {
            listMessage.add(product + " " + inventoryMap.get(product));
        }
        return listMessage;
    }

    public String purchase(String userName, String product, Integer purchaseQtt) {
        if (inventoryMap.containsKey(product)) {
            if (inventoryMap.get(product) >= purchaseQtt) {
                String message = "You order has been placed, " + nextOrderNumber + " " +
                    userName + " " + product + " " + purchaseQtt;
                orderMap.put(nextOrderNumber, userName + " " + product + " " + purchaseQtt);
                nextOrderNumber += 1;
                inventoryMap.put(product, inventoryMap.get(product) - purchaseQtt);

                return message;
            } else {
                return "Not Available - Not enough items";
            }
        } else {
            return "Not Available - We do not sell this product";
        }
    }

    public String cancel(Integer orderNumber) {
        if (orderMap.containsKey(orderNumber)) {
            ArrayList<String> order = new ArrayList<String>(
                Arrays.asList(
                    orderMap.get(orderNumber).split(" ")
                )
            );
            inventoryMap.put(order.get(1), inventoryMap.get(order.get(1)) + Integer.parseInt(order.get(2)));
            orderMap.remove(orderNumber);
            return "Order " + orderNumber + " is canceled";
        } else {
            return orderNumber + " not found, no such order";
        }
    }

    public ArrayList<String> search(String userName) {
        ArrayList<String> usersOrders = new ArrayList<>();
        for (Integer orderNumber : orderMap.keySet()) {
            ArrayList<String> order = new ArrayList<String>(
                Arrays.asList(
                    orderMap.get(orderNumber).split(" ")
                )
            );
            if (order.get(0).equals(userName)) {
                usersOrders.add(orderNumber + ", " + order.get(1) + ", " + order.get(2));
            }
        }

        if (usersOrders.size() == 0) {
            usersOrders.add("No order found for " + userName);
        }
        return usersOrders;
    }
}

public class TCPServerThread extends Thread {
    Inventory inventory;
    Socket theClient;
    public TCPServerThread(Inventory inventory, Socket s) {
        this.inventory = inventory;
        theClient = s;
    }
    public void run() {
        try {
            Scanner sc = new Scanner(theClient.getInputStream());
            PrintWriter pout = new PrintWriter(theClient.getOutputStream());
            String command = sc.nextLine();
            System.out.println("TCP: " + command);

            Scanner st = new Scanner(command);
            String tag = st.next();
            if (tag.equals("purchase")) {
                pout.println(
                    inventory.purchase(
                        st.next(),
                        st.next(),
                        Integer.parseInt(st.next())
                    )
                );
            } else if (tag.equals("cancel")) {
                pout.println(
                    inventory.cancel(
                        Integer.parseInt(st.next())
                    )
                );
            } else if (tag.equals("search")) {
                for (String message : inventory.search(st.next())) {
                    pout.println(message);
                }
            } else if (tag.equals("list")) {
                for (String message : inventory.list()) {
                    pout.println(message);
                }
            }
            pout.flush();
            theClient.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}

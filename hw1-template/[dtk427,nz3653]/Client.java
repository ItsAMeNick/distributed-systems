import java.util.*;
import java.io.*;
import java.net.*;

public class Client {
    public static void main (String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;

        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <hostAddress>: the address of the server");
            System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(3) <udpPort>: the port number for UDP connection");
            System.exit(-1);
        }

        hostAddress = args[0];
        tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);

        // false = TCP, true = UDP
        Boolean useTCPMode = true;

        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");

            // This entire switch should be moved to be server side, but I wanted to add
            // argument validation locally and thiat menifested poorly when it came
            // to all the unnecessary tcp/udp checks
            if (tokens[0].equals("setmode")) {
                if (tokens.length >= 2) {
                    if (tokens[1].equals("T")) {
                        System.out.println("Communication Mode: TCP");
                        useTCPMode = true;
                    } else if (tokens[1].equals("U")) {
                        System.out.println("Communication Mode: UDP");
                        useTCPMode = false;
                    } else {
                       System.out.println("setmode <T|U>: Improper use");
                   }
                } else {
                    System.out.println("setmode <T|U>: Improper use");
                }
            } else if (tokens[0].equals("purchase")) {
                if (tokens.length >= 4) {
                    if (useTCPMode) {
                        System.out.println(sendCommandBlockingTCP(cmd, hostAddress, tcpPort).get(0));
                    } else {
                        String response = sendCommandBlockingUDP(cmd, hostAddress, udpPort);
                        System.out.println(response);
                    }
                } else {
                    System.out.println("purchase <username> <product-name> <quantity>: Improper use");
                }
            } else if (tokens[0].equals("cancel")) {
                if (tokens.length >= 2) {
                    if (useTCPMode) {
                        System.out.println(sendCommandBlockingTCP(cmd, hostAddress, tcpPort).get(0));
                    } else {
                        String response = sendCommandBlockingUDP(cmd, hostAddress, udpPort);
                        System.out.println(response);
                    }
                } else {
                    System.out.println("cancel <order-id>: Improper use");
                }
            } else if (tokens[0].equals("search")) {
                if (tokens.length >= 2) {
                    if (useTCPMode) {
                        ArrayList<String> response = sendCommandBlockingTCP(cmd, hostAddress, tcpPort);
                        for (String message : response) {
                            System.out.println(message);
                        }
                    } else {
                        String response = sendCommandBlockingUDP(cmd, hostAddress, udpPort);
                        System.out.println(response);
                    }
                } else {
                    System.out.println("search <user-name>: Improper use");
                }
            } else if (tokens[0].equals("list")) {
                if (tokens.length >= 1) {
                    if (useTCPMode) {
                        ArrayList<String> response = sendCommandBlockingTCP(tokens[0], hostAddress, tcpPort);
                        for (String message : response) {
                            System.out.println(message);
                        }
                    } else {
                        String response = sendCommandBlockingUDP(tokens[0], hostAddress, udpPort);
                        System.out.println(response);
                    }
                } else {
                    System.out.println("list: Improper use");
                }
            } else {
                System.out.println("ERROR: No such command");
            }
        }
    }

    private static ArrayList<String> sendCommandBlockingTCP(String command, String hostAddress, int tcpPort) {
        ArrayList<String> allMessages = new ArrayList<>();

        try {
            Socket server = new Socket(hostAddress, tcpPort);
            Scanner din = new Scanner(server.getInputStream());
            PrintStream pout = new PrintStream(server.getOutputStream());

            pout.println(command);
            pout.flush();
            while (din.hasNextLine()) {
                allMessages.add(din.nextLine());
            }
        } catch (IOException e){
            System.err.println(e);
        }
        return allMessages;
    }

    private static String sendCommandBlockingUDP(String command, String hostAddress, int udpPort) {
        String returnMessage = "";

        DatagramPacket sPacket, rPacket ;
        try{
            InetAddress ia = InetAddress.getByName(hostAddress) ;
            DatagramSocket datasocket = new DatagramSocket() ;

            byte[] buffer = new byte[command.length()];
            buffer = command.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            datasocket.send(sPacket);
            byte[] rbuffer = new byte[1024];
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            datasocket.receive(rPacket);
            returnMessage = new String(rPacket.getData(), 0, rPacket.getLength()).strip();
        } catch (UnknownHostException e ) {
            System.err.println(e);
        } catch ( SocketException se ) {
            System.err.println(se);
        }  catch (IOException e){
            System.err.println(e);
        }

        return returnMessage;
    }
}

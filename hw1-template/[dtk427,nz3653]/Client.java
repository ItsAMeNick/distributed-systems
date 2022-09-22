import java.util.Scanner;
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

            if (tokens[0].equals("setmode")) {
                // TODO: set the mode of communication for sending commands to the server
                // and display the name of the protocol that will be used in future

                if (tokens.length >= 2) {
                    if (tokens[1].equals("T")) {
                        System.out.println("Communication Mode: TCP");
                        useTCPMode = true;
                    } else if (tokens[1].equals("U")) {
                        System.out.println("Communication Mode: UDP");
                        useTCPMode = false;
                        DatagramPacket sPacket, rPacket ; 
                        try{
                            InetAddress ia = InetAddress.getByName(hostname) ; 
                            DatagramSocket datasocket = new DatagramSocket() ; 
                        } catch (UnknownHostException e ) { 
                            System.err.println(e) ; 
                        } catch ( SocketException se ) { 
                            System.err.println(se) ;
                        }
                        
                    } else {
                       System.out.println("setmode <T|U>: Improper use");
                   }
                } else {
                    System.out.println("setmode <T|U>: Improper use");
                }
            } else if (tokens[0].equals("purchase")) {
                if (tokens.length >= 4) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    if(!useTCPMode){
                        // use UDP
                        try{
                        byte[] buffer = new byte[tokens[0].length];
                        buffer = tokens[0].getBytes();
                        sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
                        datasocket.send(sPacket); // should send "purchase <username> <product-name> <quantity>"
                        byte[] rbuffer = new byte[1024];
                        rPacket = new DatagramPacket(rbuffer, rbuffer.length);
                        datasocket.receive(rPacket);
                        String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
                        System.out.println(retstring);
                        } catch (IOException e){
                            System.err.println(e)
                        }
                    } else {
                        // use TCP
                    }
                } else {
                    System.out.println("purchase <username> <product-name> <quantity>: Improper use");
                }
            } else if (tokens[0].equals("cancel")) {
                if (tokens.length >= 2) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    if(!useTCPMode){
                        // use UDP
                        try{
                        byte[] buffer = new byte[cmd.length];
                        buffer = cmd.getBytes(); // cmd used here instead of tokens. need to split cmd into tokens on server side
                        sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
                        datasocket.send(sPacket); // should send "cancel <order-id>"
                        byte[] rbuffer = new byte[1024];
                        rPacket = new DatagramPacket(rbuffer, rbuffer.length);
                        datasocket.receive(rPacket);
                        String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
                        System.out.println(retstring);
                        } catch (IOException e){
                            System.err.println(e)
                        }
                    } else {
                        // use TCP
                    }
                } else {
                    System.out.println("cancel <order-id>: Improper use");
                }
            } else if (tokens[0].equals("search")) {
                if (tokens.length >= 2) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    if(!useTCPMode){
                        // use UDP
                        try{
                        byte[] buffer = new byte[cmd.length];
                        buffer = cmd.getBytes(); // cmd used here instead of tokens. need to split cmd into tokens on server side
                        sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
                        datasocket.send(sPacket); // should send "search <user-name>"
                        byte[] rbuffer = new byte[1024];
                        rPacket = new DatagramPacket(rbuffer, rbuffer.length);
                        datasocket.receive(rPacket);
                        String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
                        System.out.println(retstring);
                        } catch (IOException e){
                            System.err.println(e)
                        }
                    } else {
                        // use TCP
                    }
                } else {
                    System.out.println("search <user-name>: Improper use");
                }
            } else if (tokens[0].equals("list")) {
                if (tokens.length >= 1) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    if(!useTCPMode){
                        // use UDP
                        try{
                        byte[] buffer = new byte[tokens[0].length];
                        buffer = tokens[0].getBytes();
                        sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
                        datasocket.send(sPacket); // should send "list"
                        byte[] rbuffer = new byte[1024];
                        rPacket = new DatagramPacket(rbuffer, rbuffer.length);
                        datasocket.receive(rPacket);
                        String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
                        System.out.println(retstring);
                        } catch (IOException e){
                            System.err.println(e)
                        }
                    } else {
                        // use TCP
                    }
                } else {
                    System.out.println("list: Improper use");
                }
            } else {
                System.out.println("ERROR: No such command");
            }
        }
    }
}

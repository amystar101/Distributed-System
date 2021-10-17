package DistributedSystem.Communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

// Receive class handles the message received from other nodes in the cluster
public class Receiver extends Thread{
    private ServerSocket serverSocket = null;
    public ConcurrentHashMap<Integer, Connection>connections;
    public PriorityBlockingQueue<Message>processingQueue;

    public Receiver(int port, ConcurrentHashMap<Integer,Connection>connections, Comparator comparator){
        this.connections = connections;
        this.processingQueue = new PriorityBlockingQueue<Message>(10,comparator); // store the messages to be processed
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Receiver initialised at port = "+port+"...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    function always to read the messages
    @Override
    public void run(){
//        thread for fetching up the connections
        System.out.println("started accepting the joining request...");
        Thread fetchThread = new Thread("Thread to fetch the connections"){
            public void run(){
                while(true){
                    try {
                        Socket socket = serverSocket.accept();
                        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//                        store the remaining attributes for the connection
                        while(true){
//                            wait for the first message containing the sender's information for handshake
//                            necessary authentication can also be added here.
                            String firstMessage = in.readUTF();
                            if(firstMessage != null){
//                                first message syntax :- nodeId
                                int nodeId = Integer.parseInt(firstMessage);
                                if(connections.containsKey(nodeId)) {
                                    connections.get(nodeId).in = in;
                                    connections.get(nodeId).setAliveTime();
                                    System.out.println("Receiver Connected with nodeId = "+nodeId);
                                }
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        fetchThread.start();

        while(true){
            for(Connection connection:this.connections.values()){
                if(connection.in != null){
                    String messageBody = null;
                    try {
                        messageBody = connection.in.readUTF();
                        if(messageBody != null){
//                        insert the message in the processing queue
                            this.processingQueue.add(new Message(connection.nodeId,messageBody.split("-")[1],
                                    messageBody.split("-")[0]));
                        }
                    } catch (IOException e) {
//                        connection.in = null;
                        continue;
                    }
                }
            }

        }
    }

//    function to return message
    public Message getMessage(){
        return this.processingQueue.poll();
    }
}

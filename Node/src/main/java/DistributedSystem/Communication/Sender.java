package DistributedSystem.Communication;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

// Send class handle the sending message work.
public class Sender extends Thread {
    final int nodeId;
    public PriorityBlockingQueue<Message>processedQueue;
    public ConcurrentHashMap<Integer, Connection>connections;

    public Sender(int nodeId, Comparator comparator, ConcurrentHashMap<Integer,Connection>connections){
        this.nodeId = nodeId;
        this.processedQueue = new PriorityBlockingQueue<Message>(10,comparator); // stores the processed message;
        this.connections = connections;
    }

    @Override
    public void run() {
//        establishing the required connection.
        Thread initialConnectionThread = new Thread("Initial connection request"){
            @Override
            public void run() {
                System.out.println("Requesting the neighbour nodes for connection...");
                while (true) {
                    for (Connection connection : connections.values()) {
                        if (connection.out == null) { // can be replaced with something better representing disconnected
                            try {
                                Socket socket = new Socket("localhost", connection.port);
                                connection.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//                            sending the peer node about this node
                                connection.out.writeUTF(Integer.toString((Integer) nodeId));
                                connection.out.flush();
                                connection.setAliveTime();
                                System.out.println("Sender connected with nodeId =  " + connection.nodeId);
                            } catch (IOException e) {
//                            either the peer node is down or some error
                                continue;
                            }
                        }
                    }
                }
            }
        };

        initialConnectionThread.start();

//        handling sending of messages
        while(true){
            if(!processedQueue.isEmpty()){
                Message message = processedQueue.poll();
                try {
                    connections.get(message.nodeId).out.writeUTF(message.category+"-"+message.messageBody);
                    connections.get(message.nodeId).out.flush();
                } catch (IOException |NullPointerException e) {
//                    may be peer node is down
                    continue;
                }
            }
        }
    }

//    method to send message
    public void sendMessage(Message message){
        this.processedQueue.add(message);
    }
}

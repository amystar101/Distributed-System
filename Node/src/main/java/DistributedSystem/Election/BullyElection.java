package DistributedSystem.Election;
import DistributedSystem.Communication.Connection;
import DistributedSystem.Communication.Message;
import DistributedSystem.Communication.Sender;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BullyElection extends Thread{
    int nodeId;
    AtomicInteger masterId;
    ConcurrentHashMap<Integer, Connection> connections;
    public Sender sender;

    public BullyElection(int nodeId, ConcurrentHashMap<Integer,Connection>connections, Sender sender){
        this.connections = connections;
        this.masterId = new AtomicInteger(0);
        this.sender = sender;
        this.nodeId = nodeId;
    }

    @Override
    public void run() {
        System.out.println("Bully Election is running...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(true){
            if(!checkMaster()){
//                perform election
                this.performElection();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!this.checkMaster()){
                    this.announceMaster();
                }
            }
        }
    }

    private void performElection(){
        for(Connection connection:connections.values()){
            if(connection.nodeId > nodeId && connection.in != null && connection.out != null){
                sender.sendMessage(new Message(connection.nodeId,"Elected",
                        "System"));
            }
        }
    }

    private synchronized boolean checkMaster(){
        if(connections.get(masterId.get()) == null || connections.get(masterId.get()).in == null
                || connections.get(masterId.get()).out == null) {
            setMasterId(0);
            return false; // master is dead
        }
        return true; // master is alive;
    }

    private synchronized void setMasterId(int id){
        masterId.set(id);
    }

    public void electionHandler(Message message){
        if(message.messageBody.split(" ")[0].equals("Elected")) {
            if(message.nodeId > this.nodeId) {
                setMasterId(message.nodeId);
                System.out.println(this.masterId.get()+" is MASTER");
            }
        }
    }

    private void announceMaster(){
        for(Connection connection:connections.values()){
            if(connection.in != null && connection.out != null) {
                sender.sendMessage(new Message(connection.nodeId, "Elected " + Integer.toString(nodeId),
                        "System"));
            }
        }
        this.masterId.set(this.nodeId);
        System.out.println(this.masterId.get()+" is MASTER");
    }
}

package DistributedSystem.Heart;
import DistributedSystem.Communication.Connection;
import DistributedSystem.Communication.Message;
import DistributedSystem.Communication.Sender;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

// class for heartbeat
public class Heart extends Thread{
    public ConcurrentHashMap<Integer, Connection> connections;
    public Sender sender;
    public long beatGap;

    public Heart(ConcurrentHashMap<Integer,Connection>connections,Sender sender){
        this.connections = connections;
        this.sender = sender;
        this.beatGap = 3000;
    }

    @Override
    public void run() {
        System.out.println("Heart thread is on...");
        while(true){
            for(Connection connection:connections.values()){
                if(new Date().getTime()-connection.lastAlive >= beatGap){
//                    dead;
                    connection.dead();
                    System.out.println(connection.nodeId+" is DEAD");
                }
                sender.sendMessage(new Message(connection.nodeId,"Alive","System"));
            }
//            next check will be at some interval of time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


//    beat handling
    public void beatHandler(Message message){
        try{
            connections.get(message.nodeId).setAliveTime();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

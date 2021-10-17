package DistributedSystem.Work;

import DistributedSystem.Communication.Message;
import DistributedSystem.Communication.Receiver;
import DistributedSystem.Communication.Sender;
import DistributedSystem.Election.BullyElection;
import DistributedSystem.Heart.Heart;

// class that perform the work (new work can also be added)
public class Worker extends Thread{
    Receiver receiver;
    Sender sender;
    Heart heart;
    BullyElection bullyElection;

    public Worker(Receiver receiver, Sender sender, Heart heart, BullyElection bullyElection){
        this.receiver = receiver;
        this.sender = sender;
        this.heart = heart;
        this.bullyElection = bullyElection;
    }

    @Override
    public void run() {
//        doing the work
        System.out.println("Worker thread is on...");
        while (true){
            Message message = receiver.getMessage();
            if(message != null){
                if(message.category.equals("System")){
//                    for now, it is the only heartbeat check
                    if(message.messageBody.startsWith("Alive"))
                        heart.beatHandler(message);
                    else if(message.messageBody.startsWith("Elected")){
                        bullyElection.electionHandler(message);
                    }
                }
            }
        }
    }
}

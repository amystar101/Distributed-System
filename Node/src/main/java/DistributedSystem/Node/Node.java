package DistributedSystem.Node;

import DistributedSystem.Communication.Connection;
import DistributedSystem.Communication.Message;
import DistributedSystem.Communication.Receiver;
import DistributedSystem.Communication.Sender;
import DistributedSystem.Election.BullyElection;
import DistributedSystem.Heart.Heart;
import DistributedSystem.Work.Worker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

// Node class represent a node in the distributed server or cluster.
public class Node {
    public static void main(String[] args) throws ParseException, IOException {
        int id; // stores the id of this  node
        final int PORT;
        ConcurrentHashMap<Integer, Connection>connections = new ConcurrentHashMap<>(); // stores connection
//        comparators for the message queues
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Message m1 = (Message) o1;
                Message m2 = (Message) o2;
                if(m1.category.equals(m2.category))
                    return 0;
                else if(m1.category.equals("System"))
                    return 1;
                return -1;
            }
        };

        Receiver receiver;
        Sender sender;
        Heart heart;
        Worker worker;
        BullyElection bullyElection;

        id = Integer.parseInt(args[0]);

        System.out.println("Node Id = "+id);
        JSONParser parser = new JSONParser();
        JSONObject configurations = (JSONObject) ((JSONObject) parser.parse(
                new FileReader(System.getProperty("user.dir")+"/Config.json"))).get(Integer.toString(id));
        PORT = Integer.parseInt((String) configurations.get("PORT")); // stores the port of current node

        receiver = new Receiver(PORT,connections,comparator);
        sender = new Sender(id,comparator,connections);

        JSONArray toConnect = (JSONArray) configurations.get("connection");
//        loading the connection configuration from config.json
        for(Object obj:toConnect){
            String[] connectNode = ((String) obj).split(" "); // contains the id and the port of the connection
            connections.put(Integer.parseInt(connectNode[0]),new Connection(Integer.parseInt(connectNode[0]),
                    Integer.parseInt(connectNode[1])));
        }
        heart = new Heart(connections,sender);
        bullyElection = new BullyElection(id,connections,sender);
        worker = new Worker(receiver,sender,heart,bullyElection);

        receiver.start();
        sender.start();
        heart.start();
        worker.start();
        bullyElection.start();

    }
}

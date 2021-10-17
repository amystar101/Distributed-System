package DistributedSystem.Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.DataTruncation;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// Connection class will maintain the information connections in between the nodes in cluster
public class Connection {
    public int nodeId;
    public int port;
    public DataInputStream in = null;
    public DataOutputStream out = null;
    public long lastAlive;

    public Connection(int nodeId, int port) {
        this.nodeId = nodeId;
        this.port = port;
        lastAlive = new Date().getTime();
    }
    public Connection(DataInputStream in){
        this.in = in;
    }

//    method to set connection dead
    public synchronized void dead(){
        in = null;
        out = null;
        lastAlive = new Date().getTime();
    }

    //    setting alive
    public synchronized void setAliveTime(){
        lastAlive = new Date().getTime();
    }
}

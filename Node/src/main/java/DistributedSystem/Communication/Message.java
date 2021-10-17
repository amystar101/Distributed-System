package DistributedSystem.Communication;

public class Message {
    public int nodeId;
    public String messageBody;
    public String category;

    public Message(int nodeId,String messageBody){
        this.nodeId = nodeId;
        this.messageBody = messageBody;
        this.category = "General";
    }

    public Message(int nodeId,String messageBody,String category){
        this.nodeId = nodeId;
        this.messageBody = messageBody;
        this.category = category;
    }
}

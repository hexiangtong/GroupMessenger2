package edu.buffalo.cse.cse486586.groupmessenger2;

/**
 * Created by Xiangtong He on 3/1/16.
 */
public class Message implements Comparable<Message> {
    String type;
    String msg;
    int mid;
    int sender;
    int sqNum;
    String isDeliverable;

    public Message(String type, String msg, int mid, int sender, int sqNum, String isDeliverable){
        this.type = type;
        this.mid = mid;
        this.sender = sender;
        this.sqNum = sqNum;
        this.msg = msg;
        this.isDeliverable = isDeliverable;
    }


    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public int getMid(){
        return mid;
    }

    public void setMid(int mid){
        this.mid = mid;
    }

    public int getSender(){
        return sender;
    }

    public void setSender(int sender){
        this.sender = sender;
    }

    public int getSqNum(){
        return sqNum;
    }

    public void setSqNum(){
        this.sqNum = sqNum;
    }

    public String getMsg(){
        return msg;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }


    public String toString(){
        return type + "\t" + msg + "\t"+ mid + "\t" + sender + "\t" + sqNum + "\t" + isDeliverable;
    }

    @Override
    public int compareTo(Message message) {

        if(this.sqNum > message.sqNum){
            return 1;
        }else if(this.sqNum < message.sqNum){
            return -1;
        }
    return 0;
    }
}

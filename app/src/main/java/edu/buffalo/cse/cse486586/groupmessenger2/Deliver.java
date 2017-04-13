package edu.buffalo.cse.cse486586.groupmessenger2;

/**
 * Created by xiangtong he on 3/1/16.
 */
public class Deliver {
    String type;
    String msg;
    int mid;
    int sqNum;
    int sender;
    String isDeliverable;

    public Deliver(String type, String msg, int mid, int sender, int sqNum, String isDeliverable){
        this.type = type;
        this.msg = msg;
        this.mid = mid;
        this.sqNum = sqNum;
        this.sender = sender;
        this.isDeliverable = isDeliverable;
    }

//    public static boolean isSame(int a, int b){
//        if(a == b){
//           return true;
//        }else {
//            return false;
//        }
//    }

//    void reNew(int sqNum, boolean isDeliverable){
//        this.sqNum = sqNum;
//        this.isDeliverable = true;
//    }

    public String toString(){
        return type + "\t" + msg + "\t"+ mid + "\t" + sender + "\t" + sqNum + "\t" + isDeliverable;
    }
}
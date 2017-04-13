package edu.buffalo.cse.cse486586.groupmessenger2;

import java.util.Comparator;

/**
 * Created by Xiangtong He on 3/17/16.
 */
public class MessageComparator implements Comparator<Message> {
    @Override
    public int compare(Message lhs, Message rhs) {

        if (lhs.sqNum > rhs.sqNum) {
            return 1;
        } else if (lhs.sqNum < rhs.sqNum) {
            return -1;
        } else {
            if (lhs.sender > rhs.sender) {
                return 1;
            } else if (lhs.sender < rhs.sender) {
                return -1;
            } else {
                if (lhs.isDeliverable.equals("true") && rhs.isDeliverable.equals("false")) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
        }
    }
}

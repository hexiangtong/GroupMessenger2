package edu.buffalo.cse.cse486586.groupmessenger2;

import java.util.Comparator;

/**
 * Created by zhouyuan57 on 3/25/16.
 */
public class msgComparator implements Comparator<Message> {
    @Override
    public int compare(Message lhs, Message rhs) {

        if (lhs.sqNum > rhs.sqNum) {
            return 1;
        } else if (lhs.sqNum < rhs.sqNum) {
            return -1;
        }
        return 0;
    }
}

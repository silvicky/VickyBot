package Utility;

public class ThreadStatus {
    public static String threadStatus(Thread s)
    {
        if(s==null)return "NULL";
        if(s.isAlive())return "UP";
        return "DOWN";
    }
}

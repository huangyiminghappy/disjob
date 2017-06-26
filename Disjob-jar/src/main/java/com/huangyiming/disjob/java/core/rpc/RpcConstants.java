package com.huangyiming.disjob.java.core.rpc;

public interface RpcConstants {

    public static final int MAX_FRAME_LENGTH = 8192;
   
    public static final int LENGTH_FIELD_OFFSET = 5;
    
    public static final int LENGTH_FIELD_LENGTH = 4; 
    
    public static final int CONNECT_TIMEOUT = 3000; 
    public static final int READ_IDLE_TIME = 0;
    public static final int WRITE_IDLE_TIME = 0;
    public static final int ALL_IDLE_TIME = 90;
    public static final int CONNECT_TRY_DELAY = 3;
    
}

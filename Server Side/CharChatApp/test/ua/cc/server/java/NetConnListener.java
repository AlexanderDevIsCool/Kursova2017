package ua.cc.server.java;

public interface NetConnListener {
    
    void onConnectReady(NetConnect netconn);
    void onReceive(NetConnect netconn, String value);
    void onDisconnect(NetConnect netconn);
    void onException(NetConnect netconn, Exception e);
    
}

package ua.cc.server.java;

import static ua.cc.server.java.ServerLogs.writeLogs;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerPart implements NetConnListener{
    
    private final ArrayList<NetConnect> connections = new ArrayList<>();
    private SQLConnection1 sqlcon = new SQLConnection1();
    NetConnect net = new NetConnect();
    
    public static void main(String[] args) {    
        new ServerPart();       
    }
  
    private ServerPart() {
        System.out.println("Server running...");
        System.out.println(net.socket.getInetAddress());
        
        try (ServerSocket serversocket = new ServerSocket(3333);) {
            while(true) {
                try {
                    new NetConnect(this, serversocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: "+ e);
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectReady(NetConnect netconn) {  
        connections.add(netconn);
        writeLogs("Client connected: " + netconn);
        System.out.println("Client connected: " + netconn);
     }

    @Override
    public synchronized void onReceive(NetConnect netconn, String value) {
       try {
           System.out.println(value);
           filterMsg(value);
       } catch (SQLException e) {
           System.out.println("SQL SendMsg Exception!");
       }
    }    
    
    @Override
    public synchronized void onDisconnect(NetConnect netconn) {
        connections.remove(netconn);
        writeLogs("Client disconnected: " + netconn);
        System.out.println("Client disconnected: " + netconn);
    }

    @Override
    public synchronized void onException(NetConnect netconn, Exception e) {
        writeLogs("TCPConnection exception: +" + e);
        System.out.println("TCPConnection exception: +" + e);
    }
    
    public synchronized void sendToUser(String value, String IPAddress) {
        final int cnt = connections.size();
        for(int i = 0; i < cnt; i++) {
            System.out.println("sendToYouser: "+value+", IP: "+IPAddress);
            if(connections.get(i).getIP().equals(IPAddress)) {
                connections.get(i).sendString(value);
                System.out.println("Send to:"+connections.get(i).getIP()+", msg: "+value);
            }
        }
    }
    
    private synchronized void filterMsg(String msg) throws SQLException{
        int i = 3, d = 1;
        while(i < 10){
            if(msg.trim().substring(msg.getBytes().length-i, msg.getBytes().length-d).equals("id")){
                sendToUser(msg.substring(0, msg.getBytes().length-i),
                        sqlcon.getIPAddress(msg.trim().substring(msg.getBytes().length-i, msg.getBytes().length)));
                return;
            }
            i++;d++;
        }
        if(msg.startsWith("INSERT")) {
            sqlcon.sqlInsert(msg);
            System.out.println("INSERT: "+msg);
        }
        else if(msg.startsWith("UPDATE")) {
            sqlcon.sqlUpdate(msg);
            System.out.println("UPDATE:"+msg);
        }
        else if(msg.startsWith("SELECT")) {
            sqlcon.sqlSelect(msg);
            System.out.println("SELECT: "+msg);
        }    
    }
}

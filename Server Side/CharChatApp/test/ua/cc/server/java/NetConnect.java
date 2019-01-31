package ua.cc.server.java;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

public class NetConnect {
    public final Socket socket;
    private final Thread rxThread;
    private final NetConnListener nettListener;
    private final BufferedReader in;
    private final BufferedWriter out;
    
    public NetConnect(NetConnListener nettListener, Socket socket) throws IOException{
        this.nettListener = nettListener;
        this.socket = socket;
        socket.getInputStream();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        
        rxThread = new Thread(new Runnable(){
        @Override
        public void run(){
            try {
                nettListener.onConnectReady(NetConnect.this);
                while(!rxThread.isInterrupted()){
                    nettListener.onReceive(NetConnect.this, in.readLine());
                }
            } catch (IOException e) {
                nettListener.onException(NetConnect.this, e);
            } finally {
                nettListener.onDisconnect(NetConnect.this);
            }
        }
        });
        rxThread.start();
    }

    NetConnect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public synchronized void sendString(String value) {
        try {
            out.write("ServerSay"+value+"\r\n");
            out.flush();
        } catch (IOException e) {
            nettListener.onException(NetConnect.this, e);
        }
    }
    
    public synchronized void sendUserChange(String msgSQL) {
        try{
            out.write(msgSQL+"\n\r");
            out.flush();
        } catch (IOException e){
            nettListener.onException(NetConnect.this, e);
        }
    }
    
    public synchronized void disconnect() {
    rxThread.interrupt();
    try{
        socket.close();
    } catch (IOException e) {
        nettListener.onException(NetConnect.this, e);    
    }
    }
    
    public synchronized String getIP() {
        return socket.getInetAddress().toString();
    }
    
    @Override
    public synchronized String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": "+ socket.getPort();
    }
}

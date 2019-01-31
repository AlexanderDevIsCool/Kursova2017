package ua.cc.server.java;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

public class NetConnect {

    public Socket socket = null;
    private Thread MainThread, serverThread, onlineThread, clientThread,
            commandThread, infoThread, msglistThread, myinfoThread, deleteThread,
            toolsThread;
    private NetConnListener nettListener;
    private DataInputStream din = null;
    private DataOutputStream dout = null;

    public NetConnect(NetConnListener nettListener, Socket socket) throws IOException {
        this.nettListener = nettListener;
        this.socket = socket;
        dout = new DataOutputStream(socket.getOutputStream());
        din = new DataInputStream(socket.getInputStream());
        MainThread = new Thread(() -> {
            try {
                nettListener.onConnectReady(NetConnect.this);
                while (!MainThread.isInterrupted()) {
                    int length = din.readInt();
                    if (length > 0) {
                        byte[] message = new byte[length];
                        din.readFully(message, 0, length);
                        String msg = new String(message);
                        System.out.println("ServerRecive: ");
                        switch (msg.substring(0, 4)) {
                            case "SERV":
                                serverThread = new Thread(() -> {
                                    nettListener.onReceive(NetConnect.this, msg.substring(4));
                                });
                                if (serverThread.isAlive()) {
                                    serverThread.interrupt();
                                }
                                serverThread.start();
                                break;
                            case "ONLI":
                                onlineThread = new Thread(() -> {
                                    nettListener.ImOnline(NetConnect.this);
                                });
                                if (onlineThread.isAlive()) {
                                    onlineThread.interrupt();
                                }
                                onlineThread.start();
                                break;
                            case "CLIE":
                                clientThread = new Thread(() -> {
                                    nettListener.UserToUser(NetConnect.this, msg.substring(4));
                                });
                                if (clientThread.isAlive()) {
                                    clientThread.interrupt();
                                }
                                clientThread.start();
                                break;
                            case "INFO":
                                infoThread = new Thread(() -> {
                                    nettListener.getInfo(NetConnect.this, msg.substring(4));
                                });
                                if (infoThread.isAlive()) {
                                    infoThread.interrupt();
                                }
                                infoThread.start();
                                break;
                            case "COMD":
                                commandThread = new Thread(() -> {
                                    nettListener.onClientCommand(NetConnect.this, msg.substring(4));
                                });
                                if (commandThread.isAlive()) {
                                    commandThread.interrupt();
                                }
                                commandThread.start();
                                break;
                            case "MSLI":
                                msglistThread = new Thread(() -> {
                                    nettListener.onGiveMessageList(NetConnect.this, msg.substring(4));
                                });
                                if (msglistThread.isAlive()) {
                                    msglistThread.interrupt();
                                }
                                msglistThread.start();
                                break;
                            case "MYIN":
                                myinfoThread = new Thread(() -> {
                                    nettListener.getMyInfo(NetConnect.this);
                                });
                                if (myinfoThread.isAlive()) {
                                    myinfoThread.interrupt();
                                }
                                myinfoThread.start();
                                break;
                            case "DELE":
                                deleteThread = new Thread(() -> {
                                    nettListener.infoDelete(NetConnect.this, msg.substring(4));
                                });
                                if (deleteThread.isAlive()) {
                                    deleteThread.interrupt();
                                }
                                deleteThread.start();
                                break;
                            case "TOOL":
                                toolsThread = new Thread(() -> {
                                    nettListener.giveTools(NetConnect.this, msg.substring(4));
                                });
                                if (toolsThread.isAlive()) {
                                    toolsThread.interrupt();
                                }
                                toolsThread.start();
                                break;
                            default:
                                sendToClient("UNKNOWN MSG: " + msg);
                        }
                    }
                }
            } catch (IOException e) {
                nettListener.onException(NetConnect.this, e);
            } finally {
                nettListener.onDisconnect(NetConnect.this);
            }
        });
        MainThread.start();
    }

    public synchronized void sendToUser(String Message) {
        try {
            byte[] msg = Message.getBytes(Charset.forName("UTF-8"));
            dout.writeInt(msg.length);
            dout.write(msg);
        } catch (IOException ex) {
            System.out.println("sendToUser Exception !");
        }
    }

    public synchronized void sendToClient(String msgSQL) {
        try {
            byte[] msg = msgSQL.getBytes(Charset.forName("UTF-8"));
            dout.writeInt(msg.length);
            dout.write(msg);
        } catch (IOException ex) {
            System.out.println("sendToClient Exception !");
        }
    }

    public synchronized void sendBytes(byte[] bytes) {
        try {
            dout.writeInt(bytes.length);
            dout.write(bytes);
        } catch (IOException ex) {
            System.out.println("sendToClient Exception !");
        }
    }

    public synchronized void disconnect() {
        MainThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            nettListener.onException(NetConnect.this, e);
        }
    }

    public synchronized String getLogs() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

    @Override
    public synchronized String toString() {
        return socket.getInetAddress().toString();
    }
}

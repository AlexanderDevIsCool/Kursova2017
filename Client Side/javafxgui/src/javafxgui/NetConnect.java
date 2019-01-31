package javafxgui;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

public class NetConnect {

    public Socket socket = null;
    private Thread MainThread, loginThread, msgThread, infoThread, msglistThread, userslistThread, myinfoThread, deleteThread;
    private final NetConnListener nettListener;
    private final ClientActionListener clientListener;
    private DataInputStream din = null;
    private DataOutputStream dout = null;
    public volatile static Integer threadTOOLLength = -1, threadMSLILength = -1, threadMyInfoLength = -1, threadInfoLength = -1;
    private volatile boolean threadMSLITriger = false, threadTOOLTriger = false, threadMyInfoTriger = false, threadInfoTriger = false;
    public volatile static Thread[] mslistTreads, toolsThreads, myinfoThreads, infoThreads;
    public volatile int toolsCounter = -1, messageListCounter = -1, threadMyInfoCounter = -1, threadInfoCounter = -1;

    public NetConnect(ClientActionListener clientListener, NetConnListener nettListener, String ipAddr, int Port) throws IOException {
        this(clientListener, nettListener, new Socket(ipAddr, Port));
    }

    public NetConnect(ClientActionListener clientListener, NetConnListener nettListener, Socket socket) throws IOException {
        this.clientListener = clientListener;
        this.nettListener = nettListener;
        this.socket = socket;

        dout = new DataOutputStream(socket.getOutputStream());
        din = new DataInputStream(socket.getInputStream());

        MainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nettListener.onConnectReady(NetConnect.this);
                    while (!MainThread.isInterrupted()) {
                        int length = din.readInt();
                        if (length > 0) {
                            byte[] message = new byte[length];
                            din.readFully(message, 0, length);
                            String hello = new String(message);
                            System.out.println("Recive: ");
                            switch (hello.substring(0, 4)) {
                                case "LOGI":
                                    loginThread = new Thread(() -> {
                                        nettListener.onReceive(NetConnect.this, hello.substring(4));
                                    });
                                    if (loginThread.isAlive()) {
                                        loginThread.interrupt();
                                    }

                                    loginThread.start();
                                    break;
                                case "MESG":
                                    msgThread = new Thread(() -> {
                                        clientListener.MsgRecive(NetConnect.this, hello.substring(4));
                                    });
                                    if (msgThread.isAlive()) {
                                        msgThread.interrupt();
                                    }

                                    msgThread.start();
                                    break;
                                case "INFO":
                                    System.out.println("threadInfoLength: "+threadInfoLength);
                                    if (threadInfoLength > 1) {
                                        threadInfoCounter++;
                                        if (threadInfoTriger == false) {
                                            threadInfoTriger = true;
                                            infoThreads = new Thread[threadInfoLength];
                                        }
                                        System.out.println("messageListCounter: " + threadInfoCounter + "threadMSLILength: " + threadInfoLength);
                                        System.out.println("Thread[" + threadInfoCounter + "]: start! MESSAGE: ");
                                        infoThreads[threadInfoCounter] = new Thread(() -> {
                                            clientListener.InfoRecive(NetConnect.this, hello.substring(4));

                                        });
                                        infoThreads[threadInfoCounter].start();

                                    } else if (hello.substring(4, 8).equalsIgnoreCase("SIZE")) {
                                        threadInfoLength = Integer.parseInt(hello.substring(8));
                                        System.out.println("SIZE: " + threadInfoLength);
                                    }
                                    break;
                                case "MSLI":
                                    System.out.println("threadMSLILength: " + threadMSLILength);
                                    if (threadMSLILength > 1) {
                                        messageListCounter++;
                                        System.out.println("threadMSLITriger: " + threadMSLITriger);
                                        if (threadMSLITriger == false) {

                                            threadMSLITriger = true;
                                            System.out.println("In thread MSLI Intitialized");
                                            mslistTreads = new Thread[threadMSLILength];
                                            System.out.println("mslistTreads.length: "+mslistTreads.length);
                                        }
                                        System.out.println("messageListCounter: " + messageListCounter + "threadMSLILength: " + threadMSLILength);
                                        System.out.println("Thread[" + messageListCounter + "]: start! MESSAGE: ");
                                        mslistTreads[messageListCounter] = new Thread(() -> {
                                            clientListener.MessageListRecive(NetConnect.this, hello.substring(4));

                                        });
                                        mslistTreads[messageListCounter].start();

                                    } else if (hello.substring(4, 8).equalsIgnoreCase("SIZE")) {
                                        threadMSLILength = Integer.parseInt(hello.substring(8));
                                        System.out.println("SIZE: " + threadMSLILength);
                                    }
                                    break;
                                case "MYIN":
                                   System.out.println("threadMyInfoLength: "+threadMyInfoLength+","
                                           + "threadMyInfoCounter: "+threadMyInfoCounter);
                                   if (threadMyInfoLength > 1) {
                                        threadMyInfoCounter++;
                                        if (threadMyInfoTriger == false) {
                                            threadMyInfoTriger = true;
                                            System.out.println("array initialized: " + threadMyInfoLength);
                                            myinfoThreads = new Thread[threadMyInfoLength];
                                        }
                                                                
                                        myinfoThreads[threadMyInfoCounter] = new Thread(() -> {
                                            System.out.println("Thread run(): ");
                                            clientListener.getMyInfo(NetConnect.this, hello.substring(4));
                                        });
                                        System.out.println("Thread[" + threadMyInfoCounter + "]: start!");
                                        myinfoThreads[threadMyInfoCounter].start();
                                       

                                    } else if (hello.substring(4, 8).equalsIgnoreCase("SIZE")) {
                                        threadMyInfoLength = Integer.parseInt(hello.substring(8));
                                        System.out.println("SIZE: "+threadMyInfoLength);
                                    }
                                    break;
                                case "TOOL":
                                    if (threadTOOLLength > 1) {
                                        toolsCounter++;
                                        if (threadTOOLTriger == false) {
                                            threadTOOLTriger = true;
                                            System.out.println("array initialized: " + threadTOOLLength);
                                            toolsThreads = new Thread[threadTOOLLength];
                                        }
                                        System.out.println("toolsCounter: "+toolsCounter+", threadTOOLLength: "+threadTOOLLength);
                                      
                                        toolsThreads[toolsCounter] = new Thread(() -> {
                                            System.out.println("Thread run(): ");
                                            clientListener.ccResourcesRecive(NetConnect.this, hello.substring(4));
                                        });
                                        System.out.println("Thread[" + toolsCounter + "]: start!");
                                        toolsThreads[toolsCounter].start();
                                       

                                    } else if (hello.substring(4, 8).equalsIgnoreCase("SIZE")) {
                                        threadTOOLLength = Integer.parseInt(hello.substring(8));
                                        System.out.println("SIZE: " + threadTOOLLength);
                                    }
                                    break;

                            }
                        }
                    }
                } catch (IOException e) {
                    nettListener.onException(NetConnect.this, e);
                    clientListener.ClientException(NetConnect.this, e);
                } finally {
                    clientListener.ClientDiscon(NetConnect.this);
                    nettListener.onDisconnect(NetConnect.this);
                }
            }
        });
        MainThread.start();
    }
 
    public void stopToolThreads() {
        if (threadTOOLLength > 1) {
            for (int i = 0; i < toolsThreads.length; i++) {
                System.out.println("Thread[" + i + "]: isAlive - " + toolsThreads[i].isAlive());
                if (!toolsThreads[i].isAlive()) {
                    System.out.println("Thread[" + i + "]: interrupt isAlive - " + toolsThreads[i].isAlive());
                    toolsThreads[i].interrupt();
                }
            }
            threadTOOLLength = -1;
            toolsCounter = -1;
            threadTOOLTriger = false;
        }
    }

    public void stopMsListThreads() {
        System.out.println("threadMSLILength = "+threadMSLILength);
        int a = 0;
        if (threadMSLILength > 1) {
            for (int i1 = 0; i1 < mslistTreads.length; i1++) {
                System.out.println("Thread["+i1+"] interrupt");
                mslistTreads[i1].interrupt();
            }
            threadMSLILength = -1;
            messageListCounter = -1;
            threadMSLITriger = false;;
        }
    }

    public void stopMyInfoThreads() {
        System.out.println("MYINFOO THREAD: "+threadMyInfoLength);
        if (threadMyInfoLength > 1) {
            for (Thread myinfoThread1 : myinfoThreads) {
                if (!myinfoThread1.isAlive()) {
                    myinfoThread1.interrupt();
                }
            }
            threadMyInfoLength = -1;
            threadMyInfoCounter = -1;
            threadMyInfoTriger = false;
        }
    }
    
    public void stopInfoThreads() {
        if (threadInfoLength > 1) {
            for (int i = 0; i < infoThreads.length; i++) {
                System.out.println("ThreadInfo[" + i + "]: isAlive - " + infoThreads[i].isAlive());
                if (!infoThreads[i].isAlive()) {
                    System.out.println("ThreadInfo[" + i + "]: inturrupt isAlive - " + infoThreads[i].isAlive());
                    infoThreads[i].interrupt();
                }
            }
            threadInfoLength = -1;
            threadInfoCounter = -1;
            threadInfoTriger = false;
        }
    }

    public synchronized void sendString(String who, String message) {
        try {
            String send = who + message;
            System.out.println("sendString: ");
            byte[] msg = send.getBytes(Charset.forName("UTF-8"));
            dout.writeInt(msg.length);
            dout.write(msg);
        } catch (IOException e) {
            nettListener.onException(NetConnect.this, e);
            clientListener.ClientException(NetConnect.this, e);
        }
    }

    public synchronized void sendBytes(byte[] bytes) {
        try {
            System.out.println("sendBytes: ");
            System.out.println("bytes length: " + bytes.length);
            dout.writeInt(bytes.length);
            dout.write(bytes);
        } catch (IOException e) {
            nettListener.onException(NetConnect.this, e);
            clientListener.ClientException(NetConnect.this, e);
        }
    }

    public synchronized void disconnect() {
        MainThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            nettListener.onException(NetConnect.this, e);
            clientListener.ClientException(NetConnect.this, e);
        }
    }

    public synchronized String getIP() {
        return socket.getLocalSocketAddress().toString();
    }

    @Override
    public synchronized String toString() {
        return "Client-TCPConnection: " + socket.getLocalSocketAddress().toString() + ": " + socket.getPort();
    }
}

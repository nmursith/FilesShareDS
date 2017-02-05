package main;

import JavaBootStrapServer.Neighbour;
import javafx.application.Platform;
import similarity.CosineDistance;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by nifras on 1/10/17.
 */
public class CommandReceiver extends Thread {
    public int port;
    ArrayList<String> fileSearched = new ArrayList<>();
    FileShareDSController fileShareDSController;
    private DatagramSocket sock;
    private boolean isStopped;
    
    public CommandReceiver(FileShareDSController fileShareDSController, int port) {
        this.port = port;
        this.isStopped = false;
        this.fileShareDSController = fileShareDSController;
    }
    
    @Override
    public void run() {
        super.run();
        isStopped = false;
        try {
            sock = new DatagramSocket(port);
            System.out.println("Command Receiver  created at " + port + " . Waiting for incoming commands...");
            
            while (!isStopped) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);
                
                byte[] data = incoming.getData();
                String s = new String(data, 0, incoming.getLength());
                
                //echo the details of incoming data - client ip : client port - client message
                //echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
                
                StringTokenizer st = new StringTokenizer(s, " ");
                System.out.println(s);
                String length = st.nextToken();
                String command = st.nextToken();
                
                
                if (command.equals(Constants.SER)) {
                    String IP = st.nextToken();
                    String port = st.nextToken();
                    String file = st.nextToken();
                    if (file.contains("*")) {
                        file = file.replace("*", " ");
                    }
                    
                    String hops = st.nextToken();
                    String timestamp = st.nextToken();
                    
                    String com = file + timestamp;
                    if (!fileSearched.contains(com)) {
                        //check whther I have
                        fileSearched.add(com);
                        System.out.println(com);
                        int hop = Integer.parseInt(hops);
                        hop = hop - 1;
                        String files = "";
                        
                        int count = 0;
                        for (String filesIhave : fileShareDSController.items) {
                            
                            try {
                                Double results = new CosineDistance().apply(filesIhave.toLowerCase(), file.toLowerCase());
                                //System.out.println("Testing.... " + results);
                                
                                if (results > 0.4) {
                                    files = filesIhave.replace(" ", "*") + ",";
                                    count++;
                                }
                                //System.out.println(files);
                            } catch (Exception e) {
                                e.printStackTrace();
                                
                            }
                            
                        }
                        
                        if (files.length() > 0) {
                            
                            //new CommandSender(IP, Integer.parseInt(port), hop, fileShareDSController.getMyself(), files, count).start();
                            String request = "SEROK " + count + " " + fileShareDSController.getMyself().getIp() + " " + fileShareDSController.getMyself().getPort() + " " + hop + " " + files;
                            new CommandSender(IP, Integer.parseInt(port), request).start();
                        }
                        String request = "SER " + IP + " " + port + " " + file + " " + hop + " " + timestamp;
                        //fileShareDSController.search(request);
                        
                    }
                    
                } else if (command.equals(Constants.JOIN)) {
                    String IP = st.nextToken();
                    String port = st.nextToken();
                    String request = checkJoin(IP, Integer.parseInt(port));
                    new CommandSender(IP, Integer.parseInt(port), request).start();
                    
                } else if (command.equals(Constants.LEAVE)) {
                    String IP = st.nextToken();
                    String port = st.nextToken();
                    String request = leaveCheck(IP, Integer.parseInt(port));
                    new CommandSender(IP, Integer.parseInt(port), request).start();
                    
                } else if (command.equals(Constants.SEROK)) {
                    //length SEROK no_files IP port hops filename1 filename2 ... ...
                    String no_files = st.nextToken();
                    String IP = st.nextToken();
                    String port = st.nextToken();
                    String hops = st.nextToken();
                    StringTokenizer files = new StringTokenizer(st.nextToken(), ",");
//jh
                    while (files.hasMoreTokens()) {
                        String fileName = files.nextToken();
                        if (fileName.contains("*")) {
                            fileName = fileName.replace("*", " ");
                        }
                        final String finalFileName = fileName;
                        Platform.runLater(() -> fileShareDSController.availableItems.add(finalFileName));
                    }
                } else if (command.equals(Constants.JOINOK)) {
                    //length JOINOK value
                    String value = st.nextToken();
                    if (value.equals(Constants.SUCCESS)) {
                        System.out.println("Successfully Joined");
                        
                    } else {
                        System.out.println("Error in Joinng");
                    }
                    
                } else if (command.equals(Constants.LEAVEOK)) {
                    String value = st.nextToken();
                    if (value.equals(Constants.SUCCESS)) {
                        System.out.println("Successfully Left");
                        
                    } else {
                        System.out.println("Error in Leaving");
                    }
                    
                } else if (command.equals(Constants.ERROR)) {
                    System.err.println("ERROR");
                    
                }
            }
            
            
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String leaveCheck(String IP, int port) {
        String reply = "";
        for (int i = 0; i < fileShareDSController.nodes.size(); i++) {
            if (fileShareDSController.nodes.get(i).getPort() == port) {
                if (fileShareDSController.nodes.get(i).getIp().equals(IP)) {
                    reply = "LEAVEOK 0";
                    fileShareDSController.nodes.remove(i);
                    return reply;
                }
                
                
            }
        }
        reply = "LEAVEOK 9999";
        return reply;
    }
    
    public String checkJoin(String IP, int port) {
        String reply = "";
        for (int i = 0; i < fileShareDSController.nodes.size(); i++) {
            if (fileShareDSController.nodes.get(i).getPort() == port) {
                if (fileShareDSController.nodes.get(i).getIp().equals(IP)) {
                    reply = "JOINOK 9999";
                    return reply;
                }
                
            }
        }
        Neighbour node = new Neighbour(IP, port, "null");
        fileShareDSController.nodes.add(node);
        reply = "JOINOK 0";
        return reply;
    }
    
    public void stopThread() {
        isStopped = true;
        Thread t = this;
        t.stop();
        t = null;
    }
    
}

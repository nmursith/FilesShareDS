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
    private DatagramSocket sock;
    private boolean isStopped;
    ArrayList<String> fileSearched = new ArrayList<>();
    FileShareDSController fileShareDSController;

    public CommandReceiver(FileShareDSController fileShareDSController, int port){
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
            System.out.println("Command Receiver  created at "+ port+" . Waiting for incoming commands...");

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

                if(command.equals(Constants.SER)) {
                    String IP = st.nextToken();
                    String port = st.nextToken();
                    String file= st.nextToken();
                    String hops= st.nextToken();
                    String timestamp= st.nextToken();

                    String com = file+timestamp;
                    if(!fileSearched.contains(com)){
                        //check whther I have
                        int hop = Integer.parseInt(hops);
                        hop = hop - 1;
                        String files="";
                        System.out.println();
                        for (String filesIhave: fileShareDSController.items) {
                            try {
                                if(new CosineDistance().apply(filesIhave, file)> 0.8)
                                    files = filesIhave + " ";
                            }
                            catch (Exception e){

                            }

                        }

                        if(files.length()>0){
                            new FileSender(IP, Integer.parseInt(port), hop, fileShareDSController.getMyself(), files);
                        }
                        String request = "SER "+  IP +" " + port + " \"" + file + "\" "+ hop + " " +timestamp ;
                        fileShareDSController.search(request);

                    }


                }



            }


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  void stopThread(){
        isStopped = true;
        Thread t = this;
        t.stop();
        t = null;
    }

}

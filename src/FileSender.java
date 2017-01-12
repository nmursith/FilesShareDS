import JavaBootStrapServer.Neighbour;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by nifras on 1/12/17.
 */

public class FileSender extends Thread {
    public int port;
    public String IP;
    private DatagramSocket sock;
    private boolean isStopped;
    private Neighbour myself;
    private String command;

    public FileSender(String IP, int port, Neighbour myself, String command){
        this.IP = IP;
        this.port = port;
        this.myself = myself;
        this.command = command;
        this.isStopped = false;
    }

    @Override
    public void run() {
        super.run();
        isStopped = false;
        try {
            int port = Math.abs(new Random().nextInt())% 5000 +3000;
            sock = new DatagramSocket();
                /*
                REG IP_address port_no username
                e.g., 0036 REG 129.82.123.45 5001 1234abcd
                 */
            InetAddress IA = InetAddress.getLocalHost();
            String ip = ServerController.getIP();
            //length SEROK no_files IP port hops filename1 filename2 ... ...

            //String request = "SEROK "+  myself.getIp() +" " + myself.getPort() + " " + files;
            String request = command;
            int size = request.length() + 5;
            DecimalFormat myFormatter = new DecimalFormat("0000");
            String output = myFormatter.format(size);
            request =  output +" " + request ;

            System.out.println(request);
            InetAddress server = InetAddress.getByName(IP);

            DatagramPacket dpReply = new DatagramPacket(request.getBytes() , request.getBytes().length , server , port);
            sock.send(dpReply);
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            sock.receive(incoming);


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

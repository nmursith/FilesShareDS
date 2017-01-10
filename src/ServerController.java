import JavaBootStrapServer.Neighbour;

import java.io.IOException;
import java.net.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

/**
 * Created by nifras on 1/10/17.
 */
class ServerController {

    public  boolean isConnected = false;
    DatagramSocket sock = null;
    String serverAddrees = "127.0.0.1";
    int serverport = 55555;
    List<Neighbour> nodes = new ArrayList<Neighbour>();
    public static void main(String[] args){
        for( int i=0; i<40; i++) {
            new ServerController().connect();
        }
    }

    public void sendData(String file){

    }
    public void connect(){
        while (true) {
            try {

                int port = Math.abs(new Random().nextInt())% 5000 +3000;
                sock = new DatagramSocket();
                /*
                REG IP_address port_no username
                e.g., 0036 REG 129.82.123.45 5001 1234abcd
                 */
                InetAddress IA = InetAddress.getLocalHost();
                String ip = getIP();
                String host = IA.getHostName();

                String request = "REG "+  ip +" " + port + " " + host;
                int size = request.length() + 5;
                DecimalFormat myFormatter = new DecimalFormat("0000");
                String output = myFormatter.format(size);
                request =  output +" " + request ;

                System.out.println(request);
                InetAddress server = InetAddress.getByName(serverAddrees);

                DatagramPacket dpReply = new DatagramPacket(request.getBytes() , request.getBytes().length , server , serverport);
                sock.send(dpReply);
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                String s = new String(data, 0, incoming.getLength());

                //echo the details of incoming data - client ip : client port - client message
                System.out.println(s);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                break;
            }

        }
    }
    public void disconnect(){

    }
    public String getIP(){
        Enumeration e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        String IP = null;
        boolean assigned = false;
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                IP = i.getHostAddress();
                //System.out.println(i.getHostAddress());
                if(IP.contains(Constants.IPLIKE)) {
                    assigned = true;
                    break;
                }
            }
            if(assigned)
                break;
        }
        //System.out.println("IP: "+ IP);
        return IP;
    }

}

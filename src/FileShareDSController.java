import JavaBootStrapServer.Neighbour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.*;
import java.security.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by nifras on 1/12/17.
 */
public class FileShareDSController {
    ServerController serverController;
    @FXML
    ListView<String> filesIhave;// = new ListView<String>();
    ObservableList<String> items = FXCollections.observableArrayList ();

    @FXML
    ListView<String> filesAvailable;// = new ListView<String>();
    ObservableList<String> availableItems = FXCollections.observableArrayList ("ass","sfsd","sfsd");


    ArrayList<Neighbour> nodes;
    Neighbour myself;
    int hops = 20;


    public void init() {
        System.out.println(System.currentTimeMillis());
        filesIhave.setItems(items);
        filesAvailable.setItems(availableItems);

        serverController = new ServerController(this);
        nodes = serverController.connect();

        items.addAll(new ReadFile().getFilePerNode());

        if(!nodes.isEmpty()) {
            myself = nodes.get(nodes.size() -1);
            nodes.remove(nodes.size() - 1);
        }

        //search();

    }
    public void search(){

        String file = "\"Mission Impossible\"";
        String request = "SER "+  myself.getIp() +" " + myself.getPort() + " \"" + file + "\" "+ hops + " " +System.currentTimeMillis() ;
        search(request);
    }

    public void search(String request){
        //length SER IP port file_name hops

        int size = request.length() + 5;
        DecimalFormat myFormatter = new DecimalFormat("0000");
        String output = myFormatter.format(size);
        request =  output +" " + request ;

        System.out.println(request);

        for (Neighbour node: nodes) {
           try {
               DatagramSocket sock = new DatagramSocket();
               InetAddress server = InetAddress.getByName(node.getIp());

               DatagramPacket dpReply = new DatagramPacket(request.getBytes() , request.getBytes().length , server , node.getPort());
               sock.send(dpReply);
               byte[] buffer = new byte[65536];
               DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
               sock.receive(incoming);

               byte[] data = incoming.getData();
               String s = new String(data, 0, incoming.getLength());
               System.out.println(s);
               sock.close();
           } catch (UnknownHostException e) {
               e.printStackTrace();
           } catch (SocketException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }


        }

    }

    public ObservableList<String> getItems() {
        return items;
    }

    public void setItems(ObservableList<String> items) {
        this.items = items;
    }

    public ObservableList<String> getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(ObservableList<String> availableItems) {
        this.availableItems = availableItems;
    }

    public Neighbour getMyself() {
        return myself;
    }

    public void setMyself(Neighbour myself) {
        this.myself = myself;
    }
}

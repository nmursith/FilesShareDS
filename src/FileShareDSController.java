import JavaBootStrapServer.Neighbour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.security.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by nifras on 1/12/17.
 */
public class FileShareDSController {
    ServerController serverController;
    ListView<String> filesIhave = new ListView<String>();
    ObservableList<String> items = FXCollections.observableArrayList ();

    ListView<String> filesAvailable = new ListView<String>();
    ObservableList<String> availableItems = FXCollections.observableArrayList ();

    ArrayList<String> fileSearched = new ArrayList<>();
    ArrayList<Neighbour> nodes;
    Neighbour myself;
    int hops = 20;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    public void init() {
        filesIhave.setItems(items);
        filesAvailable.setItems(availableItems);

        serverController = new ServerController();
        nodes = serverController.connect();
        System.out.println(nodes);
        if(!nodes.isEmpty()) {
            myself = nodes.get(nodes.size() -1);
            nodes.remove(nodes.size() - 1);
        }

        search("Lord of the rings");

    }
    public void search(String file){
        //length SER IP port file_name hops
        String request = "SER "+  myself.getIp() +" " + myself.getPort() + " \"" + file + "\" "+ hops + " " + timestamp;
        int size = request.length() + 5;
        DecimalFormat myFormatter = new DecimalFormat("0000");
        String output = myFormatter.format(size);
        request =  output +" " + request ;
        System.out.println(request);


    }
}

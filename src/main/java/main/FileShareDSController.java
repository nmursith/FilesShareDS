package main;

import JavaBootStrapServer.Neighbour;
import distributed.services.FileSearch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.MyFilleList;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by nifras on 1/12/17.
 */
public class FileShareDSController {
    static ArrayList<String> files;
    public Button connectButton;
    public TextField searchFile;
    public Button searchButton;
    ServerController serverController;
    Stage stage;
    @FXML
    ListView<String> filesIhave;// = new ListView<String>();
    ObservableList<String> items = FXCollections.observableArrayList();
    @FXML
    ListView<String> filesAvailable;// = new ListView<String>();
    ObservableList<String> availableItems = FXCollections.observableArrayList();
    ArrayList<Neighbour> nodes;
    Neighbour myself;
    int hops = 20;
    private boolean isConnected = false;
    
    public static ArrayList<String> getFiles() {
        return files;
    }
    
    public void init() {
        System.out.println(System.currentTimeMillis());
        filesIhave.setItems(items);
        filesAvailable.setItems(availableItems);
        files = MyFilleList.getInstance().getFiles();
        serverController = new ServerController(this);
        items.addAll(files);
        
        //search();
        
    }
    
    public void search() {
        
        String file = searchFile.getText();//"\"Mission Impossible\"";
        file = file.replace(" ", "*");
        for (Neighbour node : nodes) {
            URL url = null;
            try {
                url = new URL("http://" + node.getIp() + ":8282/ws/search");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            
            //1st argument service URI, refer to wsdl document above
            //2nd argument is service name, refer to wsdl document above
            QName qname = new QName("http://services.distributed/", "FileSearchImplService");
            
            Service service = Service.create(url, qname);
            
            FileSearch hello = service.getPort(FileSearch.class);
            
            // System.out.println(hello.search("Microsoft"));
//jh
            StringTokenizer files = new StringTokenizer(hello.search(file), ",");
            while (files.hasMoreTokens()) {
                String fileName = files.nextToken();
                if (fileName.contains("*")) {
                    fileName = fileName.replace("*", " ");
                }
                final String finalFileName = fileName;
                Platform.runLater(() -> availableItems.add(finalFileName));
            }
        }
        // String request = "SER "+  myself.getIp() +" " + myself.getPort() + " "+file +" "+ hops + " " +System.currentTimeMillis() ;
        // search(request);
    }
    
    public void search(String request) {
        //length SER IP port file_name hops
        
        int size = request.length() + 5;
        DecimalFormat myFormatter = new DecimalFormat("0000");
        String output = myFormatter.format(size);
        request = output + " " + request;
        
        System.out.println(request);
        
        for (Neighbour node : nodes) {
            
            final String finalRequest = request;
            new Thread(() -> {
                try {
                    DatagramSocket sock = new DatagramSocket();
                    InetAddress server = InetAddress.getByName(node.getIp());
                    
                    DatagramPacket dpReply = new DatagramPacket(finalRequest.getBytes(), finalRequest.getBytes().length, server, node.getPort());
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
            }).start();
            
            
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
    
    public void download(ActionEvent actionEvent) {
        
        try {
            String file = filesAvailable.getSelectionModel().getSelectedItem();
            items.add(file);
            files.add(file);
        } catch (Exception e) {
            
        }
        availableItems.clear();
        
    }
    
    public void delete(ActionEvent actionEvent) {
    }
    
    public void connect(ActionEvent actionEvent) {
        
        if (!isConnected) {
            
            nodes = serverController.connect();
            if (serverController.isConnected()) {
                connectButton.setText("Disconnect");
                isConnected = true;
                //length JOIN IP_address port_no
                JoinLeave("JOIN");
            }
            
        } else {
            serverController.disconnect();
            if (!serverController.isConnected()) {
                connectButton.setText("Connect");
                isConnected = false;
                JoinLeave("LEAVE");
                //ength LEAVE IP_address port_no
                
            }
            
        }
        
        
        searchButton.setDisable(false);
        this.setTitle();
        
        
        if (!nodes.isEmpty()) {
            myself = nodes.get(nodes.size() - 1);
            nodes.remove(nodes.size() - 1);
        }
    }
    
    public void JoinLeave(String command) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < nodes.size(); i++) {
                    String request = command + " " + myself.getIp() + " " + myself.getPort();
                    new CommandSender(nodes.get(i).getIp(), nodes.get(i).getPort(), request).start();
                }
            }
        });
    }
    
    public void setTitle() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.setTitle("FileShareDS@" + myself.getIp() + ":" + myself.getPort());
            }
        });
        
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

package distributed.services;

import main.ServerController;
import similarity.CosineDistance;
import util.MyFilleList;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;

/**
 * Created by Udeesha on 1/29/2017.
 */
@javax.jws.WebService(endpointInterface = "distributed.services.FileSearch")
public class FileSearchImpl implements FileSearch {
    
    public static void runService() {
        FileSearchImpl wsInstance = new FileSearchImpl();
        Endpoint.publish("http://" + ServerController.getIP() + ":8282/ws/search", wsInstance);
    }
    
    @Override
    public String searchFile(String name) {
        return name + " hello";
    }
    
    @Override
    public String search(String s) {
        System.out.println("searching for: " + s);
        
        ArrayList<String> files = MyFilleList.getInstance().getFiles();
        int count = 0;
        String file = "";
        for (String filesIhave : files) {
            
            try {
                Double results = new CosineDistance().apply(filesIhave.toLowerCase(), s.toLowerCase());
                //System.out.println("Testing.... " + results);
                
                if (results > 0.4) {
                    file += (filesIhave.replace(" ", "*") + ",");
                    count++;
                }
                //System.out.println(files);
            } catch (Exception e) {
                e.printStackTrace();
                
            }
        }
        return file;
    }
}

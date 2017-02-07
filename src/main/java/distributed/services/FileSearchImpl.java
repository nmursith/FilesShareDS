package distributed.services;

import JavaBootStrapServer.Neighbour;
import main.CommandSender;
import main.Constants;
import main.FileShareDSController;
import main.ServerController;
import similarity.CosineDistance;
import util.MyFilleList;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Udeesha on 1/29/2017.
 */
@javax.jws.WebService(endpointInterface = "distributed.services.FileSearch")
public class FileSearchImpl implements FileSearch {
    ArrayList<String> fileSearched = new ArrayList<>();
    static FileShareDSController fileShareDSController;
    static String myIP;

    static int myPort;
    public static void runService(String ip, int port) {
        myIP = ip;
        myPort = port;
        FileSearchImpl wsInstance = new FileSearchImpl();
        fileShareDSController = FileShareDSController.getFileShareDSController();
        Endpoint.publish("http://" + ServerController.getIP() + ":"+port+"/ws/search", wsInstance);
    }
    
    @Override
    public String searchFile(String name) {
        return name + " hello";
    }
    
    @Override
    public void search(String s) {
        System.out.println("searching for: " + s);
        
        ArrayList<String> files = MyFilleList.getInstance().getFiles();
        int count = 0;
        String file_send = "";


        StringTokenizer st = new StringTokenizer(s, " ");
        System.out.println(s);
        //String length = st.nextToken();
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
                System.out.println("Searching..........");
                fileSearched.add(com);
                System.out.println(com);
                int hop = Integer.parseInt(hops);
                hop = hop - 1;



                for (String filesIhave : files) {

                    try {
                        Double results = new CosineDistance().apply(filesIhave.toLowerCase(), file.toLowerCase());
                        //System.out.println("Testing.... " + results);

                        if (results > 0.4) {
                            file_send += (filesIhave.replace(" ", "*") + ",");
                            count++;
                        }
                        //System.out.println(files);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (file_send.length() > 0) {

                    //new CommandSender(IP, Integer.parseInt(port), hop, fileShareDSController.getMyself(), files, count).start();
                    String response = "SEROK " + count + " " + myIP + " " + myPort + " " + hop + " " + file_send;

                    //new CommandSender(IP, Integer.parseInt(port), request).start();

                        URL url = null;
                        try {
                            url = new URL("http://" + IP + ":"+port+"/ws/search");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        //1st argument service URI, refer to wsdl document above
                        //2nd argument is service name, refer to wsdl document above
                        QName qname = new QName("http://services.distributed/", "FileSearchImplService");

                        Service service = Service.create(url, qname);

                        FileSearch hello = service.getPort(FileSearch.class);
                        hello.update(response, com);
                        // System.out.println(hello.search("Microsoft"));

                }
                else {
                    file = file.replace(" ", "*");
                    String request = "SER " + IP + " " + port + " " + file + " " + hop + " " + timestamp;


                    for (Neighbour node : fileShareDSController.getNodes()) {
                        URL url = null;
                        try {
                            url = new URL("http://" + node.getIp() + ":"+node.getPort()+"/ws/search");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        //1st argument service URI, refer to wsdl document above
                        //2nd argument is service name, refer to wsdl document above
                        QName qname = new QName("http://services.distributed/", "FileSearchImplService");

                        Service service = Service.create(url, qname);

                        FileSearch hello = service.getPort(FileSearch.class);
                        hello.search(request);
                        // System.out.println(hello.search("Microsoft"));

                    }
                }

                //fileShareDSController.search(request);

            }

        }


    }

    @Override
    public void update(String s, String query) {
        System.out.println(s);
        //String response = "SEROK " + count + " " + myIP + " " + myPort + " " + hop + " " + files
        StringTokenizer st = new StringTokenizer(s, " ");
        String comamand = st.nextToken();
        String no_files = st.nextToken();
        String IP = st.nextToken();
        String port = st.nextToken();

        String hops = st.nextToken();
        String file = st.nextToken();
        fileShareDSController.addFiles(file, hops, query);


    }
}

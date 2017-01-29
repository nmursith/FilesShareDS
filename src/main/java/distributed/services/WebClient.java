package distributed.services;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * Created by Udeesha on 1/29/2017.
 */
public class WebClient {

    public static void main(String[] args) throws Exception {

        URL url = new URL("http://localhost:8282/ws/search");

        //1st argument service URI, refer to wsdl document above
        //2nd argument is service name, refer to wsdl document above
        QName qname = new QName("http://services.distributed/", "FileSearchImplService");

        Service service = Service.create(url, qname);

        FileSearch hello = service.getPort(FileSearch.class);

        System.out.println(hello.search("Microsoft"));

    }
}

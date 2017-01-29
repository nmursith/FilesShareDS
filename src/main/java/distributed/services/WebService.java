package distributed.services;

import javax.jws.WebMethod;
import javax.xml.ws.Endpoint;

/**
 * Created by Udeesha on 1/29/2017.
 */
@javax.jws.WebService
public class WebService {
    public static void main(String[] args) {
        WebService wsInstance = new WebService();
        Endpoint.publish("http://localhost:8282/dsfs", wsInstance);
    }
    
    @WebMethod
    public String getHelloWorld() {
        return "Hello, World!";
    }
    
    @WebMethod
    public String getPersonalizedHelloWorld(String yourName) {
        return "Hello, " + yourName + "!";
    }
}

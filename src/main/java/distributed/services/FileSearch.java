package distributed.services;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Created by Udeesha on 1/29/2017.
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface FileSearch {
    @WebMethod
    String searchFile(String name);
    
    @WebMethod
    void search(String s);

    @WebMethod
    void update(String s);
}

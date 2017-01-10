/**
 * Created by nifras on 1/10/17.
 */
public class Constants {
    static String SUCCESS = "0"; // – request is successful, no nodes in the system
    static String SUCESSFULL = "1";// or 2 – request is successful, 1 or 2 nodes' contacts will be returned
    static String SUCESSFULL2 = "2";
    static String FAILED_COMMAND = "9999"; // – failed, there is some error in the command
    static String FAILED_ALREADY_REGISTERED_YOURSELF = "9998";// – failed, already registered to you, unregister first
    static String FAILED_ANOTHER_USER_REGISTERED =  "9997";// – failed, registered to another user, try a different IP and port
    static String FAILED_BS_FULLE = "9996"; // – failed, can’t register. BS full.
}

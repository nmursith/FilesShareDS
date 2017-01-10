import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nifras on 1/10/17.
 */
public class ReadFile {

    public static  void main(String []args){
        new ReadFile().readFileList();
    }
    public void readFileList(){
        ArrayList<String > files = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("FileNames.txt"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
                files.add(line);
            }
            String everything = sb.toString();
            br.close();
            System.out.println("Count "+ files.size());
            System.out.println(everything);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}

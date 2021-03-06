package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nifras on 1/10/17.
 */
public class ReadFile {


    public static  void main(String []args){
        new ReadFile().getFilePerNode();
    }

    public ArrayList<String> getQueries(){
        ArrayList<String > filesList = readFileList("Queries.txt");
        return filesList;

    }
    public  ArrayList<String> getFilePerNode(){
        int n = Math.abs(new Random().nextInt())%2 +3;

        ArrayList<String> file = new ArrayList<String>();
        ArrayList<String > filesList = readFileList("FileNames.txt");
//        ArrayList<String > temp = readFileList();
//        for (int j=0; j<20; j++) {
        for (int i = 0; i < n; i++) {
            int rand = Math.abs(new Random().nextInt() )% (filesList.size()-1);
            String temp = filesList.get(rand);
            if(!file.contains(temp)) {
                file.add(temp);
            }
            else{
                i = i-1;
            }
//                System.out.println(file.);
/*                if(!temp.contains(file[i])){
                    temp.add(file[i]);
                }
                else {
                    System.err.println("found");
                }*/
        }
//        }
//        System.out.println("Temp  Count : " + temp.size());
        return file;
    }
    public ArrayList<String> readFileList(String fileName){
        ArrayList<String > files = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
//                line = line.replace("\n","");

                files.add(line);
            }
            String everything = sb.toString();
            br.close();
            //System.out.println("Count "+ files.size());
            // System.out.println(everything);
            return files;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String [] getBootStrapSever(){
        String [] IP = new String[2];// = "192.168.8.100";
        //String iplike= "192.168";
        try {
            BufferedReader br = new BufferedReader(new FileReader("server.txt"));
            StringBuilder sb = new StringBuilder();
            IP[0] = br.readLine();
            IP[1] = br.readLine();
            IP[0] = IP[0].replace("\n","");
            IP[1] = IP[1].replace("\n","");
            br.close();
            System.out.println("BootStrapSever Addres   :   "+IP[0]);
            System.out.println("IP like Addres   :   "+IP[1]);
            return IP;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IP;
    }
}

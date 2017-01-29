package util;

import main.ReadFile;

import java.util.ArrayList;

/**
 * Created by Udeesha on 1/29/2017.
 */
public class MyFilleList {
    private static MyFilleList ourInstance = new MyFilleList();

    ArrayList<String> files;

    private MyFilleList() {
        files = new ReadFile().getFilePerNode();
        for (String s : files) {
            System.out.print(","+s);
        }
    }

    public static MyFilleList getInstance() {
        return ourInstance;
    }

    public ArrayList<String> getFiles() {
        return files;
    }
}

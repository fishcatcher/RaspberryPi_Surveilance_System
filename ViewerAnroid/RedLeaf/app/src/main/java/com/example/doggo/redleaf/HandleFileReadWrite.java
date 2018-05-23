package com.example.doggo.redleaf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HandleFileReadWrite {

    public HandleFileReadWrite()
    {

    }

    public boolean readFromFile(File path, String returnInfo[])
    {
        return readFromFile(path, returnInfo, 1);
    }

    public boolean readFromFile(File path, String returnInfo[], int start)
    {
        if (path == null || returnInfo == null || returnInfo.length == 0 || start <= 0 || !path.exists()) {
            return false;
        }

        if (!path.exists()) {
            createFile(path);
        }
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);

            String dummy;
            for (int i=1; i<start; i++)
                dummy = br.readLine();

            for (int i=0; i<returnInfo.length; i++) {
                returnInfo[i] = br.readLine();
            }

            br.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean writeToFile(File path, String writeLine)
    {
        if (path == null)
            return false;
        try {
            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(writeLine);
            bw.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void createFile(File path)
    {
        if (path == null)
            return;

        if (!path.exists()) {
            boolean flag;
            try {
                do {
                    flag = path.createNewFile();
                } while (!flag);
            } catch (IOException e) {
                //Some errors here
            }
        }
    }

}

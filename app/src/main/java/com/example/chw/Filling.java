package com.example.chw;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Filling {
    public void writeToFile(String filename, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFromFile(String filename, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
    public void saveFileToFolder(String filename, String data){
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Feedback/";    // it will return root directory of internal storage
            File root = new File(path);
            if (!root.exists()) {
                root.mkdirs();       // create folder if not exist
            }
            File file = new File(path + filename);
            if (!file.exists()) {
                file.createNewFile();   // create file if not exist
            }
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(data);
            buf.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

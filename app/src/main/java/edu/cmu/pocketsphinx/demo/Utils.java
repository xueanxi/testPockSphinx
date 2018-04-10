package edu.cmu.pocketsphinx.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 4/10/18.
 */

public class Utils {
    public static void main(String[] args) throws IOException {
        File dict = new File("/local/dict/cmudict-en-us2.dict");
        File newdict = new File("/local/dict/newdict.dict");
        File animalFile = new File("/local/dict/anima");

        ArrayList<String> animals = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(animalFile));
        String animal = null;
        while (null != (animal = r.readLine())) {
            animals.add(animal.trim());
        }

        if(animalFile == null) {
            System.out.print("animal is null");
            return;
        }
        if(newdict.exists()){
            newdict.delete();
        }
        newdict.createNewFile();
        if(dict.exists()){
            System.out.print("dict  exist");
            BufferedReader br = new BufferedReader(new FileReader(dict));
            BufferedWriter bw = new BufferedWriter(new FileWriter(newdict));
            String line = null;
            while (null != (line = br.readLine())){
                if(animals.contains(line.split(" ")[0])){
                    bw.write(line);
                    bw.newLine();
                }
            }

            bw.flush();
            bw.close();
            br.close();

        }else{
            System.out.print("dict not exist");
        }
    }
}

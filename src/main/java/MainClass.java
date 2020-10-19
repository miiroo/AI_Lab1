import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainClass {



    public static void main (String args[]) throws Exception {

        //CSV file reading
        CSVReader reader = new CSVReader(new FileReader("C:/Users/mirOo/IdeaProjects/AIlab1/src/main/resources/data.csv"), ',' , '"' , 0);
        //read all dates into on array
        List<String[]> arrayData = reader.readAll();

        String[] row = arrayData.get(0);
        System.out.println("Data count: " + (arrayData.size()-1)); //First one is attribute row
        System.out.println("Attribute count: " + (row.length-1)); //Last one is CLASS

        //Deleting rows with missed attributes
        DataClean dtClean = new DataClean();
        arrayData = dtClean.cleanData(arrayData);

        //After cleaning
        System.out.println("Data count after cleaning: " + (arrayData.size()-1)); //First one is attribute row

        //Change to binary
        arrayData= dtClean.fromCatToBinary(arrayData);

        //First 5 and attributes
        for(int i=0; i<6; i++){
            row = arrayData.get(i);
            System.out.println(Arrays.toString(row));
        }


        //Divide to 2 arrays [learning 0.75/ test 0.25]
        Random rn = new Random();
        List<String[]> arrayData2 = arrayData;
        List<String[]> learningArray = new ArrayList<String[]>();
        List<String[]> testArray = new ArrayList<String[]>();

        int learningArraySize = (int)(0.75*arrayData.size()-1);

        for (int i=0; i<learningArraySize; i++) {
            int d = rn.nextInt(arrayData2.size()-1);
            if (d > 1) {
                learningArray.add(arrayData2.get(d));
                arrayData2.remove(d);
            }
        }

        testArray = arrayData2;
        testArray.remove(0);
        arrayData2.clear();

        
        //Show arrays if needed
        System.out.println("Do you want to see Learning and Test arrays? (y/n)");
        BufferedReader rder = new BufferedReader(new InputStreamReader((System.in)));
        if (rder.readLine().equals("y")) {
            System.out.println("LEARNING ARRAY: ");
            System.out.println();
            for(String[] rowForOut : learningArray){
                System.out.println(Arrays.toString(rowForOut));
            }
            System.out.println();
            System.out.println("TEST ARRAY: ");
            for(String[] rowForOut : testArray){
                System.out.println(Arrays.toString(rowForOut));
            }
            System.out.println();
        }

    }
}

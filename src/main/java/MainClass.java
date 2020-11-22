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
        //read all dates into one array
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
        List<String[]> arrayData2 = new ArrayList<String[]>(arrayData);
        List<String[]> learningArray = new ArrayList<String[]>();
        List<String[]> testArray = new ArrayList<String[]>();

        //Change amount of learning array
        int learningArraySize = (int)(0.75*(arrayData.size()-1));

        for (int i=0; i<learningArraySize; i++) {
            int d = rn.nextInt(arrayData2.size()-1);
            if (d > 1) {
                learningArray.add(arrayData2.get(d));
                arrayData2.remove(d);
            }
        }

        //Test array contains all the remaining data
        testArray.addAll(arrayData2);
        testArray.remove(0);
        arrayData2.clear();

        System.out.println("Learning array count: " + (learningArray.size()));
        System.out.println("Test array count: " + (testArray.size()));

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



        System.out.println("Decision tree: ");
       // System.out.println(arrayData.size());
        int[] atr = new int[13];

        for (int i=0; i<12; i++) {
            atr[i] = 0;
        }
        atr[12] = 2;
        row = arrayData.get(0);
        ID3 id3 = new ID3(learningArray, atr, row);
        System.out.println("Test array: ");
        id3.testTree(testArray);
        System.out.println();
        System.out.println("Learning array: ");
        id3.testTree(learningArray);
        System.out.println();
        System.out.println("Depth: " + id3.getDepth(id3.decisionTree.root, 1, 1));
        System.out.println();
        System.out.println("Do you want to see Decision Tree? (y/n)");
        rder = new BufferedReader(new InputStreamReader((System.in)));
        if (rder.readLine().equals("y")) {
            System.out.println();
            id3.printTree();
        }
    }
}

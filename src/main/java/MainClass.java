import au.com.bytecode.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

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
        System.out.println("Leafs count: "+id3.leafCounter);
        System.out.println();
        System.out.println("Do you want to see Decision Tree? (y/n)");
        rder = new BufferedReader(new InputStreamReader((System.in)));
        if (rder.readLine().equals("y")) {
            System.out.println();
            id3.printTree();
        }
        System.out.println("////////////////////////////////////////////");
        System.out.println();
        System.out.println("Log regression: ");
        LogRegression l = new LogRegression(learningArray);
        System.out.println("Learning array: ");
        l.testModel(learningArray);
        System.out.println();
        System.out.println("Test array: ");
        l.testModel(testArray);
        System.out.println();
        System.out.println("Coefficients: ");
        l.showCoef();

        //One layer peceptron
        System.out.println();
        System.out.println("///////////////////////////////////////////////");
        System.out.println("One layer perceptron");
        System.out.println();
        int epoch = 10;
        System.out.println("Epoch amount: "+epoch);
        OneLayerPerceptron olp = new OneLayerPerceptron(learningArray, epoch, false);
        System.out.println("Learning array: ");
        olp.testModel(learningArray);
        System.out.println();
        System.out.println("Test array: ");
        olp.testModel(testArray);
        System.out.println();
        System.out.println("W matrix: ");
        olp.showW();


        //Neuraal Network
        System.out.println();
        System.out.println("////////////////////////////////////////");
        System.out.println("Neural network");
        System.out.println();
        int epoch2 = 100;
        System.out.println("Epoch amount = " + epoch2);
        NeuralNetwork nn = new NeuralNetwork(epoch2, 1);
    //    nn.addLayer(12);
        nn.addLayer(6);
        nn.addLayer(3);
        nn.trainModel(learningArray);
        System.out.println("Learning array test:");
        nn.testModel(learningArray);
        System.out.println();
        System.out.println("Test array test:");
        nn.testModel(testArray);
        System.out.println();
        nn.printWeights();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();

        ///////////////////////////LAB 6////////////////////////////////////
        for (int k = 0; k < 30; k++)
            System.out.println("*");

        System.out.println("//////////////////////");
        System.out.println("LAB 6. Neural Netrwork.");
        System.out.println();

        //get from xlsx file////////////////////////////////////////
        List<String[]> arrayDatas = new ArrayList<String[]>();
        int i = 0;
        FileInputStream inputStream = new FileInputStream(new File("C:/Users/mirOo/IdeaProjects/AIlab1/src/main/resources/data2.xlsx"));

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
  //      rowIterator.next();
        while(rowIterator.hasNext()) {
            Row row1 = rowIterator.next();
            Iterator<Cell> cellIterator = row1.cellIterator();
            arrayDatas.add(new String[9]);
            int j = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                CellType cellType = cell.getCellTypeEnum();
                switch (cellType) {
                    case STRING: arrayDatas.get(i)[j] = cell.getStringCellValue(); break;
                    case NUMERIC: arrayDatas.get(i)[j] =Double.toString(cell.getNumericCellValue()); break;
                }
                j++;
            }
            i++;
        }
        //end: get from xlsx file/////////////////////////////

        row = arrayDatas.get(0);
        System.out.println("Data count: " + (arrayDatas.size())); //First one is attribute row
        System.out.println("Attribute count: " + (row.length-1)); //Last one is CLASS

      //  for(int k=0; k<6; k++){
      //      row = arrayDatas.get(k);
     //       System.out.println(Arrays.toString(row));
     //   }


        arrayData2 = new ArrayList<String[]>(arrayDatas);


        //Change amount of learning array
       learningArraySize = (int)(0.75*(arrayDatas.size()));
        learningArray.clear();
        testArray.clear();
        for (int k=0; k<learningArraySize; k++) {
            int d = rn.nextInt(arrayData2.size());
            if (d > 1) {
                learningArray.add(arrayData2.get(d));
                arrayData2.remove(d);
            }
        }


        //Test array contains all the remaining data
        testArray.addAll(arrayData2);
        testArray.remove(0);
        arrayData2.clear();


        epoch2 = 100;
        System.out.println("Epoch = "+epoch2);
        NeuralNetworkL nn2 = new NeuralNetworkL(epoch2, 1);
        nn2.addLayer(2);
        nn2.addLayer(4);

        nn2.trainModel(learningArray);


        System.out.println("Learning array: ");
        nn2.testModel(learningArray);
        System.out.println("Test array: ");
        nn2.testModel(testArray);
        System.out.println();
        nn2.printWeights();

        System.out.println();
        System.out.println("//////////");
        System.out.println("Decision tree. Regression.");
        atr = new int[9];

        for (int a=0; a<8; a++) {
            atr[a] = 0;
        }
        atr[8] = 2;
        row = arrayDatas.get(0);
        RegressionTree rt = new RegressionTree(learningArray, atr, row);
        System.out.println("Test array: ");
        rt.testTree(testArray);
        System.out.println();
        System.out.println("Learning array: ");
        rt.testTree(learningArray);
        System.out.println();
        System.out.println("Depth: " + rt.getDepth(rt.decisionTree.root, 1, 1));
        System.out.println();
        System.out.println("Leafs count: "+rt.leafCounter);
        System.out.println();
       // System.out.println("Do you want to see Decision Tree? (y/n)");
      //  rder = new BufferedReader(new InputStreamReader((System.in)));
       // if (rder.readLine().equals("y")) {
      //      System.out.println();
      //      rt.printTree();
    //    }

    }

}

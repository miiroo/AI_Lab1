import javax.swing.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ID3 {
//TO DO: add best dilimeter finder
   // (from min to max with step = (max-min)/count
   // check min entropy for each

        public  class GraphW extends  JFrame {
            public List<JLabel> labelList = new ArrayList<JLabel>();
            JPanel panel = new JPanel(null);
            public GraphW() {

                super("AI");
                this.setBounds(0,0, 1000, 800);
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            }

            public void Show() {
                for(int i=0; i<labelList.size();i++) panel.add(labelList.get(i));
                add(panel);
            }

        }
//attributes types
    /*
    0- age: age of the patient (Int)
    1- anaemia: decrease of red blood cells or hemoglobin (boolean)
    2- creatinine phosphokinase (CPK): level of the CPK enzyme in the blood (Int)
    3- diabetes: if the patient has diabetes (boolean)
    4- ejection fraction: percentage of blood leaving the heart at each contraction (int) Max 100
    5- high blood pressure: if the patient has hypertension (boolean)
    6- platelets: platelets in the blood (int)
    7- serum creatinine: level of serum creatinine in the blood (float)
    8- serum sodium: level of serum sodium in the blood (int)
    9- sex: woman=0 or man=1 (boolean)
    10- smoking: if the patient smokes or not (boolean)
    11- time: follow-up period (int)
    12- [target] death event: if the patient deceased during the follow-up period (boolean)
     */


    private static int classID = 12;
    public Tree decisionTree;
    // 0 - not used 1 - used 2 - class attr
    private String[] atributes1;

    public int getDepth(Node ns, int answer, int curLevel) {
        if (curLevel < answer) curLevel = answer;

        if(ns.leftChild != null) {
            answer++;
            curLevel = getDepth(ns.leftChild, answer, curLevel);
        }
        else {
            answer++;
        }
            answer--;
        if(ns.rightChild != null) {
            answer++;
            curLevel = getDepth(ns.rightChild, answer, curLevel);
        }
        else {
            answer++;
        }
            answer--;

        return curLevel;
    }

    public void printTree() throws IOException {
        decisionTree.root.printTree();
    }

    public  ID3 (List<String[]> trainingData, int[] attrList, String[] attributes ) {
        decisionTree = new Tree();
        Node root = generateTree(trainingData, attrList, attributes );
        decisionTree.root = root;
        atributes1 = attributes;
    }

    public void testTree(List<String[]> datas) {
        int counter = 1;
        float accuracy = 0;
        for (int i=0; i< datas.size(); i++) {
            String rightAnswer = datas.get(i)[12];
            float currAnswer;
            Node nd = decisionTree.root;
            while (nd.isNode) {
                if (Float.parseFloat(datas.get(i)[nd.attrIndex]) < nd.key) nd = nd.leftChild;
                else nd = nd.rightChild;
            }
            currAnswer = nd.key;
            if (currAnswer == Float.parseFloat(rightAnswer)) accuracy++;
            else {
             //   out.println(counter+"."+Arrays.toString(datas.get(i)));
              //  out.println();
                counter++;
            }
        }
        accuracy /= datas.size();
        out.println("Accuracy: " + accuracy);
    }


    private float getBestDel(List<String[]> datas, int atribute) {
        float max = -999999;
        float min = 9999999;
        float curNum = 0;
        float bestDelim = 0;
        float minEntropy = 10;
        float average = 0;

        List<String[]> leftSide = new ArrayList<String[]>();
        List<String[]> rightSide = new ArrayList<String[]>();


            for (int i = 0; i < datas.size(); i++) {
                if (atribute == 6 || /*atribute == 2 ||*/ atribute == 8 || atribute == 11 || atribute == 4) {
                    if (atribute == 6)
                        curNum = Float.parseFloat(datas.get(i)[atribute]) / 10000;
                    else
                    curNum = Float.parseFloat(datas.get(i)[atribute]) / 1000;

                    if (max < curNum) max = curNum;
                    if (min > curNum) min = curNum;
                } else {
                    curNum = Float.parseFloat(datas.get(i)[atribute]);
                    if (max < curNum) max = curNum;
                    if (min > curNum) min = curNum;
                }
                average += curNum;
            }//0 2 4 6 8 11

        if (max != min) {
            average /= datas.size();
            float avg2=average;
            float step = ((max - min) / datas.size())*2;
            if (step > 0.000009) {
             //   out.println("=====================");
            //    out.println("min: "+min);
            //    out.println("max: "+max);
             //   out.println("avg: "+average);
//                out.println("step: "+step);

                if (datas.size() > 10) {
                        while (min < average) {
                            min+= step;
                            average-=step;
                        }
                        average = avg2;
                        while (max > average) {
                            max-=step;
                            average+=step;
                        }
                        average = avg2;
                }
            }

            if (step > 0.000009) {
                float i = min;
                while (i <= max) {
                    for (int j = 0; j < datas.size(); j++) {
                        if (Float.parseFloat(datas.get(j)[atribute]) < i) {
                            leftSide.add(datas.get(j));
                        } else //>countAv
                            rightSide.add(datas.get(j));
                    }
                    float left = 0;
                    float right = 0;
                    left = ((float) leftSide.size() / (float) datas.size()) * getEntropy(leftSide, classID, 1);
                    right = ((float) rightSide.size() / (float) datas.size()) * getEntropy(rightSide, classID, 1);
                    float entr = 0;
                    entr = left + right;
                    if (entr < minEntropy) {
                        minEntropy = entr;
                        bestDelim = i;
                    }

                    leftSide.clear();
                    rightSide.clear();
                    if (atribute == 0 ||  atribute == 6) {
                        if (step < 1) step++;
                        else i += (int) step;
                    }
                    else
                    i += step;
                }
                if (atribute == 6 ||/* atribute == 2 ||*/ atribute == 8 || atribute == 11 || atribute == 4) {
                    if (atribute == 6) bestDelim *= 10000;
                    else bestDelim *= 1000;
                }
            } else bestDelim = average;
        } else bestDelim = max;
        return bestDelim;
    }

    private  Node generateTree (List<String[]> datas, int[] attrList, String[] attributes) {


        short counter = 0;
        int exitCode = 0;
        int possibleAttributes = 12;

        Node currNode = new Node();
        //check possible attributes
        for (int i=0; i<possibleAttributes; i++) {
            if (attrList[i] == 0) exitCode = 1;
        }
        if (exitCode != 0) {
            //all examples are of one class check
            for (int i = 0; i < datas.size(); i++) {
                if (Integer.parseInt(datas.get(i)[classID]) == 1) counter++;
            }

            if (counter == datas.size()) {// || (counter >= (0.85 * datas.size()))) {
            //    out.println("Make leaf");
                currNode.isLeaf = true;
                currNode.isNode = false;
                currNode.attrOrClass = "Dead (1)";
                currNode.key = 1;
            }
            if (counter == 0) {//|| (counter < (0.15 * datas.size()))) {
            //    out.println("Make leaf");
                currNode.isLeaf = true;
                currNode.isNode = false;
                currNode.attrOrClass = "Alive (0)";
                currNode.key = 0;
            }
            else {
                //in other way we should find new attribute to divide
            /*
            i - int | b - boolean | f - float
            0-i 1-b 2-i 3-b 4-i 5-b 6-i 7-f 8-i 9-b 10-b 11-i
             */
                if (!currNode.isLeaf) {
                float beforeDiv = 0;//getEntropy(datas, classID, 1);
                float[] arrOfEntr = new float[12];
                List<String[]> leftSide = new ArrayList<String[]>();
                List<String[]> rightSide = new ArrayList<String[]>();
                float[] bestDelim = new float[possibleAttributes];
                float countAvF = 0;
                for (int i = 0; i < possibleAttributes; i++) {
                    leftSide.clear();
                    rightSide.clear();
                    //========count entropy=======================================
                    if (i == 0 || i == 2 || i == 4 || i == 8 || i == 11 || i == 6 || i == 7) {
                        countAvF = getBestDel(datas, i);
                        bestDelim[i] = countAvF;
                        for (int j = 0; j < datas.size(); j++) {
                            if (Float.parseFloat(datas.get(j)[i]) < countAvF) {
                                leftSide.add(datas.get(j));
                            } else //>countAv
                                rightSide.add(datas.get(j));
                        }
                        beforeDiv = getEntropy(datas, classID, 1);
                       // out.println("Entropy get: "+beforeDiv);
                        arrOfEntr[i] = beforeDiv - (((float)leftSide.size()/(float)datas.size())*getEntropy(leftSide, classID, 1)+(rightSide.size()/datas.size())*getEntropy(rightSide,  classID, 1));

                     //   beforeDiv = getEntropy(datas, i, countAv);
                       // arrOfEntr[i] = beforeDiv - ((leftSide.size()/datas.size())*getEntropy(leftSide, i, countAv)+(rightSide.size()/datas.size())*getEntropy(rightSide,  i, countAv));
                    }
                    if (i == 1 || i == 3 || i == 5 || i == 9 || i == 10) {
                        for (int j = 0; j < datas.size(); j++) {
                            if (Integer.parseInt(datas.get(j)[i]) == 0) {
                                leftSide.add(datas.get(j));
                            } else //>countAv
                                rightSide.add(datas.get(j));
                        }
                        bestDelim[i] = 1;
                        beforeDiv = getEntropy(datas, classID, 1);
                        arrOfEntr[i] = beforeDiv - (((float)leftSide.size()/(float)datas.size())*getEntropy(leftSide, classID, 1)+((float)rightSide.size()/(float)datas.size())*getEntropy(rightSide,  classID, 1));

                    }
                    //============================================================
                }


                float max = -99999;
                int index = -1;
                //==get best attribute===========
                for (int i = 0; i < possibleAttributes; i++) {
                    if ((max < arrOfEntr[i]) && (attrList[i] != 1)) {
                        max = arrOfEntr[i];
                        index = i;
                    }
                }
                //===============================


                    if (index != -1) {
                    currNode.isNode = true;
                    currNode.isLeaf = false;
                    currNode.attrIndex = index;
                    attrList[index] = 1;
                    leftSide.clear();
                    rightSide.clear();


                    //======divide to two different parts====
                    if (index == 0 || index == 2 || index == 4 || index == 8 || index == 11 || index == 6 || index == 7) {

                            for (int j = 0; j < datas.size(); j++) {
                                if (Float.parseFloat(datas.get(j)[index]) < bestDelim[index]) {
                                    leftSide.add(datas.get(j));
                                } else //>countAv
                                    rightSide.add(datas.get(j));
                            }
                        }
                    if (index == 1 || index == 3 || index == 5 || index == 9 || index == 10) {
                            for (int j = 0; j < datas.size(); j++) {
                                if (Integer.parseInt(datas.get(j)[index]) < 1) {
                                    leftSide.add(datas.get(j));
                                } else //>countAv
                                    rightSide.add(datas.get(j));
                            }
                        }
                    //=======================================


                    DecimalFormat df = new DecimalFormat("#.##");
                    if (bestDelim[index] != 0) {
                        currNode.attrOrClass = attributes[index] + " < " + df.format(bestDelim[index]);
                        currNode.key = bestDelim[index];
                    } else {

                        currNode.attrOrClass = attributes[index] + " < " + df.format(bestDelim[index]);
                        currNode.key = (float)bestDelim[index];
                    }

                    if (leftSide.size() != 0 && !currNode.isLeaf && currNode.leftChild == null) {
                     //   out.println("Go to left child ["+currNode.attrOrClass+"]"+" ["+leftSide.size()+"]");

                        currNode.leftChild = generateTree(leftSide, attrList, attributes);

                    }
                    else {
                        if (!currNode.isLeaf) {
                            counter = 0;
                        for (int i = 0; i < datas.size(); i++) {
                            if (Integer.parseInt(rightSide.get(i)[classID]) == 1) counter++;
                        }

                        if ((counter == rightSide.size()) || (counter >= (0.5 * rightSide.size()))) {
                     //       out.println("Make leaf");
                            currNode.leftChild = new Node();
                            currNode.leftChild.isLeaf = true;
                            currNode.leftChild.isNode = false;
                            currNode.leftChild.attrOrClass = "Alive (0)";
                            currNode.leftChild.key = 0;
                        }
                        if ((counter == 0) || (counter < (0.5 * rightSide.size()))) {
                         //   out.println("Make leaf");
                            currNode.leftChild = new Node();
                            currNode.leftChild.isLeaf = true;
                            currNode.leftChild.isNode = false;
                            currNode.leftChild.attrOrClass = "Dead (1)";
                            currNode.leftChild.key = 1;
                        }
                    }
                    }

                    if (rightSide.size() != 0 && !currNode.isLeaf && currNode.rightChild == null) {
                       // out.println("Go to right child ["+currNode.attrOrClass+"]"+" ["+rightSide.size()+"]");
                        currNode.rightChild = generateTree(rightSide, attrList, attributes);

                    }
                    else {
                        if (!currNode.isLeaf) {
                            counter = 0;
                        for (int i = 0; i < datas.size(); i++) {
                            if (Integer.parseInt(leftSide.get(i)[classID]) == 1) counter++;
                        }

                        if ((counter == leftSide.size()) || (counter >= (0.5 * leftSide.size()))) {
                          //  out.println("Make leaf");
                            currNode.rightChild = new Node();
                            currNode.rightChild.isLeaf = true;
                            currNode.rightChild.isNode = false;
                            currNode.rightChild.attrOrClass = "Alive (0)";
                            currNode.rightChild.key = 0;
                        }
                        if ((counter == 0) || (counter < (0.5 * leftSide.size()))) {
                       //     out.println("Make leaf");
                            currNode.rightChild = new Node();
                            currNode.rightChild.isLeaf = true;
                            currNode.rightChild.isNode = false;
                            currNode.rightChild.attrOrClass = "Dead (1)";
                            currNode.rightChild.key = 1;
                        }
                        }
                    }
                    attrList[index] = 0;
                }
                }
            }
        } else {
            counter = 0;
            for (int i = 0; i < datas.size(); i++) {
                if (Integer.parseInt(datas.get(i)[classID]) == 1) counter++;
            }

            if ((counter == datas.size()) || (counter >= (0.5 * datas.size()))) {
             //   out.println("Make leaf");
                currNode.isLeaf = true;
                currNode.isNode = false;
                currNode.attrOrClass = "Dead (1)";
                currNode.key = 1;
            }
            if ((counter == 0) || counter < (0.5 * datas.size())) {
             //   out.println("Make leaf");
                currNode.isLeaf = true;
                currNode.isNode = false;
                currNode.attrOrClass = "Alive (0)";
                currNode.key = 0;
            }
        }
        return currNode;
    }


    private float getEntropy(List<String[]> datas, int atrIndex, float divide) {
        float s = 1;
        if (datas.size() != 0) {
            float entropy = 0;
            float p1 = 0;
            float p2 = 0;
            for (int i = 0; i < datas.size(); i++)
                if ((Float.parseFloat(datas.get(i)[atrIndex]) < divide)) p1++;
            p2 = datas.size() - p1;
            p1 = p1  / datas.size();
            p2 = p2 / datas.size();
            if (p1 != 0 && p2 != 0)
            s =(float)( -1*((p1 * Math.log(p1) / Math.log(2)) + (p2 * Math.log(p2) / Math.log(2))));
            if ((p1 == 0 && p2 != 0 ) || (p1 != 0 && p2 == 0)) s = 1;
            if (p1 == 0 && p2 == 0) s = 0;

        }
      //  System.out.println("Entropy: "+s);
        return s;
    }
}




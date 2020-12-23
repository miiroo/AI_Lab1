import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class RegressionTree {

//TO DO: add best dilimeter finder
        // (from min to max with step = (max-min)/count
        // check min entropy for each
//attributes types



        private static int classID = 8;
        public Tree decisionTree;
        public int leafCounter = 0;


        // 0 - not used 1 - used 2 - class attr
        private String[] atributes1;

        public int getDepth(Node ns, int answer, int curLevel) {
            if (curLevel < answer) curLevel = answer;

            if(ns.leftChild != null) {
                answer++;
                curLevel = getDepth(ns.leftChild, answer, curLevel);
            }
            else {
                // leafCounter++;
                answer++;
            }
            answer--;
            if(ns.rightChild != null) {
                answer++;
                curLevel = getDepth(ns.rightChild, answer, curLevel);
            }
            else {
                //  leafCounter++;
                answer++;
            }
            answer--;

            return curLevel;
        }

        public void printTree() throws IOException {
            decisionTree.root.printTree();
        }

        public  RegressionTree(List<String[]> trainingData, int[] attrList, String[] attributes ) {
            leafCounter = 0;
            decisionTree = new Tree();
            Node root = generateTree(trainingData, attrList, attributes );
            decisionTree.root = root;
            atributes1 = attributes;

            //leafCounter++;
        }

        public void testTree(List<String[]> datas) {
            int counter = 1;
            float accuracy = 0;
            boolean lessMore;
            for (int i=0; i< datas.size(); i++) {
                float rightAnswer =Float.parseFloat(datas.get(i)[classID]);
                float currAnswer;
                Node nd = decisionTree.root;
                while (nd.isNode) {
                    if (Float.parseFloat(datas.get(i)[nd.attrIndex]) < nd.key) nd = nd.leftChild;
                    else nd = nd.rightChild;
                }
                currAnswer = nd.key;
                if (nd.attrOrClass.contains("<")) lessMore = true;
                else lessMore = false;

                if (lessMore) {
                    if (rightAnswer > currAnswer+nd.d || rightAnswer < currAnswer-nd.d)
                    accuracy++;
                }
                else {
                    if (Math.abs(rightAnswer - currAnswer) < nd.d ) accuracy++;
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

                    curNum = Float.parseFloat(datas.get(i)[atribute]);
                    if (max < curNum) max = curNum;
                    if (min > curNum) min = curNum;
                average += curNum;
            }

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
                        left = ((float) leftSide.size() / (float) datas.size()) * getEntropy(leftSide, classID, countMean(leftSide), countD(leftSide, countMean(leftSide)));
                        right = ((float) rightSide.size() / (float) datas.size()) * getEntropy(rightSide, classID, countMean(rightSide), countD(rightSide, countMean(rightSide)));
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
                } else bestDelim = average;
            } else bestDelim = max;
            return bestDelim;
        }

        private  Node generateTree (List<String[]> datas, int[] attrList, String[] attributes) {


            short counter = 0;
            int exitCode = 0;
            int possibleAttributes = 8;

            Node currNode = new Node();
            //check possible attributes
            for (int i=0; i<possibleAttributes; i++) {
                if (attrList[i] == 0) exitCode = 1;
            }
            if (exitCode != 0) {
                //all examples are of one class check
                for (int i = 0; i < datas.size(); i++) {
                    if (Math.abs(Float.parseFloat(datas.get(i)[classID])-countMean(datas)) <= countD(datas, countMean(datas))) counter++;
                }

                if (counter == datas.size()) {// || (counter >= (0.85 * datas.size()))) {
                    //    out.println("Make leaf");
                    leafCounter++;
                    currNode.isLeaf = true;
                    currNode.isNode = false;
                    currNode.d = countD(datas, countMean(datas));
                    currNode.attrOrClass = countMean(datas)+"+-"+currNode.d;
                    currNode.key = countMean(datas);

                }
                if (counter == 0) {//|| (counter < (0.15 * datas.size()))) {
                    //    out.println("Make leaf");
                    leafCounter++;
                    currNode.isLeaf = true;
                    currNode.isNode = false;
                    currNode.d = countD(datas, countMean(datas));
                    currNode.attrOrClass = "< " + countMean(datas)+"+-"+currNode.d + " < ";
                    currNode.key = countMean(datas);

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
                                countAvF = getBestDel(datas, i);
                                bestDelim[i] = countAvF;
                                for (int j = 0; j < datas.size(); j++) {
                                    if (Float.parseFloat(datas.get(j)[i]) < countAvF) {
                                        leftSide.add(datas.get(j));
                                    } else //>countAv
                                        rightSide.add(datas.get(j));
                                }
                                beforeDiv = getEntropy(datas, classID, countMean(datas), countD(datas,countMean(datas)));
                                // out.println("Entropy get: "+beforeDiv);
                                arrOfEntr[i] = beforeDiv - (((float)leftSide.size()/(float)datas.size())*getEntropy(leftSide, classID, countMean(leftSide), countD(leftSide, countMean(leftSide)))+(rightSide.size()/datas.size())*getEntropy(rightSide,  classID, countMean(rightSide), countD(rightSide, countMean(rightSide))));

                                //   beforeDiv = getEntropy(datas, i, countAv);
                                // arrOfEntr[i] = beforeDiv - ((leftSide.size()/datas.size())*getEntropy(leftSide, i, countAv)+(rightSide.size()/datas.size())*getEntropy(rightSide,  i, countAv));
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

                                for (int j = 0; j < datas.size(); j++) {
                                    if (Float.parseFloat(datas.get(j)[index]) < bestDelim[index]) {
                                        leftSide.add(datas.get(j));
                                    } else //>countAv
                                        rightSide.add(datas.get(j));
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
                                        if (Math.abs(Float.parseFloat(rightSide.get(i)[classID])-countMean(rightSide)) <= countD(rightSide, countMean(rightSide))) counter++;
                                    }

                                    if ((counter == rightSide.size()) || (counter >= (0.5 * rightSide.size()))) {
                                        //       out.println("Make leaf");
                                        leafCounter++;
                                        currNode.leftChild = new Node();
                                        currNode.leftChild.isLeaf = true;
                                        currNode.leftChild.isNode = false;
                                        currNode.leftChild.d = countD(rightSide, countMean(rightSide));
                                        currNode.leftChild.attrOrClass =  countMean(rightSide)+"+-"+currNode.leftChild.d;
                                        currNode.leftChild.key = countMean(rightSide);
                                    }
                                    if ((counter == 0) || (counter < (0.5 * rightSide.size()))) {
                                        //   out.println("Make leaf");
                                        leafCounter++;
                                        currNode.leftChild = new Node();
                                        currNode.leftChild.isLeaf = true;
                                        currNode.leftChild.isNode = false;
                                        currNode.leftChild.d = countD(rightSide, countMean(rightSide));
                                        currNode.leftChild.attrOrClass = "< " + countMean(rightSide)+"+-"+currNode.leftChild.d + " <";
                                        currNode.leftChild.key = countMean(rightSide);
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
                                        if (Float.parseFloat(leftSide.get(i)[classID]) == 1) counter++;
                                    }

                                    if ((counter == leftSide.size()) || (counter >= (0.5 * leftSide.size()))) {
                                        //  out.println("Make leaf");
                                        leafCounter++;
                                        currNode.rightChild = new Node();
                                        currNode.rightChild.isLeaf = true;
                                        currNode.rightChild.isNode = false;
                                        currNode.rightChild.d = countD(leftSide, countMean(leftSide));
                                        currNode.rightChild.attrOrClass = countMean(leftSide)+"+-"+currNode.rightChild.d;
                                        currNode.rightChild.key =countMean(leftSide);
                                    }
                                    if ((counter == 0) || (counter < (0.5 * leftSide.size()))) {
                                        //     out.println("Make leaf");
                                        leafCounter++;
                                        currNode.rightChild = new Node();
                                        currNode.rightChild.isLeaf = true;
                                        currNode.rightChild.isNode = false;
                                        currNode.leftChild.d = countD(leftSide, countMean(leftSide));
                                        currNode.leftChild.attrOrClass = "< " + countMean(leftSide)+"+-"+currNode.leftChild.d + " <";
                                        currNode.leftChild.key = countMean(leftSide);
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
                    if (Math.abs(Float.parseFloat(datas.get(i)[classID])-countMean(datas)) <= countD(datas, countMean(datas))) counter++;
                }

                if ((counter == datas.size()) || (counter >= (0.5 * datas.size()))) {
                    //   out.println("Make leaf");
                    leafCounter++;
                    currNode.isLeaf = true;
                    currNode.isNode = false;
                    currNode.d = countD(datas, countMean(datas));
                    currNode.attrOrClass = countMean(datas)+"+-"+currNode.d;;
                    currNode.key =countMean(datas);
                }
                if ((counter == 0) || counter < (0.5 * datas.size())) {
                    //   out.println("Make leaf");
                    leafCounter++;
                    currNode.isLeaf = true;
                    currNode.isNode = false;
                    currNode.d = countD(datas, countMean(datas));
                    currNode.attrOrClass = "< " + countMean(datas)+"+-"+currNode.d + " <";
                    currNode.key = countMean(datas);
                }
            }
            return currNode;
        }

        private  float countMean(List<String[]> datas) {
            float result=0;
            for (int i=0; i<datas.size(); i++) result +=Float.parseFloat(datas.get(i)[classID]);
            return  (result/datas.size());
        }
        private  float countD(List<String[]> datas, float avg) {
            float result=0;
            for (int i = 0; i < datas.size(); i++) {
                result += (float)Math.pow(Float.parseFloat(datas.get(i)[classID]) - avg, 2);
            }
            result = result / datas.size();
            result = (float)Math.sqrt(result);
            return  result;
        }

        private float getEntropy(List<String[]> datas, int atrIndex, float divide, float d) {
            float s = 1;
            if (datas.size() != 0) {
                float entropy = 0;
                float p1 = 0;
                float p2 = 0;
                for (int i = 0; i < datas.size(); i++)
                    if ((Float.parseFloat(datas.get(i)[atrIndex]) < divide+d) && (Float.parseFloat(datas.get(i)[atrIndex]) > divide-d)) p1++;
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


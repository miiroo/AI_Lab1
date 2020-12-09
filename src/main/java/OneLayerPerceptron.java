import java.util.List;

public class OneLayerPerceptron{
        //12 attributes + 1 free
        //free W at 12 place
        private float[] w = new float[13];
        private  int epoch;
        boolean showEpochs = false;

        public OneLayerPerceptron(List<String[]> trainingData, int epoch, boolean showEpochs) {
            this.epoch = epoch;
            this.showEpochs = showEpochs;
            //Random weights from -1 to 1
            for (int i=0;i<13;i++)
                w[i] =(float)(1.0+Math.random()*(-1.0 - 1.0));

            trainModel(trainingData);


        }

        private void trainModel(List<String[]> trData){
            float z;
            //learning speed
            float alpha = (float)0.05;
            //output
            float p;
            //MSE
            float sum_error;
            //error on learning step
            float error;

            //epochs
            for (int k=0;k<epoch;k++) {
                sum_error = 0;
                for (int j = 0; j < trData.size(); j++) {
                    z = 0;

                    //get current perceptron output
                    for (int i = 0; i < 12; i++) {
                        if (i == 2 || i == 8 || i==0 || i==4 ||  i == 11)
                            z += Float.parseFloat(trData.get(j)[i])/1000 * w[i];
                        if (i == 6)
                            z += Float.parseFloat(trData.get(j)[i])/100000 * w[i];
                        if (i != 2 && i!= 8 && i != 6 && i != 0 && i != 4 && i!=11)
                            z += Float.parseFloat(trData.get(j)[i]) * w[i];
                    }

                    z += w[12];
                    p = countOuter(z);

                    error = (float)(Integer.parseInt(trData.get(j)[12]))-p;
                    sum_error += error*error;
                    ////////////////////////////////////////////////////////////////////
                    //recount perceptron's weights
                    for (int i = 0; i < 12; i++) {
                        if (i == 2 || i == 8 || i==0 || i==4 ||  i == 11)
                            w[i] = w[i] + alpha * ((float) Integer.parseInt(trData.get(j)[12]) - p) * p * ((float)1.0 - p) * (Float.parseFloat(trData.get(j)[i])/1000);

                        if (i == 6)
                            w[i] = w[i] + alpha * ((float) Integer.parseInt(trData.get(j)[12]) - p) * p * ((float)1.0 - p) * (Float.parseFloat(trData.get(j)[i])/100000);

                        if (i != 2 && i!= 8 && i != 6 && i != 0 && i != 4 && i!=11)
                            w[i] = w[i] + alpha * ((float) Integer.parseInt(trData.get(j)[12]) - p) * p * ((float)1.0 - p) * Float.parseFloat(trData.get(j)[i]);

                    }
                    w[12] = w[12] + alpha * ((float) Integer.parseInt(trData.get(j)[12]) - p) * p * ((float)1.0 - p) * (float)1.0;
                    ////////////////////////////////////////////////////////////////////
                }
                if (showEpochs) System.out.println("Epoch: "+k+" error = "+(sum_error/trData.size()));
            }
            System.out.println();
        }

        private float countOuter(float z) {

            return (float)(1.0 / (1.0 + Math.exp(-1*z)));
        }


        public void testModel(List<String[]> datas) {
            float accuracy=0;
            float p;
            float z;

            for (int j=0;j<datas.size();j++) {
                z = 0;
                for (int i = 0; i < 12; i++) {
                    if (i == 2 || i == 8 || i==0 || i==4 || i == 11)
                        z += Float.parseFloat(datas.get(j)[i])/1000 * w[i];
                    if (i == 6)
                        z += Float.parseFloat(datas.get(j)[i])/100000 * w[i];
                    if (i != 2 && i!= 8 && i != 6 && i != 0 && i != 4 && i!=11)
                        z += Float.parseFloat(datas.get(j)[i]) * w[i];
                }
                z += w[12];
                p = countOuter(z);
                if (p>=0.5) p = 1;
                else p = 0;

                if (p == Float.parseFloat(datas.get(j)[12])) accuracy++;
            }
            accuracy = accuracy / datas.size();

            System.out.println("Accuracy: "+Float.toString(accuracy));
        }

        public void showW(){
            System.out.println("w0="+w[12]);
            for (int i=0;i<12;i++) {
                System.out.println("w"+(i+1)+"="+w[i]);
            }
        }
}

import java.util.List;

public class LogRegression {
    //12 attributes + 1 free coef
    //free coef locates at 12 place
    private float[] w = new float[13];

    public LogRegression(List<String[]> trainingData) {

        //feel all coef
        for (int i=0;i<12;i++)
            w[i] = 0;
        float mean = 0;
     //   for (int i=0;i<trainingData.size();i++){
    //            mean+=Float.parseFloat(trainingData.get(i)[12]);
    //    }
   //     mean = mean/trainingData.size();
   //     w[12] = (float)Math.log(mean/(1-mean));
        w[12] = 0;
        trainModel(trainingData);


    }

    private void trainModel(List<String[]> trData){
        float z;
        float alpha = (float)0.2;
        float p;
        float sum_error;
        float error;
        //epoch
        for (int k=0;k<10000;k++) {
            sum_error = 0;
            for (int j = 0; j < trData.size(); j++) {
                z = 0;
                for (int i = 0; i < 12; i++) {
                    if (i == 2 || i == 8 || i==0 || i==4 ||  i == 11)
                        z += Float.parseFloat(trData.get(j)[i])/1000 * w[i];
                    if (i == 6)
                        z += Float.parseFloat(trData.get(j)[i])/100000 * w[i];
                    if (i != 2 && i!= 8 && i != 6 && i != 0 && i != 4 && i!=11)
                        z += Float.parseFloat(trData.get(j)[i]) * w[i];
                }
                z += w[12];
             //  System.out.println("z="+z);
               // System.out.println("exp="+Math.exp(-1*z));
                p = (float)(1.0 / (1.0 + Math.exp(-1*z)));
             //  System.out.println("p="+p);
                error = (float)(Integer.parseInt(trData.get(j)[12]))-p;
                System.out.println("Error square: "+(error*error));
                sum_error += error*error;
                for (int i = 0; i < 12; i++) {
                    if (i == 2 || i == 8 || i==0 || i==4 ||  i == 11)
                        w[i] = w[i] + alpha * ((float) Integer.parseInt(trData.get(j)[12]) - p) * p * ((float)1.0 - p) * (Float.parseFloat(trData.get(j)[i])/1000);

                    if (i == 6)
                        w[i] = w[i] + alpha * ((float) Integer.parseInt(trData.get(j)[12]) - p) * p * ((float)1.0 - p) * (Float.parseFloat(trData.get(j)[i])/100000);

                    if (i != 2 && i!= 8 && i != 6 && i != 0 && i != 4 && i!=11)
                        w[i] = w[i] + alpha * ((float) Integer.parseInt(trData.get(j)[12]) - p) * p * ((float)1.0 - p) * Float.parseFloat(trData.get(j)[i]);

                }
                w[12] = w[12] + alpha * ((float) Integer.parseInt(trData.get(j)[12]) - p) * p * ((float)1.0 - p) * (float)1.0;
            }
         //   System.out.println("Epoch: "+k+" error = "+sum_error);
        }
        System.out.println();
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
            p = (float)( 1 / (1 + Math.exp(-1*z)));
            if (p>=0.5) p = 1;
            else p = 0;

            if (p == Float.parseFloat(datas.get(j)[12])) accuracy++;
        }
        accuracy = accuracy / datas.size();

        System.out.println("Accuracy: "+Float.toString(accuracy));
    }

    public void showCoef(){
        System.out.println("b0="+w[12]);
        for (int i=0;i<12;i++) {
            System.out.println("b"+(i+1)+"="+w[i]);
        }
    }

}

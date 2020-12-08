import java.util.List;
import java.lang.Math;

public class LogRegression {
    //12 attributes + 1 free coef
    //free coef locates at 12 place
    private float[] w = new float[13];

    public LogRegression(List<String[]> trainingData) {

        //feel all coef
        for (int i=0;i<12;i++)
            w[i] = 0;
        float mean = 0;
        for (int i=0;i<trainingData.size();i++){
                mean+=Float.parseFloat(trainingData.get(i)[12]);
        }
        mean = mean/trainingData.size();
        w[12] = (float)Math.log(mean/(1-mean));
        trainModel(trainingData);


    }

    private void trainModel(List<String[]> trData){
        float z;
        float alpha = (float)0.3;
        float p;
        for (int k=0;k<10;k++) {
            for (int j = 0; j < trData.size(); j++) {
                z = 0;
                for (int i = 0; i < 12; i++) {
                    z += Float.parseFloat(trData.get(j)[i]) * w[i];
                }
                z += w[12];
                p = 1 / (1 + (float)Math.exp(-z));
                for (int i = 0; i < 12; i++) {
                    w[i] = w[i] + alpha * (Float.parseFloat(trData.get(j)[12]) - p) * p * (1 - p) * Float.parseFloat(trData.get(j)[i]);
                }
                w[12] = w[12] + alpha * (Float.parseFloat(trData.get(j)[12]) - p) * p * (1 - p) * 1;
            }
        }
    }

    public void testModel(List<String[]> datas) {
        float accuracy=0;
        float p;
        float z;

        for (int j=0;j<datas.size();j++) {
            z = 0;
            for (int i = 0; i < 12; i++) {
                z += Float.parseFloat(datas.get(j)[i]) * w[i];
            }
            z += w[12];
            p = 1 / (1 + (float)Math.exp(-z));
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

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
    //        //12 attributes + 1 free
    //        //free W at 12 place
    private float alpha = (float)0.3;
    private float epsilon = (float)0.3;
    private int sdvig = 0;

    private class Neuron {

        public  float delta = 1;
        public float[] w;
        public  float[] grad;
        public float lastOut;
        public  float[] deltaW;

        public float getLast() {return  lastOut;}

        //get amount of previous layer/inputs as a param
        //generate random weights for input data
        Neuron(int inputsC) {
            w = new float[inputsC];
            grad = new float[inputsC];
            deltaW = new float[inputsC];
            for (int i=0;i<inputsC;i++) {
                w[i] = (float) (1.0 + Math.random() * (-1.0 - 1.0));
                deltaW[i] = 0;
            }
        }


        protected void countOuter(float z) {
            lastOut = (float)(1.0 / (1.0 + Math.exp(-1*z)));
        }

    }

    private int epoch;
    private List<Neuron[]> network = new ArrayList<Neuron[]>();

    NeuralNetwork(int epoch, int sdvig) {
        this.epoch = epoch;
        this.sdvig = sdvig;
    }

    public void addLayer(int neuronCount) {
        network.add(new Neuron[neuronCount]);

        for (int i=0;i<neuronCount;i++) {
            //add inputs
            //if neurons locates one first layer they got (current layer-1 neuron count) inputs
            //if it's first layer they got 13 (for me) inputs
            if (network.size()-2 >= 0)
                network.get(network.size()-1)[i] = new Neuron(network.get(network.size()-2).length+sdvig);
            else network.get(network.size()-1)[i] = new Neuron(13);
        }
    }

    public void trainModel(List<String[]> datas) {
        //output
        float z;
        //add activation layer
        network.add(new Neuron[1]);
        network.get(network.size() - 1)[0] = new Neuron(network.get(network.size()-2).length+sdvig);


        //train for current amount of epoch
        for(int a = 0; a<epoch; a++) {

            for (int j = 0; j<datas.size(); j++) {
                //get results of each layer//////////////////////
                for (int layers = 0; layers < network.size(); layers++) {
                    if (layers == 0) {
                        for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                            z = 0;
                            for (int i = 0; i < 12; i++) {
                                if (i == 2 || i == 8 || i == 0 || i == 4 || i == 11)
                                    z += Float.parseFloat(datas.get(j)[i]) / 1000 * network.get(layers)[neurons].w[i];
                                if (i == 6)
                                    z += Float.parseFloat(datas.get(j)[i]) / 100000 * network.get(layers)[neurons].w[i];
                                if (i != 2 && i != 8 && i != 6 && i != 0 && i != 4 && i != 11)
                                    z += Float.parseFloat(datas.get(j)[i]) * network.get(layers)[neurons].w[i];
                            }

                            z += network.get(layers)[neurons].w[12];
                            network.get(layers)[neurons].countOuter(z);
                        }
                    } else {
                        for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                            z = 0;
                            for (int i = 0; i < network.get(layers)[neurons].w.length-sdvig; i++) {
                                z += network.get(layers - 1)[i].lastOut * network.get(layers)[neurons].w[i];
                            }
                            if (sdvig == 1)
                               z += network.get(layers)[neurons].w[network.get(layers)[neurons].w.length-1];
                            network.get(layers)[neurons].countOuter(z);
                        }
                    }
                }
                ////////end of: get result of each layer/////////////

                //get delta on last layer
                float outIdeal = Float.parseFloat(datas.get(j)[12]);
                float outActual = network.get(network.size() - 1)[0].lastOut;
                float f = (1-outActual)*outActual;
                network.get(network.size() - 1)[0].delta = (outIdeal - outActual) * f;

                //get delta for all other layers
                for (int layers = network.size()-2; layers >= 0; layers--) {
                    for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                        float summ = 0;
                        f = ((1-network.get(layers)[neurons].lastOut) * network.get(layers)[neurons].lastOut);
                        for (int weightFromNeuron = 0; weightFromNeuron < network.get(layers+1).length; weightFromNeuron++) {
                            summ = network.get(layers+1)[weightFromNeuron].w[neurons] * network.get(layers+1)[weightFromNeuron].delta;
                        }
                        network.get(layers)[neurons].delta = f * summ;
                    }
                }


                //update weights//////////////////////////////
                //Grad for each weight = neuron output * delta of next neuron.
                for (int layers = network.size()-1; layers >=0; layers--) {
                    for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                        if (layers != 0) {
                            for (int weight = 0; weight < network.get(layers)[neurons].w.length-sdvig; weight++) {
                                   float output = network.get(layers - 1)[weight].lastOut;
                                   float delta = network.get(layers)[neurons].delta;
                                   network.get(layers)[neurons].grad[weight] = output*delta;
                            }
                            if (sdvig == 1 ) {
                                float output = network.get(layers)[neurons].w[network.get(layers)[neurons].w.length-sdvig];
                                float delta = network.get(layers)[neurons].delta;
                                network.get(layers)[neurons].grad[network.get(layers)[neurons].w.length-sdvig] = output*delta;
                            }
                        }
                        else {
                            for (int i=0; i<12; i++) {
                                float output = 0;
                                if (i == 2 || i == 8 || i == 0 || i == 4 || i == 11)
                                    output = Float.parseFloat(datas.get(j)[i]) / 1000;
                                if (i == 6)
                                    output = Float.parseFloat(datas.get(j)[i]) / 100000;
                                if (i != 2 && i != 8 && i != 6 && i != 0 && i != 4 && i != 11)
                                    output = Float.parseFloat(datas.get(j)[i]);

                                float delta = network.get(layers)[neurons].delta;
                                network.get(layers)[neurons].grad[i] = output*delta;
                            }

                            float output = network.get(layers)[neurons].w[12];
                            float delta = network.get(layers)[neurons].delta;
                            network.get(layers)[neurons].grad[12] = output*delta;

                        }
                    }
                }

                //update
                for (int layers = 0; layers < network.size(); layers++) {
                    for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                        for (int weight = 0; weight < network.get(layers)[neurons].w.length; weight++) {
                            float deltaww =epsilon * network.get(layers)[neurons].grad[weight] +alpha * network.get(layers)[neurons].deltaW[weight];
                            network.get(layers)[neurons].deltaW[weight] = deltaww;
                            network.get(layers)[neurons].w[weight] += deltaww;
                        }
                    }
                }
                ////////end of update weights////////////////////////////

            }
        }
    }

    public  void testModel(List<String[]> datas) {
        float z;
        float accuracy=0;
        for (int j = 0; j<datas.size(); j++) {
            //get results of each layer//////////////////////
            for (int layers = 0; layers < network.size(); layers++) {
                if (layers == 0) {
                    for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                        z = 0;
                        for (int i = 0; i < 12; i++) {
                            if (i == 2 || i == 8 || i == 0 || i == 4 || i == 11)
                                z += Float.parseFloat(datas.get(j)[i]) / 1000 * network.get(layers)[neurons].w[i];
                            if (i == 6)
                                z += Float.parseFloat(datas.get(j)[i]) / 100000 * network.get(layers)[neurons].w[i];
                            if (i != 2 && i != 8 && i != 6 && i != 0 && i != 4 && i != 11)
                                z += Float.parseFloat(datas.get(j)[i]) * network.get(layers)[neurons].w[i];
                        }

                        z += network.get(layers)[neurons].w[12];
                        network.get(layers)[neurons].countOuter(z);
                    }
                } else {
                    for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                        z = 0;
                        for (int i = 0; i < network.get(layers)[neurons].w.length-sdvig; i++) {
                            z += network.get(layers - 1)[i].getLast() * network.get(layers)[neurons].w[i];
                        }
                        if (sdvig == 1)
                            z+= network.get(layers)[neurons].w[network.get(layers)[neurons].w.length-1];
                        network.get(layers)[neurons].countOuter(z);
                    }
                }
            }
            float result = network.get(network.size()-1)[0].lastOut;
            if (result < 0.5) result = 0;
            else result = 1;

            if (result == Float.parseFloat(datas.get(j)[12])) accuracy++;
        }
        accuracy = accuracy/datas.size();
        System.out.println("Accuracy = " + accuracy);
    }


    public void printWeights() {
        System.out.println("Pattern: W[layer][neuron][weight]");
        for (int layers=0;layers < network.size(); layers++) {
            for(int neurons=0; neurons < network.get(layers).length; neurons++) {
                for(int weights=0; weights < network.get(layers)[neurons].w.length; weights++) {
                    System.out.println("W["+layers+"]["+neurons+"]["+weights+"]="+network.get(layers)[neurons].w[weights]);
                }
                System.out.println();
            }
            System.out.println();
        }
    }


}

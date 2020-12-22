import java.util.ArrayList;
import java.util.List;

public class NeuralNetworkL {

        //        //12 attributes + 1 free
        //        //free W at 12 place
        private float alpha = (float)0.3;
        private float epsilon = (float)0.3;
        private int sdvig;

        private class Neuron {

            public  float delta = 1;
            public float[] w;
            public  float[] grad;
            public float lastOut;
            public  float[] deltaW;
            private boolean act;

            public float getLast() {return  lastOut;}

            //get amount of previous layer/inputs as a param
            //generate random weights for input data
            Neuron(int inputsC, boolean act) {
                w = new float[inputsC];
                grad = new float[inputsC];
                deltaW = new float[inputsC];
                for (int i=0;i<inputsC;i++) {
                    w[i] = (float) (1.0 + Math.random() * (-1.0 - 1.0));
                    deltaW[i] = 0;
                }
                this.act = act;
            }


            protected void countOuter(float z) {
             //   lastOut = (float)(1.0 / (1.0 + Math.exp(-1*z)));
                if (act)
                    lastOut = z;
                else
                    lastOut = (float) ((2.0 / (1.0 + Math.exp(-2*z)))-1);
            }

        }

        private int epoch;
        private List<Neuron[]> network = new ArrayList<Neuron[]>();

        NeuralNetworkL(int epoch, int sdvig) {
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
                    network.get(network.size()-1)[i] = new Neuron(network.get(network.size()-2).length+sdvig, false);
                else network.get(network.size()-1)[i] = new Neuron(8, false);
            }
        }

        public void trainModel(List<String[]> datas) {
            //output
            float z;
            //add activation layer
            network.add(new Neuron[1]);
            network.get(network.size() - 1)[0] = new Neuron(network.get(network.size()-2).length+sdvig, true);


            //train for current amount of epoch
            for(int a = 0; a<epoch; a++) {

                for (int j = 0; j<datas.size(); j++) {
                    //get results of each layer//////////////////////
                    for (int layers = 0; layers < network.size(); layers++) {
                        if (layers == 0) {
                            for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                                z = 0;
                                for (int i = 0; i < 7; i++) {
                                        z += Float.parseFloat(datas.get(j)[i])*10 * network.get(layers)[neurons].w[i];
                                }

                                z += network.get(layers)[neurons].w[7];
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
                    float outIdeal = Float.parseFloat(datas.get(j)[8])*10;
                    float outActual = network.get(network.size() - 1)[0].lastOut;
                    float f = (1-outActual*outActual);
                    network.get(network.size() - 1)[0].delta = (outIdeal - outActual) * f;

                    //get delta for all other layers
                    for (int layers = network.size()-2; layers >= 0; layers--) {
                        for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                            float summ = 0;
                            f = 1-(network.get(layers)[neurons].lastOut * network.get(layers)[neurons].lastOut);
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
                                for (int i=0; i<7; i++) {
                                    float output = 0;
                                    output = Float.parseFloat(datas.get(j)[i])*10;
                                    float delta = network.get(layers)[neurons].delta;
                                    network.get(layers)[neurons].grad[i] = output*delta;
                                }

                                float output = network.get(layers)[neurons].w[7];
                                float delta = network.get(layers)[neurons].delta;
                                network.get(layers)[neurons].grad[7] = output*delta;

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
            float d = countD(datas, countMean(datas));
            for (int j = 0; j<datas.size(); j++) {
                //get results of each layer//////////////////////
                for (int layers = 0; layers < network.size(); layers++) {
                    if (layers == 0) {
                        for (int neurons = 0; neurons < network.get(layers).length; neurons++) {
                            z = 0;
                            for (int i = 0; i < 7; i++) {
                                    z += Float.parseFloat(datas.get(j)[i])*10 * network.get(layers)[neurons].w[i];
                            }

                            z += network.get(layers)[neurons].w[7];
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
                //if (result < 0.5) result = 0;
                //else result = 1;
                //System.out.println("Result:" +result +" Actual:"+(Float.parseFloat(datas.get(j)[8]))*10);
                if (Math.pow((result - Float.parseFloat(datas.get(j)[8])*10),2) <= d) accuracy++;
            }
            accuracy = accuracy/datas.size();
            System.out.println("Accuracy = " + accuracy);
        }

        private float countMean(List<String[]> dt) {
            float mean=0;
            for (int i=0; i< dt.size(); i++) {
                mean += Float.parseFloat(dt.get(i)[8]);
            }
            return  mean/dt.size();
        }
        private float countD(List<String[]> dt, float mean) {
            float result=0;
            for (int i = 0; i < dt.size(); i++) {
                result += (float)Math.pow(Float.parseFloat(dt.get(i)[8]) - mean, 2);
            }
            result = result / dt.size();
            result = (float)Math.sqrt(result);
            return  result;
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

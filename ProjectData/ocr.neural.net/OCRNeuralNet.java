package ocr.neural.net;

public class OCRNeuralNet {
    
    public int inputNodes, hiddenNodes, outputNodes;
    public double learningRate;
    public double[][] wih,who;
    
    public OCRNeuralNet(int inputNodes, int hiddenNodes, int outputNodes, double learningRate, double[][] wih, double[][] who){
        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;
        this.learningRate = learningRate;
        this.wih = wih;
        this.who = who;
        
        if(this.wih == null){
            this.wih = generateRandWeights(Math.pow(this.hiddenNodes, -0.5), this.hiddenNodes, this.inputNodes);
        }
        
        if(this.who == null){
            this.who = generateRandWeights(Math.pow(this.outputNodes, -0.5),this.outputNodes,this.hiddenNodes);
        }
    }
    
    public double[][] query(double[] inputList){
        double[][] inputs = MatrixMult.transpose(inputList);
        double[][] hiddenInputs = MatrixMult.mult(wih, inputs);
        double[][] hiddenOutputs = activationFunc(hiddenInputs);
        double[][] finalInputs = MatrixMult.mult(who, hiddenOutputs);
        return activationFunc(finalInputs);
    }
    
    public double[][] reverse(double[][] targetList){
        double[][] newActivation = reverseActivationFunc(MatrixMult.transpose(targetList));
        double[][] hiddenOutputs = MatrixMult.mult(MatrixMult.transpose(who),newActivation);
        hiddenOutputs = rescale(hiddenOutputs,0.98,0.01);
        hiddenOutputs = reverseActivationFunc(hiddenOutputs);
        double[][] inputs = MatrixMult.mult(MatrixMult.transpose(wih),hiddenOutputs);
        inputs = rescale(inputs,255,1);
        return inputs;
    }
    
    public double[][] rescale(double[][] input2,double maxVal, double minVal){
        double[][] input = input2;
        double min = getMaxMin(input,false);
        //System.out.println(max+", "+min);
        for(int i=0;i<input.length;i++){
            for(int j=0;j<input[i].length;j++){
                input[i][j]-=min;
            }
        }
        double max = getMaxMin(input,true);
        for(int i=0;i<input.length;i++){
            for(int j=0;j<input[i].length;j++){
                input[i][j]/=max;
                input[i][j]*=maxVal;
                input[i][j]+=minVal;
            }
        }
        return input;
    }
    
    public double getMaxMin(double[][] input, boolean maxMin){
        double value;
        if(maxMin){
            value=-100000;
        }else{
            value=1000000;
        }
        for(int i=0;i<input.length;i++){
            for(int j=0;j<input[i].length;j++){
                if(maxMin){
                    if(input[i][j]>value){
                        value=input[i][j];
                    }
                }
                if(!maxMin){
                    if(input[i][j]<value){
                        value=input[i][j];
                    }
                }
            }
        }
        return value;
    }
    
    public double[][] reverseActivationFunc(double[][] targetList){
        for(int j=0;j<targetList.length;j++){
            for(int i=0;i<targetList[j].length;i++){
                targetList[j][i] = -Math.log((targetList[j][i])/(1-targetList[j][i]));
            }
        }
        return targetList;
    }
    
    public void train(double[] inputList, double[] targetList){
        double[][] inputs = MatrixMult.transpose(inputList);
        double[][] hiddenInputs = MatrixMult.mult(wih, inputs);
        double[][] hiddenOutputs = activationFunc(hiddenInputs);
        double[][] finalInputs = MatrixMult.mult(who, hiddenOutputs);
        double[][] finalOutput = activationFunc(finalInputs);
        
        double[][] targetVals = MatrixMult.transpose(targetList);
        double[][] outputErrors = new double[finalOutput.length][1];
        for(int i=0;i<outputErrors.length;i++){
            outputErrors[i][0] = targetVals[i][0] - finalOutput[i][0];
        }
        double[][] hiddenErrors = MatrixMult.mult(MatrixMult.transpose(who), outputErrors);
        
        double[][] updateWIH = updateWeightLinks(hiddenErrors, hiddenOutputs, inputs);
        for(int i=0;i<updateWIH.length;i++){
            for(int j=0;j<updateWIH[i].length;j++){
                wih[i][j]+=updateWIH[i][j];
            }
        }
        
        double[][] updateWHO = updateWeightLinks(outputErrors, finalOutput, hiddenOutputs);
        for(int i=0;i<updateWHO.length;i++){
            for(int j=0;j<updateWHO[i].length;j++){
                who[i][j]+=updateWHO[i][j];
            }
        }
    }
    
    public double[][] updateWeightLinks(double[][] finalError, double[][] output, double[][] inputs){
        double[][] elementMult = new double[finalError.length][1];
        for(int i=0;i<finalError.length;i++){
            for(int j=0;j<finalError[i].length;j++){
                elementMult[i][j] = finalError[i][j]*output[i][j]*(1-output[i][j]);
            }
        }
        double[][] fullMatrix = MatrixMult.mult(elementMult, MatrixMult.transpose(inputs));
        for(int i=0;i<fullMatrix.length;i++){
            for(int j=0;j<fullMatrix[i].length;j++){
                fullMatrix[i][j] *=learningRate;
            }
        }
        return fullMatrix;
    }
    
    public double[][] activationFunc(double[][] inputs){
        for(int i=0;i<inputs.length;i++){
            for(int j=0;j<inputs[i].length;j++){
                inputs[i][j] = 1/(1+Math.exp(-inputs[i][j]));
            }
        }
        return inputs;
    }
    
    public double[][] generateRandWeights(double stdDev, int rows, int cols){
        java.util.Random rand = new java.util.Random();
        double[][] weightSet = new double[rows][cols];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                weightSet[i][j] = rand.nextGaussian()*stdDev;
            }
        }
        return weightSet;
    }
}
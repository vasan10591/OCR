package ocr.neural.net;

public class MatrixMult {

    public static double[][] mult(double a[][], double b[][]){
    double j[][]=new double [a.length][b[0].length];
        for (int i=0;i<j.length;i++){
            for(int k=0;k<j[i].length;k++){
                j[i][k]=getVal(a[i],swap(b,k));
            }
        }
    return j;
    }

    private static double getVal(double a[], double b[]){
        double k=0;

        for(int i=0;i<a.length;i++){
            k=k+(a[i]*b[i]);
        }
        return k;
    }
    
    private static double[] swap(double b[][], int k){
        double j[]=new double [b.length];

        for(int i=0;i<j.length;i++){
            j[i]=b[i][k];
        }

        return j;
    }
    
    public static double[][] transpose(double[][] inputList){
        double[][] tempList = new double[inputList[0].length][inputList.length];
        for(int i=0;i<inputList[0].length;i++){
            for(int j=0;j<inputList.length;j++){
                tempList[i][j] = inputList[j][i];
            }
        }
        return tempList;
    }
    
    public static double[][] transpose(double[] inputList){
        double[][] newArr = new double[inputList.length][1];
        for(int i=0;i<newArr.length;i++){
            newArr[i][0] = inputList[i];
        }
        return newArr;
    }
    
}
package cn.edu.xjtlu.wigeoscanner.Modules;

public class MathMethod {
    //added
    /*
     * 求(h,v)坐标的位置的余子式
     */
    public float[][] getConfactor(float[][] data, int h, int v) {
        int H = data.length;
        int V = data[0].length;
        float[][] newdata = new float[H-1][V-1];
        for(int i=0; i<newdata.length; i++) {
            if(i < h-1) {
                for(int j=0; j<newdata[i].length; j++) {
                    if(j < v-1) {
                        newdata[i][j] = data[i][j];
                    }else {
                        newdata[i][j] = data[i][j+1];
                    }
                }
            }else {
                for(int j=0; j<newdata[i].length; j++) {
                    if(j < v-1) {
                        newdata[i][j] = data[i+1][j];
                    }else {
                        newdata[i][j] = data[i+1][j+1];
                    }
                }
            }
        }

//      for(int i=0; i<newdata.length; i ++)
//          for(int j=0; j<newdata[i].length; j++) {
//              System.out.println(newdata[i][j]);
//          }
        return newdata;
    }
    /*
     * 计算行列式的值
     */
    public float getMartrixResult(float[][] data) {
        /*
         * 二维矩阵计算
         */
        if(data.length == 2) {
            return data[0][0]*data[1][1] - data[0][1]*data[1][0];
        }
        /*
         * 二维以上的矩阵计算
         */
        float result = 0;
        int num = data.length;
        float[] nums = new float[num];
        for(int i=0; i<data.length; i++) {
            if(i%2 == 0) {
                nums[i] = data[0][i] * getMartrixResult(getConfactor(data, 1, i+1));
            }else {
                nums[i] = -data[0][i] * getMartrixResult(getConfactor(data, 1, i+1));
            }
        }
        for(int i=0; i<data.length; i++) {
            result += nums[i];
        }

//      System.out.println(result);
        return result;
    }

    public float[][] getReverseMartrix(float[][] data) {
        float[][] newdata = new float[data.length][data[0].length];
        float A = getMartrixResult(data);
//      System.out.println(A);
        for(int i=0; i<data.length; i++) {
            for(int j=0; j<data[0].length; j++) {
                if((i+j)%2 == 0) {
                    newdata[i][j] = getMartrixResult(getConfactor(data, i+1, j+1)) / A;
                }else {
                    newdata[i][j] = -getMartrixResult(getConfactor(data, i+1, j+1)) / A;
                }

            }
        }
        newdata = trans(newdata);

        for(int i=0;i<newdata.length; i++) {
            for(int j=0; j<newdata[0].length; j++) {
                System.out.print(newdata[i][j]+ "   ");
            }
            System.out.println();
        }
        return newdata;
    }

    private float[][] trans(float[][] newdata) {
        // TODO Auto-generated method stub
        float[][] newdata2 = new float[newdata[0].length][newdata.length];
        for(int i=0; i<newdata.length; i++)
            for(int j=0; j<newdata[0].length; j++) {
                newdata2[j][i] = newdata[i][j];
            }
        return newdata2;
    }

    public float[][] multip3x(float[][] X, float[][] Y){
        float[][] result = new float[3][Y[0].length];
        for(int row = 0;row < 3;row++){
            for(int col = 0;col < Y[0].length;col++){
                float num = 0;
                for(int i = 0;i <3;i++){
                    num+=X[row][i]*Y[i][col];
                }
                result[row][col]=num;
            }
        }
        return result;
    }
    //end

}

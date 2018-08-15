package cn.edu.xjtlu.wigeoscanner.Modules;

public class discard {

    /*int rotation = getWindowManager().getDefaultDisplay().getRotation();
                if (rotation == 1) {
                    SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, mRotationMatrix);
                } else {
                    SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, mRotationMatrix);
                }*/

    /*SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

     */
                /*azimuth = (float)(((mOrientationAngles[0]*180)/Math.PI)+180);
                pitch = (float)(((mOrientationAngles[1]*180/Math.PI))+90);
                roll = (float)(((mOrientationAngles[2]*180/Math.PI)));*/
    /*Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_GRAVITY) {
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
    } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

        magnetic[0] = event.values[0];
        magnetic[1] = event.values[1];
        magnetic[2] = event.values[2];

        float[] R = new float[9];
        float[] I = new float[9];
        SensorManager.getRotationMatrix(R, I, gravity, magnetic);
        float [] A_D = event.values.clone();
        float [] A_W = new float[3];
        A_W[0] = R[0] * A_D[0] + R[1] * A_D[1] + R[2] * A_D[2];
        A_W[1] = R[3] * A_D[0] + R[4] * A_D[1] + R[5] * A_D[2];
        A_W[2] = R[6] * A_D[0] + R[7] * A_D[1] + R[8] * A_D[2];

        String resultX = String.format("%8.3f",A_W[0]);
        String resultY = String.format("%8.3f",A_W[1]);
        String resultZ = String.format("%8.3f",A_W[2]);
        tv_geoX.setText("GeoX:"+ resultX);
        tv_geoY.setText("GeoY:"+ resultY);
        tv_geoZ.setText("GeoZ:"+ resultZ);
    else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        System.arraycopy(event.values, 0, mAccelerometerReading,
                0, mAccelerometerReading.length);
    }
    }
    */
            /*String resultX = String.format("%8.2f:%8.2f:%8.2f",mReverseMatrix[0][0],mReverseMatrix[1][0],mReverseMatrix[2][0]);
            String resultY = String.format("%8.2f:%8.2f:%8.2f",mReverseMatrix[0][1],mReverseMatrix[1][1],mReverseMatrix[2][1]);
            String resultZ = String.format("%8.2f:%8.2f:%8.2f",mReverseMatrix[0][2],mReverseMatrix[1][2],mReverseMatrix[2][2]);*/
            /*String resultX = String.format("%8.3f:%8.3f",mRotationMatrix[0],mRotationMatrix[3]);
            String resultY = String.format("%8.3f:%8.3f",mRotationMatrix[1],mRotationMatrix[4]);
            String resultZ = String.format("%8.3f:%8.3f",mRotationMatrix[2],mRotationMatrix[5]);*/

            /*String resultX = String.format("%8.3f",azimuth);
            String resultY = String.format("%8.3f",pitch);
            String resultZ = String.format("%8.3f",roll);*/


    //private final float[] mRotationMatrix = new float[9];
    //private final float[] mOrientationAngles = new float[3];

    //tv_geoX.setText("GeoX:"+geoitem.getStore_geo_x());
    //tv_geoY.setText("GeoY:"+geoitem.getStore_geo_y());
    //tv_geoZ.setText("GeoZ:"+geoitem.getStore_geo_z());
//输出方向角



    //输出的某位置的地磁特征量 精度有问题
    //double total = Math.sqrt(MagRot[0][0]*MagRot[0][0]+MagRot[1][0]*MagRot[1][0]+MagRot[2][0]*MagRot[2][0]);
    //double total = Math.sqrt(MagRot[0]*MagRot[0]+MagRot[1]*MagRot[1]+MagRot[2]*MagRot[2]);
            /*String resultX = String.format("%8.4f",MagRot[0][0]);
            String resultY = String.format("%8.4f",MagRot[1][0]);
            String resultZ = String.format("%8.4f",MagRot[2][0]);*/

    //获取raw地磁数据
            /*tv_geoX.setText("GeoX:"+mMagnetometerReading[0]);
            tv_geoY.setText("GeoY:"+mMagnetometerReading[1]);
            tv_geoZ.setText("GeoZ:"+mMagnetometerReading[2]);*/
    //获取raw加速度计数据 弃用 用Grav
            /*tv_geoX.setText("GeoX:"+ mAccelerometerReading[0]);
            tv_geoY.setText("GeoY:"+ mAccelerometerReading[1]);
            tv_geoZ.setText("GeoZ:"+ mAccelerometerReading[2]);*/
    //获取raw Gravity计数据
            /*tv_geoX.setText("GeoX:"+ mGravmeterReading[0]);
            tv_geoY.setText("GeoY:"+ mGravmeterReading[1]);
            tv_geoZ.setText("GeoZ:"+ mGravmeterReading[2]);*/
}

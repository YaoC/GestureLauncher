package cn.edu.pku.yaochg.imagesimilarity;

/**
 * @author chengyao
 *         date 2017/6/4
 *         mail chengyao09@hotmail.com
 **/
public class DssimInterface {
    static {
        //jniutil这个参数，可根据需要任意修改
        System.loadLibrary("dssim");
    }

    //java调C/C++中的方法都需要用native声明且方法名必须和C/C++的方法名一样
    public native double similarity(String img1, String img2);
}

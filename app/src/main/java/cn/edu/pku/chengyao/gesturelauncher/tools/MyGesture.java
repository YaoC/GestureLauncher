package cn.edu.pku.chengyao.gesturelauncher.tools;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chengyao
 *         date 2017/5/10
 *         mail chengyao09@hotmail.com
 **/
public class MyGesture implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> gesture;
    private String packageName;
    private int position;

    public MyGesture() {
        gesture = null;
        packageName = "";
        position = 0;
    }

    MyGesture(float[] img, int position, String packageName) {
        gesture = new LinkedList<>();
        for (int i = 0; i < img.length; i++) {
            if (img[i] == 0) {
                gesture.add(i);
            }
        }
        this.position = position;
        this.packageName = packageName;
    }

    public byte[] getGesture() {
        byte[] img = new byte[10000];
        for (Integer i : gesture) {
            img[i] = 1;
        }
        return img;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getPosition() {
        return position;
    }
}

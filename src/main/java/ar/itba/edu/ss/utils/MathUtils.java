package ar.itba.edu.ss.utils;

public class MathUtils {

    public static double[] delta(double[] a, double[] b) {
        if (a.length != b.length) {
            return new double[0];
        }
        double[] resp = new double[2];
        resp[0] = b[0] - a[0];
        resp[1] = b[1] - a[1];
        return resp;
    }

    public static double delta(double[] a) {
        return a[1] - a[0];
    }

    public static double dot(double[] a, double[] b) {
        if (a.length != b.length) {
            return -1;
        }
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }
}

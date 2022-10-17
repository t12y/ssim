package io.github.t12y.ssim;

import io.github.t12y.ssim.models.Matrix;

public class Math {
    public static int[] floor(double[] xn) {
        int[] out = new int[xn.length];

        for (int x = 0; x < xn.length; x++) {
            out[x] = (int)java.lang.Math.floor(xn[x]);
        }

        return out;
    }

    public static double sum2d(Matrix m) {
        double out = 0;

        for (int x = 0; x < m.data.length; x++) {
            out += m.data[x];
        }

        return out;
    }

    private static Matrix add2dMx(Matrix ref1, Matrix ref2) {
        Matrix result = new Matrix(ref1);

        for (int x = 0; x < ref1.height; x++) {
            int offset = x * ref1.width;

            for (int y = 0; y < ref1.width; y++) {
                result.data[offset + y] = ref1.data[offset + y] + ref2.data[offset + y];
            }
        }

        return result;
    }

    private static Matrix subtract2dMx(Matrix ref1, Matrix ref2) {
        Matrix result = new Matrix(ref1);

        for (int x = 0; x < ref1.height; x++) {
            int offset = x * ref1.width;

            for (int y = 0; y < ref1.width; y++) {
                result.data[offset + y] = ref1.data[offset + y] - ref2.data[offset + y];
            }
        }

        return result;
    }

    private static Matrix add2dScalar(Matrix ref, double increase) {
        Matrix result = new Matrix(ref);

        for (int x = 0; x < ref.data.length; x++) {
            result.data[x] = ref.data[x] + increase;
        }

        return result;
    }

    public static Matrix add2d(Matrix A, Matrix increase) {
        return add2dMx(A, increase);
    }

    public static Matrix add2d(Matrix A, double increase) {
        return add2dScalar(A, increase);
    }

    public static Matrix subtract2d(Matrix A, Matrix decrease) {
        return subtract2dMx(A, decrease);
    }

    private static Matrix divide2dScalar(Matrix ref, double divisor) {
        Matrix result = new Matrix(ref);

        for (int x = 0; x < ref.data.length; x++) {
            result.data[x] = ref.data[x] / divisor;
        }

        return result;
    }

    private static Matrix divide2dMx(Matrix ref1, Matrix ref2) {
        Matrix result = new Matrix(ref1);

        for (int x = 0; x < ref1.data.length; x++) {
            result.data[x] = ref1.data[x] / ref2.data[x];
        }

        return result;
    }

    public static Matrix divide2d(Matrix A, Matrix divisor) {
        return divide2dMx(A, divisor);
    }

    public static Matrix divide2d(Matrix A, double divisor) {
        return divide2dScalar(A, divisor);
    }

    private static Matrix multiply2dScalar(Matrix ref, double multiplier) {
        Matrix result = new Matrix(ref);

        for (int x = 0; x < ref.data.length; x++) {
            result.data[x] = ref.data[x] * multiplier;
        }

        return result;
    }

    private static Matrix multiply2dMx(Matrix ref1, Matrix ref2) {
        Matrix result = new Matrix(ref1);

        for (int x = 0; x < ref1.data.length; x++) {
            result.data[x] = ref1.data[x] * ref2.data[x];
        }

        return result;
    }

    public static Matrix multiply2d(Matrix A, Matrix multiplier) {
        return multiply2dMx(A, multiplier);
    }

    public static Matrix multiply2d(Matrix A, double multiplier) {
        return multiply2dScalar(A, multiplier);
    }

    public static Matrix square2d(Matrix A) {
        return multiply2d(A, A);
    }

    public static double mean2d(Matrix A) {
        return (sum2d(A)) / A.data.length;
    }
}

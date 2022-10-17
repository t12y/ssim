package io.github.t12y.ssim;

import io.github.t12y.ssim.models.Matrix;
import io.github.t12y.ssim.models.Options;
import io.github.t12y.ssim.models.Shape;

import static io.github.t12y.ssim.matlab.Conv2.conv2;
import static io.github.t12y.ssim.matlab.NormPDF.normpdf;
import static io.github.t12y.ssim.matlab.Transpose.transpose;

public class FastSSIM {
    protected static Matrix fastSSIM(Matrix pixels1, Matrix pixels2, Options options) {
        Matrix w = normpdf(getRange(options.windowSize), 0, 1.5);
        double L = java.lang.Math.pow(2, options.bitDepth) - 1;
        double c1 = java.lang.Math.pow(options.k1 * L, 2);
        double c2 = java.lang.Math.pow(options.k2 * L,  2);

        w = Math.divide2d(w, Math.sum2d(w));
        Matrix wt = transpose(w);
        Matrix m1 = conv2(pixels1, w, wt, Shape.VALID);
        Matrix m2 = conv2(pixels2, w, wt, Shape.VALID);
        Matrix m1Sq = Math.square2d(m1);
        Matrix m2Sq = Math.square2d(m2);
        Matrix m12 = Math.multiply2d(m1, m2);
        Matrix pixels1Sq = Math.square2d(pixels1);
        Matrix pixels2Sq = Math.square2d(pixels2);
        Matrix s1Sq = Math.subtract2d(conv2(pixels1Sq, w, wt, Shape.VALID), m1Sq);
        Matrix s2Sq = Math.subtract2d(conv2(pixels2Sq, w, wt, Shape.VALID), m2Sq);
        Matrix s12 = Math.subtract2d(conv2(Math.multiply2d(pixels1, pixels2), w, wt, Shape.VALID), m12);

        if (c1 > 0 && c2 > 0) {
            return genSSIM(m12, s12, m1Sq, m2Sq, s1Sq, s2Sq, c1, c2);
        }

        return genUQI(m12, s12, m1Sq, m2Sq, s1Sq, s2Sq);
    }

    private static Matrix getRange(int size) {
        int offset = (int)java.lang.Math.floor((double)size / 2);
        double[] data = new double[offset * 2 + 1];

        for (int x = -offset; x <= offset; x++) {
            data[x + offset] = java.lang.Math.abs(x);
        }

        return new Matrix(1, data.length, data);
    }

    private static Matrix genSSIM(Matrix m12, Matrix s12, Matrix m1Sq, Matrix m2Sq, Matrix s1Sq, Matrix s2Sq, double c1, double c2) {
        Matrix num1 = Math.add2d(Math.multiply2d(m12, 2), c1);
        Matrix num2 = Math.add2d(Math.multiply2d(s12, 2), c2);
        Matrix denom1 = Math.add2d(Math.add2d(m1Sq, m2Sq), c1);
        Matrix denom2 = Math.add2d(Math.add2d(s1Sq, s2Sq), c2);

        return Math.divide2d(Math.multiply2d(num1, num2), Math.multiply2d(denom1, denom2));
    }

    private static Matrix genUQI(Matrix m12, Matrix s12, Matrix m1Sq, Matrix m2Sq, Matrix s1Sq, Matrix s2Sq) {
        Matrix numerator1 = Math.multiply2d(m12, 2);
        Matrix numerator2 = Math.multiply2d(s12, 2);
        Matrix denominator1 = Math.add2d(m1Sq, m2Sq);
        Matrix denominator2 = Math.add2d(s1Sq, s2Sq);

        return Math.divide2d(Math.multiply2d(numerator1, numerator2), Math.multiply2d(denominator1, denominator2));
    }
}

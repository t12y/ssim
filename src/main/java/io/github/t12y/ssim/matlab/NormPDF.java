package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;

public class NormPDF {
    public static Matrix normpdf(Matrix ref, int m, double s) {
        double SQ2PI = 2.506628274631000502415765284811;
        Matrix result = new Matrix(ref);

        for (int i = 0; i < ref.data.length; i++) {
            double z = (ref.data[i] - m) / s;

            result.data[i] = Math.exp(-(Math.pow(z, 2)) / 2) / (s * SQ2PI);
        }

        return result;
    }
}

package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;

public class RBG2Gray {
    public static Matrix rgb2gray(Matrix ref) {
        Matrix result = new Matrix(ref);

        for (int i = 0; i < ref.data.length; i += 4) {
            int grayIndex = i / 4;
            result.data[grayIndex] = (int)(0.29894 * ref.data[i] + 0.58704 * ref.data[i + 1] + 0.11402 * ref.data[i + 2] + 0.5);
        }

        return result;
    }

    public static Matrix rgb2grayInteger(Matrix ref) {
        Matrix result = new Matrix(ref);

        for (int i = 0; i < ref.data.length; i += 4) {
            int grayIndex = i / 4;
            result.data[grayIndex] = (int)(77 * ref.data[i] + 150 * ref.data[i + 1] + 29 * ref.data[i + 2] + 128) >> 8;
        }

        return result;
    }
}

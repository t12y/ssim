package io.github.t12y.ssim.matlab.internal;

import io.github.t12y.ssim.models.Matrix;

import java.util.Arrays;

public class Numbers {
    public static Matrix numbers(int height, int width, int num){
        Matrix result = new Matrix(height, width, width * height);
        Arrays.fill(result.data, num);
        return result;
    }
}

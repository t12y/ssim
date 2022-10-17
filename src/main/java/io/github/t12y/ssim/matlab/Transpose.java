package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;

public class Transpose {
    public static Matrix transpose(Matrix ref) {
        Matrix result = new Matrix(ref.width, ref.height, ref.data.length);

        for (int i = 0; i < ref.height; i++) {
            for (int j = 0; j < ref.width; j++) {
                result.data[j * ref.height + i] = ref.data[i * ref.width + j];
            }
        }

        return result;
    }
}

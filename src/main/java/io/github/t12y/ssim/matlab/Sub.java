package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;

public class Sub {
    public static Matrix sub(Matrix ref, int x, int height, int y, int width) {
        Matrix result = new Matrix(height, width, width * height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.data[i * width + j] = ref.data[(y + i) * ref.width + x + j];
            }
        }

        return result;
    }
}

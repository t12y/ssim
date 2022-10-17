package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;

public class Skip2d {
    public static Matrix skip2d(Matrix A, int startRow, int everyRow, int endRow, int startCol, int everyCol, int endCol) {
        int width = (int)Math.ceil((double)(endCol - startCol) / everyCol);
        int height = (int)Math.ceil((double)(endRow - startRow) / everyRow);
        double[] data = new double[width * height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int Ai = startRow + i * everyRow;
                int Aj = startCol + j * everyCol;

                data[i * width + j] = A.data[Ai * A.width + Aj];
            }
        }

        return new Matrix(height, width, data);
    }
}

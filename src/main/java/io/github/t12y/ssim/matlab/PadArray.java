package io.github.t12y.ssim.matlab;

import io.github.t12y.ssim.models.Matrix;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import static io.github.t12y.ssim.matlab.Mod.mod;

public class PadArray {
    private static Matrix mirrorHorizonal(Matrix ref) {
        Matrix result = new Matrix(ref);

        for (int x = 0; x < ref.height; x++) {
            for (int y = 0; y < ref.width; y++) {
                result.data[x * ref.width + y] = ref.data[x * ref.width + ref.width - 1 - y];
            }
        }

        return result;
    }

    private static Matrix mirrorVertical(Matrix ref) {
        Matrix result = new Matrix(ref);

        for (int x = 0; x < ref.height; x++) {
            for (int y = 0; y < ref.width; y++) {
                result.data[x * ref.width + y] = ref.data[(ref.height - 1 - x) * ref.width + y];
            }
        }

        return result;
    }

    private static Matrix concatHorizontal(Matrix A, Matrix B) {
        int width = A.width + B.width;
        double[] data = new double[A.height * width];

        for (int x = 0; x < A.height; x++) {
            for (int y = 0; y < A.width; y++) {
                data[x * width + y] = A.data[x * A.width + y];
            }
            for (int y = 0; y < B.width; y++) {
                data[x * width + y + A.width] = B.data[x * B.width + y];
            }
        }

        return new Matrix(A.height, width, data);
    }

    private static Matrix concatVertical(Matrix A, Matrix B) {
        double[] data = DoubleStream.concat(Arrays.stream(A.data), Arrays.stream(B.data)).toArray();
        return new Matrix(A.height + B.height, A.width, data);
    }

    private static Matrix padHorizontal(Matrix A, int pad) {
        int width = A.width + 2 * pad;
        double[] data = new double[width * A.height];
        Matrix mirrored = concatHorizontal(A, mirrorHorizonal(A));

        for (int x = 0; x < A.height; x++) {
            for (int y = -pad; y < A.width + pad; y++) {
                data[x * width + y + pad] = mirrored.data[x * mirrored.width + mod(y, mirrored.width)];
            }
        }

        return new Matrix(A.height, width, data);
    }

    private static Matrix padVertical(Matrix A, int pad) {
        Matrix mirrored = concatVertical(A, mirrorVertical(A));
        int height = A.height + pad * 2;
        double[] data = new double[A.width * height];

        for (int x = -pad; x < A.height + pad; x++) {
            for (int y = 0; y < A.width; y++) {
                data[(x + pad) * A.width + y] = mirrored.data[mod(x, mirrored.height) * A.width + y];
            }
        }

        return new Matrix(height, A.width, data);
    }

    private static Matrix fastPadding(Matrix A, int padHeight, int padWidth) {
        int width = A.width + padWidth * 2;
        int height = A.height + padHeight * 2;
        double[] data = new double[width * height];

        for (int x = -padHeight; x < 0; x++) {
            // A
            for (int y = -padWidth; y < 0; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[(Math.abs(x) - 1) * A.width + Math.abs(y) - 1];
            }
            // B
            for (int y = 0; y < A.width; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[(Math.abs(x) - 1) * A.width + y];
            }
            // C
            for (int y = A.width; y < A.width + padWidth; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[(Math.abs(x) - 1) * A.width + 2 * A.width - y - 1];
            }
        }

        for (int x = 0; x < A.height; x++) {
            // D
            for (int y = -padWidth; y < 0; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[x * A.width + Math.abs(y) - 1];
            }
            // E
            for (int y = 0; y < A.width; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[x * A.width + y];
            }
            // F
            for (int y = A.width; y < A.width + padWidth; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[x * A.width + 2 * A.width - y - 1];
            }
        }

        for (int x = A.height; x < A.height + padHeight; x++) {
            // G
            for (int y = -padWidth; y < 0; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[(2 * A.height - x - 1) * A.width + Math.abs(y) - 1];
            }
            // H
            for (int y = 0; y < A.width; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[(2 * A.height - x - 1) * A.width + y];
            }
            // I
            for (int y = A.width; y < A.width + padWidth; y++) {
                data[(x + padHeight) * width + y + padWidth] = A.data[(2 * A.height - x - 1) * A.width + 2 * A.width - y - 1];
            }
        }

        return new Matrix(height, width, data);
    }

    public static Matrix padarray(Matrix A, int padHeight, int padWidth) {
        if (A.height >= padHeight && A.width >= padWidth) {
            return fastPadding(A, padHeight, padWidth);
        }

        return padVertical(padHorizontal(A, padWidth), padHeight);
    }
}

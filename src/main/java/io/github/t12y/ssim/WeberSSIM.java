package io.github.t12y.ssim;

import io.github.t12y.ssim.models.MSSIMMatrix;
import io.github.t12y.ssim.models.Matrix;
import io.github.t12y.ssim.models.Options;

public class WeberSSIM {
    interface Fn3 {
        public double call(double v, int x, int y);
    }

    interface Fn4 {
        public double call(double a, double b, int x, int y);
    }

    private static double[] edgeHandler(int w, int h, double[] sumArray, int matrixWidth) {
        double rightEdge = sumArray[h * matrixWidth + w + 1];
        double bottomEdge = sumArray[(h + 1) * matrixWidth + w];
        double bottomRightEdge = sumArray[(h + 1) * matrixWidth + w + 1];
        return new double[]{ rightEdge, bottomEdge, bottomRightEdge };
    }

    private static Matrix partialSumMatrix1(Matrix pixels, Fn3 f) {
        int matrixWidth = pixels.width + 1;
        int matrixHeight = pixels.height + 1;
        double[] sumArray = new double[matrixWidth * matrixHeight];

        for (int h = pixels.height - 1; h >= 0; --h) {
            for (int w = pixels.width - 1; w >= 0; --w) {
                double[] edge = edgeHandler(w, h, sumArray, matrixWidth);
                sumArray[h * matrixWidth + w] = f.call(pixels.data[h * pixels.width + w], w, h) + edge[0] + edge[1] - edge[2];
            }
        }

        return new Matrix(matrixHeight, matrixWidth, sumArray);
    }

    private static Matrix partialSumMatrix2(Matrix pixels1, Matrix pixels2, Fn4 f) {
        int matrixWidth = pixels1.width + 1;
        int matrixHeight = pixels1.height + 1;
        double[] sumArray = new double[matrixWidth * matrixHeight];
        for (int h = pixels1.height - 1; h >= 0; --h) {
            for (int w = pixels1.width - 1; w >= 0; --w) {
                double[] edge = edgeHandler(w, h, sumArray, matrixWidth);
                int offset = h * pixels1.width + w;
                sumArray[h * matrixWidth + w] = f.call(pixels1.data[offset], pixels2.data[offset], w, h) + edge[0] + edge[1] - edge[2];
            }
        }
        return new Matrix(matrixHeight, matrixWidth, sumArray);
    }

    private static Matrix windowMatrix(Matrix sumMatrix, int windowSize, int divisor) {
        int imageWidth = sumMatrix.width - 1;
        int imageHeight = sumMatrix.height - 1;
        int windowWidth = imageWidth - windowSize + 1;
        int windowHeight = imageHeight - windowSize + 1;
        double[] windows = new double[windowWidth * windowHeight];

        for (int h = 0; h < imageHeight; ++h) {
            for (int w = 0; w < imageWidth; ++w) {
                if (w < windowWidth && h < windowHeight) {
                    double sum = sumMatrix.data[sumMatrix.width * h + w] -
                            sumMatrix.data[sumMatrix.width * h + w + windowSize] -
                            sumMatrix.data[sumMatrix.width * (h + windowSize) + w] +
                            sumMatrix.data[sumMatrix.width * (h + windowSize) + w + windowSize];

                    windows[h * windowWidth + w] = sum / divisor;
                }
            }
        }

        return new Matrix(windowHeight, windowWidth, windows);
    }

    private static Matrix windowSums(Matrix pixels, int windowSize) {
        return windowMatrix(partialSumMatrix1(pixels, (v, x, y) -> v), windowSize,1);
    }

    private static Matrix windowVariance(Matrix pixels, Matrix sums, int windowSize) {
        int windowSquared = windowSize * windowSize;
        Matrix varX = windowMatrix(partialSumMatrix1(pixels, (v, x, y) -> v * v), windowSize, 1);

        for (int i = 0; i < sums.data.length; ++i) {
            double mean = sums.data[i] / windowSquared;
            double sumSquares = varX.data[i] / windowSquared;

            double squareMeans = mean * mean;
            varX.data[i] = 1024 * (sumSquares - squareMeans);
        }

        return varX;
    }

    private static Matrix windowCovariance(Matrix pixels1, Matrix pixels2, Matrix sums1, Matrix sums2, int windowSize) {
        int windowSquared = windowSize * windowSize;
        Matrix covXY = windowMatrix(partialSumMatrix2(pixels1, pixels2, (a, b, x, y) -> a * b), windowSize, 1);

        for (int i = 0; i < sums1.data.length; ++i) {
            covXY.data[i] = 1024 * (covXY.data[i] / windowSquared - (sums1.data[i] / windowSquared) * (sums2.data[i] / windowSquared));
        }

        return covXY;
    }

    protected static MSSIMMatrix weberSSIM(Matrix pixels1, Matrix pixels2, Options options) {
        double L = java.lang.Math.pow(2, options.bitDepth) - 1;
        double c1 = options.k1 * L * (options.k1 * L);
        double c2 = options.k2 * L * (options.k2 * L);
        int windowSquared = options.windowSize * options.windowSize;

        for (int i=0; i<pixels1.data.length; i++) {
            pixels1.data[i] = (int)(pixels1.data[i] + 0.5);
        }
        for (int i=0; i<pixels2.data.length; i++) {
            pixels2.data[i] = (int)(pixels2.data[i] + 0.5);
        }

        Matrix sums1 = windowSums(pixels1, options.windowSize);
        Matrix variance1 = windowVariance(pixels1, sums1, options.windowSize);

        Matrix sums2 = windowSums(pixels2, options.windowSize);
        Matrix variance2 = windowVariance(pixels2, sums2, options.windowSize);
        Matrix covariance = windowCovariance(pixels1, pixels2, sums1, sums2, options.windowSize);
        int size = sums1.data.length;

        double mssim = 0;
        double[] ssims = new double[size];
        for (int i = 0; i < size; ++i) {
            double meanx = sums1.data[i] / windowSquared;
            double meany = sums2.data[i] / windowSquared;
            double varx = variance1.data[i] / 1024;
            double vary = variance2.data[i] / 1024;
            double cov = covariance.data[i] / 1024;
            double na = 2 * meanx * meany + c1;
            double nb = 2 * cov + c2;
            double da = meanx * meanx + meany * meany + c1;
            double db = varx + vary + c2;
            double ssim = (na * nb) / da / db;
            ssims[i] = ssim;

            if (i == 0) {
                mssim = ssim;
            } else {
                mssim = mssim + (ssim - mssim) / (i + 1);
            }
        }

        return new MSSIMMatrix(mssim, sums1.height, sums1.width, ssims);
    }
}

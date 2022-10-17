package io.github.t12y.ssim.models;

public class MSSIMMatrix extends Matrix {
    public double mssim;

    public MSSIMMatrix(Matrix m, double mssim) {
        super(m.height, m.width, m.data);
        this.mssim = mssim;
    }

    public MSSIMMatrix(double mssim, int height, int width, double[] data) {
        super(height, width, data);
        this.mssim = mssim;
    }
}

package io.github.t12y.ssim.models;

public class Matrix {
    public int width;
    public int height;
    public double[] data;

    public Matrix(){}

    public Matrix(int height, int width, int size) {
        this.height = height;
        this.width = width;
        this.data = new double[size];
    }

    public Matrix(int height, int width, double[] data) {
        this.height = height;
        this.width = width;
        this.data = data;
    }

    public Matrix(Matrix m) {
        this.height = m.height;
        this.width = m.width;
        this.data = new double[m.data.length];
    }

    public void set(Matrix m) {
        this.height = m.height;
        this.width = m.width;
        this.data = m.data;
    }
}

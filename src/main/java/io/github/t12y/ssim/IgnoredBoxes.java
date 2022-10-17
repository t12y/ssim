package io.github.t12y.ssim;

import io.github.t12y.ssim.models.MSSIMMatrix;
import io.github.t12y.ssim.models.Options;

public class IgnoredBoxes {
    protected static void adjustMSSIM(int height, int width, MSSIMMatrix mssimMatrix, Options options) {
        if (options.ignoredBoxes == null || options.ignoredBoxes.length == 0) return;

        // throw out all SSIM windows inside ignored boxes
        int mapHeight = mssimMatrix.height;
        int mapWidth = mssimMatrix.width;
        double[] mapData = mssimMatrix.data;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int mapIdx = (int) (
                        (mapWidth * java.lang.Math.floor((double) (mapHeight * y) / height)) +
                                java.lang.Math.floor((double) (mapWidth * x) / width)
                );
                if (mapData[mapIdx] == 1.0) continue; // already perfect match

                for (int i = 0; i < options.ignoredBoxes.length; i++) {
                    int[] box = options.ignoredBoxes[i];
                    boolean isWithinIgnoreBox = x >= box[0] && x <= box[1] && y >= box[2] && y <= box[3];

                    if (isWithinIgnoreBox) {
                        mapData[mapIdx] = 1.0;
                        break;
                    }
                }
            }
        }

        // re-calculate mean SSIM with ignored window scores
        double sum = 0.0;
        for (double mapDatum : mapData) {
            sum += mapDatum;
        }

        mssimMatrix.mssim = sum / mapData.length;
    }
}

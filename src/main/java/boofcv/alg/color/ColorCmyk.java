/*
 * Copyright (c) 2021, Nelson Gonçalves. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boofcv.alg.color;

import boofcv.alg.color.impl.ImplColorCmyk;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageGray;
import boofcv.struct.image.Planar;

public class ColorCmyk {

    public static int cmykToRgb(double c, double m, double y, double k) {
        int r = (int) (255.0 * (1.0 - c / 100.0) * (1.0 - k / 100.0));
        int g = (int) (255.0 * (1.0 - m / 100.0) * (1.0 - k / 100.0));
        int b = (int) (255.0 * (1.0 - y / 100.0) * (1.0 - k / 100.0));
        return (r << 16) | (g << 8) | b;
    }

    public static void cmykToRgb(double c, double m, double y, double k, double[] rgb) {
        rgb[0] = 255 * (1 - c / 100) * (1 - k / 100);
        rgb[1] = 255 * (1 - m / 100) * (1 - k / 100);
        rgb[2] = 255 * (1 - y / 100) * (1 - k / 100);
    }

    public static void cmykToRgb(float c, float m, float y, float k, float[] rgb) {
        rgb[0] = 255 * (1 - c / 100) * (1 - k / 100);
        rgb[1] = 255 * (1 - m / 100) * (1 - k / 100);
        rgb[2] = 255 * (1 - y / 100) * (1 - k / 100);
    }

    public static void rgbToCmyk(double r, double g, double b, double[] cmyk) {
        double percentageR = r / 255 * 100;
        double percentageG = g / 255 * 100;
        double percentageB = b / 255 * 100;

        double k = 100 - Math.max(Math.max(percentageR, percentageG), percentageB);
        cmyk[3] = k;

        if (k == 100) {
            cmyk[0] = 0;
            cmyk[1] = 0;
            cmyk[2] = 0;
            return;
        }

        cmyk[0] = (100 - percentageR - k) / (100 - k) * 100;
        cmyk[1] = (100 - percentageG - k) / (100 - k) * 100;
        cmyk[2] = (100 - percentageB - k) / (100 - k) * 100;
    }

    public static void rgbToCmyk(float r, float g, float b, float[] cmyk) {
        float percentageR = r / 255 * 100;
        float percentageG = g / 255 * 100;
        float percentageB = b / 255 * 100;

        float k = 100 - Math.max(Math.max(percentageR, percentageG), percentageB);
        cmyk[3] = k;

        if (k == 100) {
            cmyk[0] = 0;
            cmyk[1] = 0;
            cmyk[2] = 0;
            return;
        }

        cmyk[0] = (100 - percentageR - k) / (100 - k) * 100;
        cmyk[1] = (100 - percentageG - k) / (100 - k) * 100;
        cmyk[2] = (100 - percentageB - k) / (100 - k) * 100;
    }

    public static <T extends ImageGray<T>> void cmykToRgb(Planar<T> cmyk, Planar<T> rgb) {
        rgb.reshape(cmyk.width, cmyk.height, 3);
        if (cmyk.getBandType() == GrayF32.class) {
            ImplColorCmyk.cmykToRgb_F32((Planar<GrayF32>) cmyk, (Planar<GrayF32>) rgb);
        } else {
            throw new IllegalArgumentException("Unsupported band type " + cmyk.getBandType().getSimpleName());
        }
    }

    public static <T extends ImageGray<T>> void rgbToCmyk(Planar<T> rgb, Planar<T> cmyk) {
        cmyk.reshape(rgb.width, rgb.height, 4);
        if (cmyk.getBandType() == GrayF32.class) {
            ImplColorCmyk.rgbToCmyk_F32((Planar<GrayF32>) rgb, (Planar<GrayF32>) cmyk);
        } else {
            throw new IllegalArgumentException("Unsupported band type " + cmyk.getBandType().getSimpleName());
        }
    }

}

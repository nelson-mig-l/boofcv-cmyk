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
package boofcv.alg.color.impl;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

public class ImplColorCmyk {

    public static void cmykToRgb_F32(Planar<GrayF32> cmyk, Planar<GrayF32> rgb) {
        GrayF32 C = cmyk.getBand(0);
        GrayF32 M = cmyk.getBand(1);
        GrayF32 Y = cmyk.getBand(2);
        GrayF32 K = cmyk.getBand(3);

        GrayF32 R = rgb.getBand(0);
        GrayF32 G = rgb.getBand(1);
        GrayF32 B = rgb.getBand(2);

        for (int row = 0; row < cmyk.height; row++) {
            int indexCmyk = cmyk.startIndex + row * cmyk.stride;
            int indexRgb = rgb.startIndex + row * rgb.stride;
            int endRgb = indexRgb + cmyk.width;

            for (; indexRgb < endRgb; indexCmyk++, indexRgb++) {
                float c = C.data[indexCmyk];
                float m = M.data[indexCmyk];
                float y = Y.data[indexCmyk];
                float k = K.data[indexCmyk];

                R.data[indexRgb] = 255 * (1 - c / 100) * (1 - k / 100);
                G.data[indexRgb] = 255 * (1 - m / 100) * (1 - k / 100);
                B.data[indexRgb] = 255 * (1 - y / 100) * (1 - k / 100);
            }
        }
    }

    public static void rgbToCmyk_F32(Planar<GrayF32> rgb, Planar<GrayF32> cmyk) {
        GrayF32 R = rgb.getBand(0);
        GrayF32 G = rgb.getBand(1);
        GrayF32 B = rgb.getBand(2);

        GrayF32 C = cmyk.getBand(0);
        GrayF32 M = cmyk.getBand(1);
        GrayF32 Y = cmyk.getBand(2);
        GrayF32 K = cmyk.getBand(3);

        for (int row = 0; row < cmyk.height; row++) {
            int indexRgb = rgb.startIndex + row * rgb.stride;
            int indexCmyk = cmyk.startIndex + row * cmyk.stride;
            int endRgb = indexRgb + cmyk.width;

            for (; indexRgb < endRgb; indexCmyk++, indexRgb++) {

                float r = R.data[indexRgb];
                float g = G.data[indexRgb];
                float b = B.data[indexRgb];

                float percentageR = r / 255 * 100;
                float percentageG = g / 255 * 100;
                float percentageB = b / 255 * 100;

                float k = 100 - Math.max(Math.max(percentageR, percentageG), percentageB);
                K.data[indexCmyk] = k;

                if (k == 100) {
                    C.data[indexCmyk] = 0;
                    M.data[indexCmyk] = 0;
                    Y.data[indexCmyk] = 0;
                } else {
                    C.data[indexCmyk] = (100 - percentageR - k) / (100 - k) * 100;
                    M.data[indexCmyk] = (100 - percentageG - k) / (100 - k) * 100;
                    Y.data[indexCmyk] = (100 - percentageB - k) / (100 - k) * 100;
                }
            }
        }
    }
}

/*
 * Copyright 2013 Japplis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joeffice.wordprocessor.app;

/**
 * Convert pixels to inches and vice versa.
 * 
 * @author Stanislav Lapitsky
 */
public class PixelConverter {

    /**
     * Calculate size (pixels) for given size in inches.
     *
     * @param inchSize size in inches.
     * @return
     */
    public static int converInchesToPixels(double inchSize) {
        return (int) Math.round(inchSize * 72);
    }

    /**
     * Calculate size (inches) for given size in pixels.
     *
     * @param pixSize size in pixels.
     * @return
     */
    public static double converPixelsToInches(int pixSize) {
        int factor = (int) Math.pow(10, 5);
        double result = ((double) Math.round(((double) pixSize) / 72 * factor)) / factor;
        return result;
    }

}

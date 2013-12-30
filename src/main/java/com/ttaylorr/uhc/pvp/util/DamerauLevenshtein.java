package com.ttaylorr.uhc.pvp.util;

public class DamerauLevenshtein {
    public static int Compute(String a, String b, int threshold)
    {
        char[] aChars = a.toCharArray();
        char[] bChars = b.toCharArray();

        // Ensure arrays [i] / aLength use shorter length
        if (aChars.length > bChars.length)
        {
            char[] tmpChars = bChars;
            bChars = aChars;
            aChars = tmpChars;
        }

        int aLength = aChars.length;
        int bLength = bChars.length;

        // Return trivial case - difference in string lengths exceeds threshold
        if (bLength - aLength > threshold) { return Integer.MAX_VALUE; }

        int[] dCurrent = new int[aLength + 1];
        int[] dMinus1 = new int[aLength + 1];
        int[] dMinus2 = new int[aLength + 1];
        int[] dSwap;

        for (int i = 0; i <= aLength; i++) { dCurrent[i] = i; }

        int jm1 = 0;

        for (int j = 1; j <= bLength; j++)
        {
            // Rotate
            dSwap = dMinus2;
            dMinus2 = dMinus1;
            dMinus1 = dCurrent;
            dCurrent = dSwap;

            // Initialize
            int minDistance = Integer.MAX_VALUE;
            dCurrent[0] = j;
            int im1 = 0;
            int im2 = -1;

            for (int i = 1; i <= aLength; i++)
            {
                int cost = aChars[im1] == bChars[jm1] ? 0 : 1;

                int del = dCurrent[im1] + 1;
                int ins = dMinus1[i] + 1;
                int sub = dMinus1[im1] + cost;

                //Fastest execution for min value of 3 integers
                int min = (del > ins) ? (ins > sub ? sub : ins) : (del > sub ? sub : del);

                if (i > 1 && j > 1 && aChars[im2] == bChars[jm1] && aChars[im1] == bChars[j - 2])
                    min = Math.min(min, dMinus2[im2] + cost);

                dCurrent[i] = min;
                if (min < minDistance) { minDistance = min; }
                im1++;
                im2++;
            }
            jm1++;
            if (minDistance > threshold) { return Integer.MAX_VALUE; }
        }

        int result = dCurrent[aLength];
        return (result > threshold) ? Integer.MAX_VALUE : result;
    }
}

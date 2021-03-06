package com.distributed.systems.dom_judge.test;

import java.io.*;

public class info {

    public static void main(String[] args) throws IOException {
        File input = new File("/home/radkal2/Desktop/input.txt");
        File outputSum = new File("/home/radkal2/Desktop/output_sum.txt");
        File outputMul = new File("/home/radkal2/Desktop/output_mul.txt");
        File outputSub = new File("/home/radkal2/Desktop/output_sub.txt");
        FileWriter inputFR = new FileWriter(input);
        FileWriter outputSumFR = new FileWriter(outputSum);
        FileWriter outputMulFR = new FileWriter(outputMul);
        FileWriter outputSubFR = new FileWriter(outputSub);
        for (int i = 0; i < 100; i++) {
            int a = (int) (Math.random() * 100);
            int b = (int) (Math.random() * 100);
            inputFR.write(a +" " + b +"\n");
            outputSumFR.write(a + b + "\n");
            outputMulFR.write(a * b + "\n");
            outputSubFR.write(a - b + "\n");
        }
        inputFR.close();
        outputSumFR.close();
        outputMulFR.close();
        outputSubFR.close();
    }
}

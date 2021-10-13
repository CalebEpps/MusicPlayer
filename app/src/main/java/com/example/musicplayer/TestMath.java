package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Collections;
class FormulaDivide {

    public ArrayList<Integer> formula(int totalTime, int currentTime) {
        int tracker = 0;
        int originalTime = currentTime;
        ArrayList<Integer> toReturn = new ArrayList<>();
        int endCircle = 0;
        while(endCircle < 2) {
            if (currentTime + 30 > totalTime) {
                currentTime = 30 - (totalTime - currentTime);
                endCircle++;
                if(endCircle == 2) {
                    toReturn.add(originalTime);
                    break;
                } else {
                    toReturn.add(currentTime);
                }
            } else {
                currentTime = currentTime + 30;
                toReturn.add(currentTime);
            }
        }
        Collections.sort(toReturn);
        for(int i = 0; i < toReturn.size() - 1; i++) {
            if(Math.abs(toReturn.get(i+1)- toReturn.get(i)) < 30) {
                toReturn.remove(i);
            }
        }

        return toReturn;
    }

}

public class TestMath {

    public static void main(String[] args) {
        ArrayList<Integer> values = new ArrayList<Integer>();
        FormulaDivide formulaDivide = new FormulaDivide();
        int currentTime  = 180;
        int totalTime = 253;
        values = formulaDivide.formula(totalTime, currentTime);

        for (Integer value : values) {
            System.out.print(value + " ");
        }

    }

}


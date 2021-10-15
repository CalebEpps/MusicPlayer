package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Collections;

class FormulaDivide {

    public ArrayList<Integer> FFFormula(int totalTime, int currentTime) {
        int tracker = 0;
        int originalTime = currentTime;
        ArrayList<Integer> toReturn = new ArrayList<>();
        int endCircle = 0;
        while(endCircle < 2) {
            if (currentTime + 30000 > totalTime) {
                currentTime = 30000 - (totalTime - currentTime);
                endCircle++;
                if(endCircle == 2) {
                    toReturn.add(originalTime);
                    break;
                } else {
                    toReturn.add(currentTime);
                }
            } else {
                currentTime = currentTime + 30000;
                toReturn.add(currentTime);
            }
        }
        Collections.sort(toReturn);
        for(int i = 0; i < toReturn.size() - 1; i++) {
            if(Math.abs(toReturn.get(i+1)- toReturn.get(i)) < 30000) {
                toReturn.remove(i);
            }
        }



        return toReturn;
    }

    public ArrayList<Integer> RRformula(int totalTime, int currentTime) {
        ArrayList<Integer> toReturn = new ArrayList<>();
        int timesToRun = 0;
        int originalTime = currentTime;
        int endCircle = 0;
        //int originalTime = currentTime;
        toReturn.add(currentTime);
        while(endCircle < 2) {
            if(currentTime - 30000 < 0) {
                if(endCircle == 2) {
                    toReturn.add(originalTime);
                    break;
                }  else {
                    endCircle++;
                    toReturn.add(currentTime);
                }
                currentTime = totalTime - (30000 - currentTime);
                System.out.println(currentTime+ "   ELSE IF");
            } else {
                currentTime = currentTime - 30000;
                System.out.println(currentTime+"     ELSE");
            }
            toReturn.add(currentTime);
            timesToRun++;
        }
        Collections.sort(toReturn);

        for(int i = 0; i < toReturn.size() - 1; i++) {
            if(Math.abs(toReturn.get(i+1)- toReturn.get(i)) < 30000) {
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
        values = formulaDivide.FFFormula(totalTime, currentTime);

        for (Integer value : values) {
            System.out.print(value + " ");
        }

    }

}

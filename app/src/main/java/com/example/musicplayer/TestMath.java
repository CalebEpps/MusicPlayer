package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Collections;

class FormulaDivide {

    public ArrayList<Integer> FFFormula(int totalTime, int currentTime) {
        ArrayList<Integer> toReturn = new ArrayList<>();
        int timesToRun = 0;
        boolean passedZero = false;
        //int originalTime = currentTime;
        toReturn.add(currentTime);
        while (timesToRun <= 3) {

            if (currentTime + 30000 > totalTime) {
                currentTime = 30000 - (totalTime - currentTime);
                System.out.println(currentTime + "   IF");
                toReturn.add(currentTime);
                passedZero = true;
            } else if (currentTime - 30000 < 0 && passedZero == false && timesToRun > 1) {
                currentTime = totalTime - (30000 - currentTime);
                System.out.println(currentTime + "   ELSE IF");
                toReturn.add(currentTime);
            } else {
                currentTime = currentTime + 30000;
                System.out.println(currentTime + "     ELSE");
                toReturn.add(currentTime);
            }
            timesToRun++;
        }

        Collections.sort(toReturn);

        return toReturn;
    }

    public ArrayList<Integer> RRformula(int totalTime, int currentTime) {
        ArrayList<Integer> toReturn = new ArrayList<>();
        int timesToRun = 0;
        boolean passedZero = false;
        //int originalTime = currentTime;
        toReturn.add(currentTime);
        while(timesToRun <= 3) {
            if(currentTime - 30000 < 0 && !passedZero) {
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


package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Collections;

class FormulaDivide {

    public ArrayList<Integer> FFFormula(int totalTime, int currentTime, int howToDivide) {
        // We need to create a variable to store the original time
        // because we're going to RAVAGE the currentTime variable.
        int originalTime = currentTime;
        // The arraylist we'll return
        ArrayList<Integer> toReturn = new ArrayList<>();
        // This variable tracks the number of times we've traversed
        // the ENTIRE song.
        int endCircle = 0;
        // We break the loop if we've passed the song's total length more than once.
        while(endCircle < 2) {
            // Example of this:
            // currentTime = 175. totalTime = 180 (in seconds).
            // currentTime = 30 - (180 - 175) = 25 seconds, which is where
            // we'd want to be :D
            if (currentTime + howToDivide > totalTime) {
                currentTime = howToDivide - (totalTime - currentTime);
                endCircle++;
                if(endCircle == 2) {
                    // We add the original time once we yeet out of the loop.
                   // toReturn.add(originalTime);
                    break;
                } else {
                    toReturn.add(currentTime);
                }
                // Nothing spicy happening? Cool, let's just add 30 seconds.
            } else {
                currentTime = currentTime + howToDivide;
                toReturn.add(currentTime);
            }
        }
        // This is a fancy way of sorting an arraylist
        Collections.sort(toReturn);
        // SOOOOOO We gotta make sure we only have "howToDivide" second intervals in our list.
        // This code does it. :D
        for(int i = 0; i < toReturn.size() - 1; i++) {
            if(Math.abs(toReturn.get(i+1)- toReturn.get(i)) < howToDivide) {
                toReturn.remove(i);
            }
        }



        return toReturn;
    }
// Read the FF Formula, it's basically the same minus the math.
    public ArrayList<Integer> RRformula(int totalTime, int currentTime, int howToDivide) {
        ArrayList<Integer> toReturn = new ArrayList<>();
        int timesToRun = 0;
        int endCircle = 0;
        toReturn.add(currentTime);
        while(endCircle < 2) {
            if(currentTime - howToDivide < 0) {
                if(endCircle == 2) {
                    //toReturn.add(originalTime);
                    break;
                }  else {
                    endCircle++;
                    toReturn.add(currentTime);
                }
                currentTime = totalTime - (howToDivide - currentTime);
                System.out.println(currentTime+ "   ELSE IF");
            } else {
                currentTime = currentTime - howToDivide;
                System.out.println(currentTime+"     ELSE");
            }
            toReturn.add(currentTime);
            timesToRun++;
        }
        Collections.sort(toReturn);

        for(int i = 0; i < toReturn.size() - 1; i++) {
            if(Math.abs(toReturn.get(i+1)- toReturn.get(i)) < howToDivide) {
                toReturn.remove(i);
            }
        }


        return toReturn;

    }
}
// This is an old test class we don't really need anymore. :)
// Just here for reference really, or if the math needs to change.
public class TestMath {

    public static void main(String[] args) {
        ArrayList<Integer> values = new ArrayList<Integer>();
        FormulaDivide formulaDivide = new FormulaDivide();
        int currentTime  = 180;
        int totalTime = 253;
        values = formulaDivide.FFFormula(totalTime, currentTime, 15000);

        for (Integer value : values) {
            System.out.print(value + " ");
        }

    }

}

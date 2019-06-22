package com.airwallex.codechallenge;

import com.airwallex.codechallenge.impl.JsonStandardOutputListener;
import com.airwallex.codechallenge.impl.RisingFallingRule;
import com.airwallex.codechallenge.impl.SpotChangeRule;
import com.airwallex.codechallenge.input.Reader;
import com.airwallex.codechallenge.process.AlertGenerator;

public class App {

    public static void main(String[] args) {
        Reader reader = new Reader();
        AlertGenerator alertGenerator = new AlertGenerator()
                .registerAlertRule(new SpotChangeRule())
                .registerAlertRule(new RisingFallingRule())
                .registerAlertListener(new JsonStandardOutputListener());

        reader
                .read(args[0])
                .forEach(alertGenerator::consume);
    }

}

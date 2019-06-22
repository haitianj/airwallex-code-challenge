package com.airwallex.codechallenge.impl;

import com.airwallex.codechallenge.interfaces.AlertListener;
import com.airwallex.codechallenge.jackson.AlertPrinter;
import com.airwallex.codechallenge.output.Alert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

public class JsonStandardOutputListener implements AlertListener {

    /*
    Examples from README.md:
        { "timestamp": 1554933784.023, "currencyPair": "CNYAUD", "alert": "spotChange" }
        { "timestamp": 1554933784.023, "currencyPair": "CNYAUD", "alert": "falling", "seconds": 3420 }
    */

    private final ObjectWriter writer;

    public JsonStandardOutputListener() {
        writer = new ObjectMapper().registerModule(new KotlinModule()).writer(new AlertPrinter());
    }

    @Override
    public void notify(Alert alert) {
        try {
            System.out.println(writer.writeValueAsString(alert));
        } catch (Exception ignore) {
            // don't pollute the standard output with exception message
        }
    }
}

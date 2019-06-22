package com.airwallex.codechallenge.impl;

import com.airwallex.codechallenge.interfaces.AlertListener;
import com.airwallex.codechallenge.jackson.AlertPrinter;
import com.airwallex.codechallenge.output.Alert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock AlertListener for testing purpose as json lines are cached for later comparison.
 */
public class MockJsonLinesListener implements AlertListener {

    private final List<String> jsonLines;
    private final ObjectWriter writer;

    public MockJsonLinesListener() {
        jsonLines = new ArrayList<>();
        writer = new ObjectMapper().registerModule(new KotlinModule()).writer(new AlertPrinter());
    }

    @Override
    public void notify(Alert alert) {
        try {
            jsonLines.add(writer.writeValueAsString(alert));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getJsonLines() {
        return jsonLines;
    }
}

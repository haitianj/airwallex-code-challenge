package com.airwallex.codechallenge.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import java.io.IOException;

public class AlertPrinter extends MinimalPrettyPrinter {

    /*
    Examples from README.md:
        { "timestamp": 1554933784.023, "currencyPair": "CNYAUD", "alert": "spotChange" }
        { "timestamp": 1554933784.023, "currencyPair": "CNYAUD", "alert": "falling", "seconds": 3420 }
    */

    @Override
    public void writeStartObject(JsonGenerator g) throws IOException {
        g.writeRaw('{');
        g.writeRaw(' ');
    }

    @Override
    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
        g.writeRaw(' ');
        g.writeRaw('}');
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(_separators.getObjectFieldValueSeparator());
        g.writeRaw(' ');
    }

    @Override
    public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
        g.writeRaw(_separators.getObjectEntrySeparator());
        g.writeRaw(' ');
    }
}

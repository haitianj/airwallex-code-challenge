package com.airwallex.codechallenge.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

public class AlertInstantSerializer extends JsonSerializer<Instant> {

    /*
    Examples from README.md:
        { "timestamp": 1554933784.023, "currencyPair": "CNYAUD", "alert": "spotChange" }
        { "timestamp": 1554933784.023, "currencyPair": "CNYAUD", "alert": "falling", "seconds": 3420 }
    */

    @Override
    public void serialize(Instant instant, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(BigDecimal.valueOf(instant.toEpochMilli(), 3));
    }
}

package com.airwallex.codechallenge.impl;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.interfaces.AlertRule;
import com.airwallex.codechallenge.output.Alert;

import java.util.*;

/**
 * <i>"when the spot rate for a currency pair changes by more than 10% from the 5 minute average for that currency pair"</i>
 */
public class SpotChangeRule implements AlertRule {

    private final int monitorDurationSeconds;
    private final int alertLimitPercent;
    private final Map<String, Monitor> monitors;

    private class Monitor {
        Queue<Long> previousRates = new LinkedList<>();
        long sum = 0L;
    }

    /**
     * Create a default rule which acts exactly as described in {@link SpotChangeRule}.
     */
    public SpotChangeRule() {
        this(300, 10);
    }

    /**
     * Create a customized rule.
     *
     * @param monitorDurationSeconds Seconds to keep track for average rate calculation
     * @param alertLimitPercent      Percentage of delta between new rate and average rate above which an alert shall be generated
     */
    public SpotChangeRule(int monitorDurationSeconds, int alertLimitPercent) {
        if (monitorDurationSeconds <= 0 || alertLimitPercent <= 0)
            throw new IllegalArgumentException("Both arguments must be positive. Actual: " + monitorDurationSeconds + ", " + alertLimitPercent);
        this.monitorDurationSeconds = monitorDurationSeconds;
        this.alertLimitPercent = alertLimitPercent;
        monitors = new HashMap<>();
    }

    @Override
    public Optional<Alert> evaluate(CurrencyConversionRate rate) {
        // grab the corresponding monitor for this currency pair
        Monitor monitor = monitors.computeIfAbsent(rate.getCurrencyPair(), key -> new Monitor());
        // normalize the original 5 decimal digits rate by multiplying it with 100000 and round the product.
        long normalizedRate = Math.round(rate.getRate() * 100000);
        long averageRate = monitor.previousRates.isEmpty() ? 0L : Math.round(monitor.sum * 1.0 / monitor.previousRates.size());
        // archive the current normalized rate first
        monitor.previousRates.add(normalizedRate);
        monitor.sum += normalizedRate;
        if (monitor.previousRates.size() > monitorDurationSeconds) {
            //noinspection ConstantConditions
            monitor.sum -= monitor.previousRates.poll();
        }
        // generate alert
        if (averageRate == 0L) return Optional.empty();
        long delta = Math.abs(normalizedRate - averageRate);
        if (delta * 100 > averageRate * alertLimitPercent) {
            // { "timestamp": 1554933784.023, "currencyPair": "CNYAUD", "alert": "spotChange" }
            return Optional.of(new Alert(rate.getTimestamp(), rate.getCurrencyPair(), "spotChange", null));
        }
        return Optional.empty();
    }
}

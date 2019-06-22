package com.airwallex.codechallenge.impl;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.interfaces.AlertRule;
import com.airwallex.codechallenge.output.Alert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <i>"when the spot rate has been rising/falling for 15 minutes. This alert should be throttled to only output once per
 * minute and should report the length of time of the rise/fall in seconds."</i>
 */
public class RisingFallingRule implements AlertRule {

    private final int alertMinimumSeconds;
    private final int alertIntervalSeconds;
    private final Map<String, Monitor> monitors;

    private class Monitor {
        Status status = Status.CONSTANT;
        double rate = 0.0;
        int durationSeconds = 0;
        int countdownSeconds = 0;
    }

    private enum Status {
        CONSTANT, RISING, FALLING
    }

    /**
     * Create a default rule which acts exactly as described in {@link RisingFallingRule}.
     */
    public RisingFallingRule() {
        this(900, 60);
    }

    /**
     * Create a customized rule.
     *
     * @param alertMinimumSeconds  Minimum seconds for which the rising/falling status keeps alive to generate an alert
     * @param alertIntervalSeconds Interval in seconds between a new alert and its predecessor
     */
    public RisingFallingRule(int alertMinimumSeconds, int alertIntervalSeconds) {
        if (alertMinimumSeconds <= 1)
            throw new IllegalArgumentException("Argument 'alertMinimumSeconds' must be larger than 1. Actual: " + alertMinimumSeconds);
        if (alertIntervalSeconds <= 0)
            throw new IllegalArgumentException("Argument 'alertIntervalSeconds' must be positive. Actual: " + alertIntervalSeconds);
        this.alertMinimumSeconds = alertMinimumSeconds;
        this.alertIntervalSeconds = alertIntervalSeconds;
        this.monitors = new HashMap<>();
    }

    @Override
    public Optional<Alert> evaluate(CurrencyConversionRate rate) {
        // grab the corresponding monitor for this currency pair
        Monitor monitor = monitors.computeIfAbsent(rate.getCurrencyPair(), key -> new Monitor());
        // imprecise but consistently imprecise
        double currentRate = rate.getRate();
        double previousRate = monitor.rate;
        monitor.rate = currentRate;
        if (previousRate == 0.0) return Optional.empty();
        // detect status
        double delta = currentRate - previousRate;
        if (delta == 0) {
            // reset
            monitor.status = Status.CONSTANT;
            monitor.durationSeconds = 0;
            monitor.countdownSeconds = 0;
            return Optional.empty();
        }
        Status currentStatus = delta > 0 ? Status.RISING : Status.FALLING;
        Status previousStatus = monitor.status;
        monitor.status = currentStatus;
        // check if the ongoing status is still upheld
        if (currentStatus != previousStatus) {
            monitor.durationSeconds = 1;
            monitor.countdownSeconds = Math.min(alertIntervalSeconds, alertMinimumSeconds); // the first alert shall ignore interval restriction
            return Optional.empty();
        }
        // generate alert
        monitor.durationSeconds++;
        monitor.countdownSeconds--;
        if (monitor.durationSeconds >= alertMinimumSeconds && monitor.countdownSeconds <= 0) {
            monitor.countdownSeconds = alertIntervalSeconds;
            // { "timestamp": 1554933784.023, "currencyPair": "CNYAUD", "alert": "falling", "seconds": 3420 }
            return Optional.of(new Alert(rate.getTimestamp(), rate.getCurrencyPair(), currentStatus.toString().toLowerCase(), monitor.durationSeconds));
        }
        return Optional.empty();
    }
}

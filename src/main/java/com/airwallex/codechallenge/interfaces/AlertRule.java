package com.airwallex.codechallenge.interfaces;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.output.Alert;

import java.util.Optional;

public interface AlertRule {

    /**
     * Given a rate, evaluate and generate an alert if the rule's condition is met.
     */
    Optional<Alert> evaluate(CurrencyConversionRate rate);
}

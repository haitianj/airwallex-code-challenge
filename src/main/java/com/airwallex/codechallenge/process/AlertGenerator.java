package com.airwallex.codechallenge.process;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.interfaces.AlertListener;
import com.airwallex.codechallenge.interfaces.AlertRule;
import com.airwallex.codechallenge.output.Alert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple single thread CurrencyConversionRate to Alert generator, which is fully customizable by registering it with
 * different alert rules and listeners.
 * <i>It's your own responsibility to balance the speed and complexity of this service.</i>
 */
public class AlertGenerator {

    private List<AlertRule> rules = new LinkedList<>();
    private List<AlertListener> listeners = new LinkedList<>();

    /**
     * Register this rule to the generator and it will be immediately evaluated against the next coming rate.
     * <i>Registration order is the evaluation order.</i>
     */
    public AlertGenerator registerAlertRule(AlertRule rule) {
        rules.add(rule);
        return this;
    }

    /**
     * Register this listener to the generator and it will be immediately notified with the next generated alert.
     * <i>Registration order is the notification order.</i>
     */
    public AlertGenerator registerAlertListener(AlertListener listener) {
        listeners.add(listener);
        return this;
    }

    /**
     * Remove this rule from future evaluation.
     * <i>Warning: Do not add it back or its behavior will be unpredictable.</i>
     *
     * @return true if the given rule is found and removed.
     */
    @SuppressWarnings("unused")
    public boolean removeAlertRule(AlertRule rule) {
        return rules.remove(rule);
    }

    /**
     * Remove this listener from future notification.
     *
     * @return true if the given listener is found and removed.
     */
    @SuppressWarnings("unused")
    public boolean removeAlertListener(AlertListener listener) {
        return listeners.remove(listener);
    }

    /**
     * The provided rate will be evaluated by all the registered rules and every generated alert will be sent to all the
     * registered listeners.
     */
    public void consume(CurrencyConversionRate rate) {
        List<Alert> alerts = new ArrayList<>();
        // generate alerts
        for (AlertRule rule : rules) {
            rule.evaluate(rate).ifPresent(alerts::add);
        }
        // send alerts
        for (AlertListener listener : listeners) {
            for (Alert alert : alerts) {
                listener.notify(alert);
            }
        }
    }
}

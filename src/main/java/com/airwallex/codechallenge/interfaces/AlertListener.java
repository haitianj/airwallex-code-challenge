package com.airwallex.codechallenge.interfaces;

import com.airwallex.codechallenge.output.Alert;

public interface AlertListener {

    /**
     * Given an alert, do whatever you want with it as long as no RuntimeException is thrown.
     */
    void notify(Alert alert);
}

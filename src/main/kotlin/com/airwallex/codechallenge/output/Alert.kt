package com.airwallex.codechallenge.output

import com.airwallex.codechallenge.jackson.AlertInstantSerializer
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.Instant

data class Alert(
    @JsonSerialize(using = AlertInstantSerializer::class) val timestamp: Instant,
    val currencyPair: String,
    val alert: String,
    @JsonInclude(JsonInclude.Include.NON_NULL) val seconds: Int? = null
)
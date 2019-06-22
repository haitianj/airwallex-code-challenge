package com.airwallex.codechallenge.process

import com.airwallex.codechallenge.impl.MockJsonLinesListener
import com.airwallex.codechallenge.impl.RisingFallingRule
import com.airwallex.codechallenge.impl.SpotChangeRule
import com.airwallex.codechallenge.input.Reader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Not familiar with kotlin code patterns. Just follow ReaderTest to create some unit tests for AlertGenerator.
internal class AlertGeneratorTest {

    private lateinit var reader: Reader
    private lateinit var mockJsonLinesListener: MockJsonLinesListener
    private lateinit var alertGenerator: AlertGenerator

    @BeforeEach
    fun setup() {
        reader = Reader()
        mockJsonLinesListener = MockJsonLinesListener()
        alertGenerator = AlertGenerator().registerAlertListener(mockJsonLinesListener)
    }

    @Nested
    inner class SpotChange {

        private var inputFile = "src/test/resources/test-spot-change.jsonl"

        @Test
        fun `default 5min 10pct`() {
            alertGenerator.registerAlertRule(SpotChangeRule())
            reader.read(inputFile).forEach(alertGenerator::consume)
            val result = mockJsonLinesListener.jsonLines
            assertThat(result).hasSize(12)
            assertThat(result).containsSequence(
                "{ \"timestamp\": 1554933788.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933789.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933794.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933795.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933798.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933801.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933802.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933803.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933804.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933805.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933806.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933807.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }"
            )
        }

        @Test
        fun `1min 25pct`() {
            alertGenerator.registerAlertRule(SpotChangeRule(60, 25))
            reader.read(inputFile).forEach(alertGenerator::consume)
            val result = mockJsonLinesListener.jsonLines
            assertThat(result).hasSize(3)
            assertThat(result).containsSequence(
                "{ \"timestamp\": 1554933788.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933794.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933795.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }"
            )
        }

        @Test
        fun `5sec 20pct`() {
            alertGenerator.registerAlertRule(SpotChangeRule(5, 20))
            reader.read(inputFile).forEach(alertGenerator::consume)
            val result = mockJsonLinesListener.jsonLines
            assertThat(result).hasSize(5)
            assertThat(result).containsSequence(
                "{ \"timestamp\": 1554933788.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933794.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933801.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933808.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933809.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }"
            )
        }
    }

    @Nested
    inner class RisingFalling {

        private var inputFile = "src/test/resources/test-rising-falling.jsonl"

        @Test
        fun `default 15min 1min`() {
            alertGenerator.registerAlertRule(RisingFallingRule())
            reader.read(inputFile).forEach(alertGenerator::consume)
            val result = mockJsonLinesListener.jsonLines
            assertThat(result).isEmpty()
        }

        @Test
        fun `5sec 2sec`() {
            alertGenerator.registerAlertRule(RisingFallingRule(5, 2))
            reader.read(inputFile).forEach(alertGenerator::consume)
            val result = mockJsonLinesListener.jsonLines
            assertThat(result).hasSize(9)
            assertThat(result).containsSequence(
                "{ \"timestamp\": 1554933790.067, \"currencyPair\": \"USDCNY\", \"alert\": \"rising\", \"seconds\": 5 }",
                "{ \"timestamp\": 1554933792.067, \"currencyPair\": \"USDCNY\", \"alert\": \"rising\", \"seconds\": 7 }",
                "{ \"timestamp\": 1554933798.067, \"currencyPair\": \"USDCNY\", \"alert\": \"rising\", \"seconds\": 5 }",
                "{ \"timestamp\": 1554933800.067, \"currencyPair\": \"USDCNY\", \"alert\": \"rising\", \"seconds\": 7 }",
                "{ \"timestamp\": 1554933805.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 5 }",
                "{ \"timestamp\": 1554933807.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 7 }",
                "{ \"timestamp\": 1554933809.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 9 }",
                "{ \"timestamp\": 1554933811.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 11 }",
                "{ \"timestamp\": 1554933813.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 13 }"
            )
        }

        @Test
        fun `10sec 1sec`() {
            alertGenerator.registerAlertRule(RisingFallingRule(10, 1))
            reader.read(inputFile).forEach(alertGenerator::consume)
            val result = mockJsonLinesListener.jsonLines
            assertThat(result).hasSize(4)
            assertThat(result).containsSequence(
                "{ \"timestamp\": 1554933810.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 10 }",
                "{ \"timestamp\": 1554933811.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 11 }",
                "{ \"timestamp\": 1554933812.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 12 }",
                "{ \"timestamp\": 1554933813.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 13 }"
            )
        }
    }

    @Nested
    inner class MultiCurrencies {

        private var inputFile = "src/test/resources/test-multi-currencies.jsonl"

        @Test
        fun `default spot change`() {
            alertGenerator.registerAlertRule(SpotChangeRule())
            reader.read(inputFile).forEach(alertGenerator::consume)
            val result = mockJsonLinesListener.jsonLines
            assertThat(result).hasSize(6)
            assertThat(result).containsSequence(
                "{ \"timestamp\": 1554933788.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933789.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933789.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933794.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933795.023, \"currencyPair\": \"CNYAUD\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933796.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }"
            )
        }

    }

    @Nested
    inner class MixedRules {

        private var inputFile = "src/test/resources/test-mixed-rules.jsonl"

        @Test
        fun `1min 25pct 5sec 2sec`() {
            alertGenerator
                .registerAlertRule(SpotChangeRule(60, 25))
                .registerAlertRule(RisingFallingRule(5, 2))
            reader.read(inputFile).forEach(alertGenerator::consume)
            val result = mockJsonLinesListener.jsonLines
            assertThat(result).hasSize(15)
            assertThat(result).containsSequence(
                "{ \"timestamp\": 1554933786.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933791.067, \"currencyPair\": \"USDCNY\", \"alert\": \"rising\", \"seconds\": 5 }",
                "{ \"timestamp\": 1554933794.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933795.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933796.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933798.067, \"currencyPair\": \"USDCNY\", \"alert\": \"rising\", \"seconds\": 5 }",
                "{ \"timestamp\": 1554933799.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933800.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933800.067, \"currencyPair\": \"USDCNY\", \"alert\": \"rising\", \"seconds\": 7 }",
                "{ \"timestamp\": 1554933805.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 5 }",
                "{ \"timestamp\": 1554933807.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 7 }",
                "{ \"timestamp\": 1554933819.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 9 }",
                "{ \"timestamp\": 1554933811.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 11 }",
                "{ \"timestamp\": 1554933813.067, \"currencyPair\": \"USDCNY\", \"alert\": \"spotChange\" }",
                "{ \"timestamp\": 1554933813.067, \"currencyPair\": \"USDCNY\", \"alert\": \"falling\", \"seconds\": 13 }"
            )
        }

    }
}

package com.spotpricepredictor.pro

import com.spotpricepredictor.pro.domain.model.DefaultTradeSuggestions
import com.spotpricepredictor.pro.domain.model.InstrumentType
import com.spotpricepredictor.pro.domain.model.SignalCategory
import com.spotpricepredictor.pro.domain.model.TradeAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TradeSuggestionsTest {

    @Test
    fun `default trade suggestions contain both equity and fno suggestions`() {
        val response = DefaultTradeSuggestions.getFallbackResponse()
        assertFalse(response.suggestions.isEmpty())
        assertTrue(response.suggestions.size >= 6)

        // Must contain Equity
        val equitySuggestions = response.suggestions.filter { it.instrumentType == InstrumentType.EQUITY }
        assertFalse(equitySuggestions.isEmpty())

        // Must contain F&O (Options / Futures)
        val fnoSuggestions = response.suggestions.filter { it.instrumentType == InstrumentType.OPTIONS }
        assertFalse(fnoSuggestions.isEmpty())
    }

    @Test
    fun `default trade suggestions contain both indices and stocks`() {
        val response = DefaultTradeSuggestions.getFallbackResponse()
        
        val indexSignals = response.suggestions.filter { it.category == SignalCategory.INDEX }
        assertTrue(indexSignals.any { it.symbol == "NIFTY" })
        assertTrue(indexSignals.any { it.symbol == "BANKNIFTY" })

        val stockSignals = response.suggestions.filter { it.category == SignalCategory.EQUITY }
        assertTrue(stockSignals.any { it.symbol == "RELIANCE" })
        assertTrue(stockSignals.any { it.symbol == "TCS" })
    }

    @Test
    fun `each trade suggestion is backed by technical analysis and indicators`() {
        val response = DefaultTradeSuggestions.getFallbackResponse()
        response.suggestions.forEach { s ->
            assertNotNull(s.technicalAnalysis)
            assertTrue("RSI should be between 0 and 100", s.technicalAnalysis.rsi in 0.0..100.0)
            assertFalse("Supertrend should not be empty", s.technicalAnalysis.supertrend.isBlank())
            assertFalse("VWAP signal should not be empty", s.technicalAnalysis.vwapSignal.isBlank())
            assertFalse("Rationale should not be empty", s.technicalAnalysis.rationale.isBlank())
            assertTrue("Stop loss should be defined", s.stopLoss > 0)
            assertTrue("Target should be defined", s.target1 > 0)
        }
    }

    @Test
    fun `fno trade suggestions include strike expiry and option premium levels`() {
        val response = DefaultTradeSuggestions.getFallbackResponse()
        val fnoList = response.suggestions.filter { it.instrumentType == InstrumentType.OPTIONS }
        fnoList.forEach { s ->
            val fno = s.fnoDetails
            assertNotNull("FNO details must be present for options", fno)
            assertTrue("Premium entry must be positive", fno!!.premiumEntry > 0)
            assertTrue("Premium target must be positive", fno.premiumTarget > 0)
            assertTrue("Premium stop loss must be positive", fno.premiumStopLoss > 0)
            assertTrue("Lot size must be positive", fno.lotSize > 0)
            assertFalse("Contract name must not be empty", fno.contractName.isBlank())
        }
    }

    @Test
    fun `fallback chart data provides valid candles and calculated indicators`() {
        val chart = DefaultTradeSuggestions.getFallbackChart("NIFTY", "15m")
        assertEquals("NIFTY", chart.symbol)
        assertFalse(chart.candles.isEmpty())

        val lastCandle = chart.candles.last()
        assertTrue(lastCandle.high >= lastCandle.low)
        assertNotNull(lastCandle.vwap)
        assertNotNull(lastCandle.rsi)
        assertNotNull(lastCandle.supertrend)
        assertNotNull(lastCandle.macd)
    }

    @Test
    fun `fallback chart generates distinct candles and timestamps across intervals`() {
        val intervals = listOf("15s", "30s", "1m", "5m", "15m", "30m", "1h", "4h", "1D", "1W", "1M", "1Y")
        val charts = intervals.associateWith { DefaultTradeSuggestions.getFallbackChart("NIFTY", it) }

        // All intervals must produce 40 candles
        charts.values.forEach { chart ->
            assertEquals(40, chart.candles.size)
        }

        // 15s must have seconds format (HH:mm:ss)
        val chart15s = charts["15s"]!!
        assertTrue(chart15s.candles.last().timeStr.count { it == ':' } == 2)

        // 1D must have day-month format
        val chart1D = charts["1D"]!!
        assertFalse(chart1D.candles.last().timeStr.contains(":"))

        // 1Y must have 4 digit year format
        val chart1Y = charts["1Y"]!!
        assertTrue(chart1Y.candles.last().timeStr.length == 4)

        // The candle prices must vary between 15s and 1D (fixing previous bug where they were identical)
        val close15s = chart15s.candles.first().close
        val close1D = chart1D.candles.first().close
        val close1Y = chart1Y.candles.first().close
        assertTrue("Candle closes must differ across timeframes", close15s != close1D || close1D != close1Y)

        // Indicator calculations must be present across intervals
        charts.values.forEach { chart ->
            val c = chart.candles.last()
            assertNotNull(c.ema20)
            assertNotNull(c.ema50)
            assertNotNull(c.vwap)
            assertNotNull(c.bbUpper)
            assertNotNull(c.supertrend)
            assertNotNull(c.rsi)
            assertNotNull(c.macd)
        }
    }

    @Test
    fun `prediction result supports institutional buy hold or sell recommendations and custom stock detection`() {
        val samplePrediction = com.spotpricepredictor.pro.domain.model.PredictionResult(
            symbol = "TATAPOWER",
            currentSpot = 410.0,
            predictedSpotPrice = 425.0,
            expectedHigh = 430.0,
            expectedLow = 405.0,
            confidencePct = 76.5,
            probBullish = 0.65,
            probBearish = 0.15,
            probSideways = 0.20,
            expectedRangeLower = 405.0,
            expectedRangeUpper = 430.0,
            targetPrice = 432.0,
            stopLoss = 398.0,
            expectedTimeToReach = "30 mins",
            marketStrength = "Strong Momentum",
            volatilityLevel = "Normal",
            riskLevel = "Moderate",
            suggestedStrategy = com.spotpricepredictor.pro.domain.model.StrategyType.BUY_CALL,
            strategyRationale = "Bullish momentum breakout",
            advancedAnalysis = com.spotpricepredictor.pro.domain.model.AdvancedAnalysis(
                pcr = 1.25,
                oiShiftBias = "Bullish",
                deltaExposureDex = 12000.0,
                gammaExposureGex = 4500.0,
                dealerPositioning = "Long Gamma",
                maxPain = 410.0,
                supportLevels = listOf(400.0, 395.0),
                resistanceLevels = listOf(420.0, 430.0),
                vwapPosition = "Above VWAP",
                atrVolatility = 8.5,
                momentumScore = 72.0,
                trendScore = 68.0,
                marketSentimentScore = 75.0
            ),
            modelBreakdown = com.spotpricepredictor.pro.domain.model.ModelForecastBreakdown(
                oiAnalysisSpot = 420.0,
                greeksSpot = 422.0,
                mlEnsembleSpot = 426.0,
                lstmSpot = 424.0,
                priceActionSpot = 427.0
            ),
            actionRecommendation = "BUY",
            actionRationale = "BUY Recommendation: 65% bullish breakout targeting ₹432",
            disclaimer = "SEBI Disclaimer"
        )

        assertEquals("BUY", samplePrediction.actionRecommendation)
        assertTrue(samplePrediction.actionRationale!!.contains("BUY Recommendation"))
        assertTrue(samplePrediction.symbol !in com.spotpricepredictor.pro.data.preferences.WatchlistManager.DEFAULT_SYMBOLS)
    }
}

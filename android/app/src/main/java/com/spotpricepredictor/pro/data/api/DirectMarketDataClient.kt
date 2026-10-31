package com.spotpricepredictor.pro.data.api

import com.spotpricepredictor.pro.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.sqrt

@Singleton
class DirectMarketDataClient @Inject constructor() {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    companion object {
        private val TICKER_MAP = mapOf(
            "NIFTY" to "^NSEI",
            "BANKNIFTY" to "^NSEBANK",
            "FINNIFTY" to "NIFTY_FIN_SERVICE.NS",
            "MIDCPNIFTY" to "^NSEMDCP50",
            "SENSEX" to "^BSESN",
            "BANKEX" to "BSE-BANK.BO",
            "NIFTYIT" to "^CNXIT",
            "NIFTYAUTO" to "^CNXAUTO",
            "NIFTYPHARMA" to "^CNXPHARMA",
            "NIFTYFMCG" to "^CNXFMCG",
            "NIFTYMETAL" to "^CNXMETAL",
            "NIFTYENERGY" to "^CNXENERGY",
            "NIFTYINFRA" to "^CNXINFRA",
            "NIFTYREALTY" to "^CNXREALTY",
            "CRUDEOIL" to "CL=F",
            "GOLD" to "GC=F",
            "SILVER" to "SI=F",
            "NATURALGAS" to "NG=F",
            "COPPER" to "HG=F",
            "ZINC" to "ZNC=F",
            "ALUMINIUM" to "ALI=F",
            "USDINR" to "INR=X",
            "EURINR" to "EURINR=X",
            "GBPINR" to "GBPINR=X",
            "JPYINR" to "JPYINR=X",
            "BTC" to "BTC-USD",
            "ETH" to "ETH-USD"
        )
    }

    private fun resolveTickers(symbol: String): List<String> {
        val clean = symbol.trim().uppercase().replace(" ", "").replace("-", "")
        val mapped = TICKER_MAP[clean]
        return if (mapped != null) {
            listOf(mapped)
        } else {
            listOf("$clean.NS", "$clean.BO")
        }
    }

    suspend fun fetchDirectQuote(symbol: String): LiveQuote? = withContext(Dispatchers.IO) {
        val sym = symbol.trim().uppercase()
        val candidates = resolveTickers(sym)
        val mStatus = MarketStatusHelper.getMarketStatus(sym)

        for (tick in candidates) {
            val url = "https://query1.finance.yahoo.com/v8/finance/chart/$tick?interval=1d&range=1d"
            try {
                val req = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build()

                val response = httpClient.newCall(req).execute()
                response.use { resp ->
                    if (!resp.isSuccessful) return@use
                    val bodyStr = resp.body?.string() ?: return@use
                    val root = JSONObject(bodyStr)
                    val chartObj = root.optJSONObject("chart") ?: return@use
                    val resArr = chartObj.optJSONArray("result") ?: return@use
                    if (resArr.length() == 0) return@use

                    val resultObj = resArr.getJSONObject(0)
                    val meta = resultObj.optJSONObject("meta") ?: return@use

                    val p = if (meta.has("regularMarketPrice") && !meta.isNull("regularMarketPrice")) {
                        meta.getDouble("regularMarketPrice")
                    } else if (meta.has("chartPreviousClose") && !meta.isNull("chartPreviousClose")) {
                        meta.getDouble("chartPreviousClose")
                    } else {
                        0.0
                    }

                    val pc = if (meta.has("chartPreviousClose") && !meta.isNull("chartPreviousClose")) {
                        meta.getDouble("chartPreviousClose")
                    } else {
                        p
                    }

                    if (p > 0.0) {
                        val liveP = Math.round(p * 100.0) / 100.0
                        val prevC = Math.round(pc * 100.0) / 100.0
                        val chg = Math.round((liveP - prevC) * 100.0) / 100.0
                        val pct = if (prevC > 0.0) Math.round(((chg / prevC) * 100.0) * 100.0) / 100.0 else 0.0

                        return@withContext LiveQuote(
                            symbol = sym,
                            spotPrice = liveP,
                            previousClose = prevC,
                            change = chg,
                            changePercent = pct,
                            isRealtime = mStatus.isOpen,
                            isMarketOpen = mStatus.isOpen,
                            marketStatus = mStatus.statusText,
                            sessionLabel = mStatus.sessionLabel,
                            lastCloseTime = mStatus.lastCloseTime,
                            nextSession = mStatus.nextSession
                        )
                    }
                }
            } catch (e: Exception) {
                // Try next candidate ticker
            }
        }
        null
    }

    suspend fun fetchDirectChart(symbol: String, interval: String, limit: Int = 60): ChartData? = withContext(Dispatchers.IO) {
        val sym = symbol.trim().uppercase()
        val candidates = resolveTickers(sym)
        val mStatus = MarketStatusHelper.getMarketStatus(sym)

        val (yInterval, yRange, timeFmt) = when (interval.lowercase()) {
            "1m" -> Triple("1m", "1d", "HH:mm")
            "5m" -> Triple("5m", "1d", "HH:mm")
            "15m" -> Triple("15m", "5d", "HH:mm")
            "30m" -> Triple("30m", "5d", "HH:mm")
            "1h" -> Triple("60m", "1mo", "dd MMM HH:mm")
            "4h" -> Triple("60m", "3mo", "dd MMM HH:mm")
            "1d" -> Triple("1d", "6mo", "dd MMM")
            "1w" -> Triple("1wk", "2y", "dd MMM yy")
            "1M", "1mo" -> Triple("1mo", "5y", "MMM yyyy")
            else -> Triple("15m", "5d", "HH:mm")
        }

        for (tick in candidates) {
            val url = "https://query1.finance.yahoo.com/v8/finance/chart/$tick?interval=$yInterval&range=$yRange"
            try {
                val req = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build()

                val response = httpClient.newCall(req).execute()
                response.use { resp ->
                    if (!resp.isSuccessful) return@use
                    val bodyStr = resp.body?.string() ?: return@use
                    val root = JSONObject(bodyStr)
                    val chartObj = root.optJSONObject("chart") ?: return@use
                    val resArr = chartObj.optJSONArray("result") ?: return@use
                    if (resArr.length() == 0) return@use

                    val resultObj = resArr.getJSONObject(0)
                    val timestamps = resultObj.optJSONArray("timestamp") ?: return@use
                    val indicators = resultObj.optJSONObject("indicators") ?: return@use
                    val quoteArr = indicators.optJSONArray("quote") ?: return@use
                    if (quoteArr.length() == 0) return@use

                    val quoteObj = quoteArr.getJSONObject(0)
                    val opens = quoteObj.optJSONArray("open")
                    val highs = quoteObj.optJSONArray("high")
                    val lows = quoteObj.optJSONArray("low")
                    val closes = quoteObj.optJSONArray("close")
                    val volumes = quoteObj.optJSONArray("volume")

                    val candleCount = timestamps.length()
                    val rawCandles = mutableListOf<CandleBar>()
                    val dateFormat = SimpleDateFormat(timeFmt, Locale.ENGLISH).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Kolkata")
                    }

                    for (i in 0 until candleCount) {
                        if (opens == null || highs == null || lows == null || closes == null) continue
                        if (opens.isNull(i) || highs.isNull(i) || lows.isNull(i) || closes.isNull(i)) continue

                        val o = opens.optDouble(i)
                        val h = highs.optDouble(i)
                        val l = lows.optDouble(i)
                        val c = closes.optDouble(i)
                        val v = if (volumes != null && !volumes.isNull(i)) volumes.optLong(i) else 10000L
                        val tsSec = timestamps.optLong(i)

                        if (!o.isNaN() && !h.isNaN() && !l.isNaN() && !c.isNaN() && tsSec > 0) {
                            val tsMillis: Long = tsSec * 1000L
                            val tStr = dateFormat.format(Date(tsMillis))
                            rawCandles.add(
                                CandleBar(
                                    timestamp = tsMillis,
                                    timeStr = tStr,
                                    open = Math.round(o * 100.0) / 100.0,
                                    high = Math.round(h * 100.0) / 100.0,
                                    low = Math.round(l * 100.0) / 100.0,
                                    close = Math.round(c * 100.0) / 100.0,
                                    volume = v
                                )
                            )
                        }
                    }

                    if (rawCandles.size >= 10) {
                        val candlesWithIndicators = computeTechnicalIndicators(rawCandles)
                        val finalCandles = candlesWithIndicators.takeLast(limit)
                        val latestCandle = finalCandles.last()
                        val firstCandle = finalCandles.first()

                        val spot = latestCandle.close
                        val change = Math.round((spot - firstCandle.open) * 100.0) / 100.0
                        val changePct = if (firstCandle.open > 0) Math.round(((change / firstCandle.open) * 100.0) * 100.0) / 100.0 else 0.0

                        val (call, score, tgt, sl, sigs) = evaluateTechnicalSignals(finalCandles, spot)

                        return@withContext ChartData(
                            symbol = sym,
                            interval = interval,
                            spotPrice = spot,
                            change = change,
                            changePct = changePct,
                            candles = finalCandles,
                            technicalCall = call,
                            technicalScore = score,
                            targetPrice = tgt,
                            stopLoss = sl,
                            strategySignals = sigs,
                            isMarketOpen = mStatus.isOpen,
                            marketStatus = mStatus.statusText,
                            sessionLabel = mStatus.sessionLabel,
                            lastCloseTime = mStatus.lastCloseTime
                        )
                    }
                }
            } catch (e: Exception) {
                // Try next ticker
            }
        }
        null
    }

    private fun computeTechnicalIndicators(candles: List<CandleBar>): List<CandleBar> {
        val n = candles.size
        val closes = candles.map { it.close }
        val highs = candles.map { it.high }
        val lows = candles.map { it.low }
        val volumes = candles.map { it.volume }

        // EMA 20 & 50
        val ema20 = calculateEma(closes, 20)
        val ema50 = calculateEma(closes, 50)
        val sma200 = calculateSma(closes, 200)

        // VWAP
        val vwap = mutableListOf<Double>()
        var cumVol = 0.0
        var cumVolPrice = 0.0
        for (i in 0 until n) {
            val typical = (highs[i] + lows[i] + closes[i]) / 3.0
            val vol = volumes[i].toDouble().coerceAtLeast(1.0)
            cumVolPrice += typical * vol
            cumVol += vol
            vwap.add(Math.round((cumVolPrice / cumVol) * 100.0) / 100.0)
        }

        // Bollinger Bands (20, 2)
        val bbUpper = mutableListOf<Double?>()
        val bbMid = mutableListOf<Double?>()
        val bbLower = mutableListOf<Double?>()
        for (i in 0 until n) {
            if (i < 19) {
                bbUpper.add(null)
                bbMid.add(null)
                bbLower.add(null)
            } else {
                val window = closes.subList(i - 19, i + 1)
                val mean = window.average()
                val variance = window.map { (it - mean) * (it - mean) }.average()
                val std = sqrt(variance)
                bbMid.add(Math.round(mean * 100.0) / 100.0)
                bbUpper.add(Math.round((mean + 2.0 * std) * 100.0) / 100.0)
                bbLower.add(Math.round((mean - 2.0 * std) * 100.0) / 100.0)
            }
        }

        // Supertrend (10, 3)
        val stValues = mutableListOf<Double?>()
        val stDirs = mutableListOf<String?>()
        val atr = mutableListOf<Double>()
        for (i in 0 until n) {
            if (i == 0) {
                atr.add(highs[0] - lows[0])
            } else {
                val tr1 = highs[i] - lows[i]
                val tr2 = abs(highs[i] - closes[i - 1])
                val tr3 = abs(lows[i] - closes[i - 1])
                val tr = maxOf(tr1, maxOf(tr2, tr3))
                val prevAtr = atr[i - 1]
                atr.add((prevAtr * 9.0 + tr) / 10.0)
            }
        }

        var curDir = "UP"
        var curVal = closes[0]
        for (i in 0 until n) {
            val basicUpper = (highs[i] + lows[i]) / 2.0 + (3.0 * atr[i])
            val basicLower = (highs[i] + lows[i]) / 2.0 - (3.0 * atr[i])
            if (closes[i] > basicUpper) {
                curDir = "UP"
                curVal = basicLower
            } else if (closes[i] < basicLower) {
                curDir = "DOWN"
                curVal = basicUpper
            }
            stValues.add(Math.round(curVal * 100.0) / 100.0)
            stDirs.add(curDir)
        }

        // RSI (14)
        val rsiList = mutableListOf<Double?>()
        var avgGain = 0.0
        var avgLoss = 0.0
        for (i in 0 until n) {
            if (i == 0) {
                rsiList.add(null)
            } else if (i < 14) {
                val diff = closes[i] - closes[i - 1]
                if (diff > 0) avgGain += diff else avgLoss += -diff
                rsiList.add(null)
            } else if (i == 14) {
                avgGain /= 14.0
                avgLoss /= 14.0
                val rs = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
                rsiList.add(Math.round((100.0 - (100.0 / (1.0 + rs))) * 10.0) / 10.0)
            } else {
                val diff = closes[i] - closes[i - 1]
                val gain = if (diff > 0) diff else 0.0
                val loss = if (diff < 0) -diff else 0.0
                avgGain = (avgGain * 13.0 + gain) / 14.0
                avgLoss = (avgLoss * 13.0 + loss) / 14.0
                val rs = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
                rsiList.add(Math.round((100.0 - (100.0 / (1.0 + rs))) * 10.0) / 10.0)
            }
        }

        // MACD (12, 26, 9)
        val ema12 = calculateEma(closes, 12)
        val ema26 = calculateEma(closes, 26)
        val macdLine = mutableListOf<Double>()
        for (i in 0 until n) {
            val e12 = ema12[i] ?: closes[i]
            val e26 = ema26[i] ?: closes[i]
            macdLine.add(e12 - e26)
        }
        val macdSignalLine = calculateEma(macdLine, 9)

        return candles.mapIndexed { i, c ->
            val mVal = Math.round(macdLine[i] * 100.0) / 100.0
            val sVal = macdSignalLine[i]?.let { Math.round(it * 100.0) / 100.0 } ?: mVal
            val hVal = Math.round((mVal - sVal) * 100.0) / 100.0
            c.copy(
                ema20 = ema20[i]?.let { Math.round(it * 100.0) / 100.0 },
                ema50 = ema50[i]?.let { Math.round(it * 100.0) / 100.0 },
                sma200 = sma200[i]?.let { Math.round(it * 100.0) / 100.0 },
                vwap = vwap[i],
                bbUpper = bbUpper[i],
                bbMiddle = bbMid[i],
                bbLower = bbLower[i],
                supertrend = stValues[i],
                supertrendDirection = stDirs[i],
                rsi = rsiList[i],
                macd = mVal,
                macdSignal = sVal,
                macdHist = hVal
            )
        }
    }

    private fun calculateEma(data: List<Double>, period: Int): List<Double?> {
        val n = data.size
        val result = mutableListOf<Double?>()
        val k = 2.0 / (period + 1.0)
        var prevEma: Double? = null

        for (i in 0 until n) {
            if (i < period - 1) {
                result.add(null)
            } else if (i == period - 1) {
                val sma = data.subList(0, period).average()
                prevEma = sma
                result.add(sma)
            } else {
                val currentEma = (data[i] * k) + (prevEma!! * (1.0 - k))
                prevEma = currentEma
                result.add(currentEma)
            }
        }
        return result
    }

    private fun calculateSma(data: List<Double>, period: Int): List<Double?> {
        val n = data.size
        val result = mutableListOf<Double?>()
        for (i in 0 until n) {
            if (i < period - 1) {
                result.add(null)
            } else {
                result.add(data.subList(i - period + 1, i + 1).average())
            }
        }
        return result
    }

    private data class SignalEvalResult(
        val call: String,
        val score: Double,
        val target: Double,
        val stopLoss: Double,
        val signals: List<StrategySignal>
    )

    private fun evaluateTechnicalSignals(candles: List<CandleBar>, spot: Double): SignalEvalResult {
        val last = candles.last()
        var bullishVotes = 0
        var bearishVotes = 0
        val signals = mutableListOf<StrategySignal>()

        // 1. EMA 20/50 Trend Alignment
        val ema20 = last.ema20 ?: spot
        val ema50 = last.ema50 ?: spot
        if (spot >= ema20 && ema20 >= ema50) {
            bullishVotes += 2
            signals.add(StrategySignal("EMA 20/50 Golden Trend", "BUY", 88.0, "Spot trading above 20 EMA and 50 EMA indicating clean bullish market structure."))
        } else if (spot <= ema20 && ema20 <= ema50) {
            bearishVotes += 2
            signals.add(StrategySignal("EMA 20/50 Death Cross", "SELL", 85.0, "Spot trading below 20 EMA and 50 EMA signaling confirmed bearish momentum."))
        } else {
            signals.add(StrategySignal("EMA 20/50 Neutral", "NEUTRAL", 50.0, "Moving averages converging in consolidation range."))
        }

        // 2. Supertrend (10, 3)
        if (last.supertrendDirection == "UP") {
            bullishVotes += 2
            signals.add(StrategySignal("Supertrend (10, 3)", "BUY", 85.0, "Green Supertrend support protecting current price structure."))
        } else {
            bearishVotes += 2
            signals.add(StrategySignal("Supertrend (10, 3)", "SELL", 85.0, "Red Supertrend resistance overhead capping upside rallies."))
        }

        // 3. RSI (14) Momentum
        val rsi = last.rsi ?: 50.0
        if (rsi > 58.0) {
            bullishVotes += 1
            signals.add(StrategySignal("RSI (14) Momentum", "BUY", 75.0, "RSI in bullish expansion zone at ${"%.1f".format(rsi)}."))
        } else if (rsi < 42.0) {
            bearishVotes += 1
            signals.add(StrategySignal("RSI (14) Momentum", "SELL", 75.0, "RSI in oversold breakdown territory at ${"%.1f".format(rsi)}."))
        } else {
            signals.add(StrategySignal("RSI (14) Momentum", "NEUTRAL", 50.0, "RSI balanced in neutral equilibrium zone (${"%.1f".format(rsi)})."))
        }

        // 4. MACD Histogram
        val macdHist = last.macdHist ?: 0.0
        if (macdHist > 0) {
            bullishVotes += 1
            signals.add(StrategySignal("MACD Divergence", "BUY", 80.0, "MACD positive histogram expanding above signal line."))
        } else {
            bearishVotes += 1
            signals.add(StrategySignal("MACD Divergence", "SELL", 80.0, "MACD negative histogram contracting beneath signal line."))
        }

        val totalVotes = bullishVotes + bearishVotes
        val netBullishRatio = if (totalVotes > 0) bullishVotes.toDouble() / totalVotes.toDouble() else 0.5
        val score = Math.round((netBullishRatio * 200.0 - 100.0) * 10.0) / 10.0 // -100 to +100

        val call = when {
            score >= 45.0 -> "STRONG BUY"
            score >= 15.0 -> "BUY"
            score <= -45.0 -> "STRONG SELL"
            score <= -15.0 -> "SELL"
            else -> "HOLD"
        }

        val step = when {
            spot > 40000 -> 150.0
            spot > 10000 -> 60.0
            spot > 3000 -> 25.0
            spot > 1000 -> 15.0
            spot > 300 -> 5.0
            else -> 1.5
        }

        val isBullish = call.contains("BUY")
        val tgt = if (isBullish) Math.round((spot + (step * 2.5)) * 100.0) / 100.0 else Math.round((spot - (step * 2.5)) * 100.0) / 100.0
        val sl = if (isBullish) Math.round((spot - (step * 1.2)) * 100.0) / 100.0 else Math.round((spot + (step * 1.2)) * 100.0) / 100.0

        return SignalEvalResult(call, score, tgt, sl, signals)
    }
}

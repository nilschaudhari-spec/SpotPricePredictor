package com.spotpricepredictor.pro.data.repository

import com.spotpricepredictor.pro.data.api.PredictorApiService
import com.spotpricepredictor.pro.data.api.models.PredictionRequestDto
import com.spotpricepredictor.pro.data.local.dao.PredictionHistoryDao
import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity
import com.spotpricepredictor.pro.domain.model.*
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepositoryImpl @Inject constructor(
    private val apiService: PredictorApiService,
    private val historyDao: PredictionHistoryDao
) : MarketRepository {

    override suspend fun getOptionChain(
        symbol: String,
        daysToExpiry: Double,
        customSpot: Double?,
        expiryDate: String?
    ): Result<OptionChainData> {
        return try {
            val response = apiService.getOptionChain(
                symbol = symbol,
                daysToExpiry = daysToExpiry,
                customSpot = customSpot,
                expiryDate = expiryDate
            )
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val strikes = dto.strikes.map { s ->
                    StrikeRowData(
                        strikePrice = s.strikePrice,
                        call = OptionLeg(
                            ltp = s.call.ltp,
                            volume = s.call.volume,
                            oi = s.call.oi,
                            changeInOi = s.call.changeInOi,
                            iv = s.call.iv,
                            greeks = GreekMetrics(
                                delta = s.call.greeks.delta,
                                gamma = s.call.greeks.gamma,
                                theta = s.call.greeks.theta,
                                vega = s.call.greeks.vega,
                                rho = s.call.greeks.rho
                            ),
                            buildup = s.call.buildup
                        ),
                        put = OptionLeg(
                            ltp = s.put.ltp,
                            volume = s.put.volume,
                            oi = s.put.oi,
                            changeInOi = s.put.changeInOi,
                            iv = s.put.iv,
                            greeks = GreekMetrics(
                                delta = s.put.greeks.delta,
                                gamma = s.put.greeks.gamma,
                                theta = s.put.greeks.theta,
                                vega = s.put.greeks.vega,
                                rho = s.put.greeks.rho
                            ),
                            buildup = s.put.buildup
                        )
                    )
                }

                val expiries = dto.availableExpiries.map {
                    ExpiryInfo(date = it.date, daysToExpiry = it.daysToExpiry, type = it.type)
                }

                Result.success(
                    OptionChainData(
                        symbol = dto.symbol,
                        spotPrice = dto.spotPrice,
                        pcr = dto.pcr,
                        maxPain = dto.maxPain,
                        immediateSupport = dto.immediateSupport,
                        immediateResistance = dto.immediateResistance,
                        strikes = strikes,
                        selectedDaysToExpiry = dto.selectedDaysToExpiry,
                        selectedExpiryDate = dto.selectedExpiryDate,
                        availableExpiries = expiries
                    )
                )
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRealtimeQuote(symbol: String): Result<LiveQuote> {
        return try {
            val response = apiService.getRealtimeQuote(symbol)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    LiveQuote(
                        symbol = dto.symbol,
                        spotPrice = dto.spotPrice,
                        previousClose = dto.previousClose,
                        change = dto.change,
                        changePercent = dto.changePercent,
                        isRealtime = dto.isRealtime,
                        step = dto.step,
                        lotSize = dto.lotSize
                    )
                )
            } else {
                Result.failure(Exception("Failed to fetch live quote: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun predictSpotPrice(
        symbol: String,
        currentSpot: Double,
        horizon: PredictionHorizon,
        customMinutes: Int?,
        trendMode: TrendDirection
    ): Result<PredictionResult> {
        return try {
            val requestDto = PredictionRequestDto(
                symbol = symbol,
                currentSpotPrice = currentSpot,
                predictionHorizon = horizon.name,
                customMinutes = customMinutes,
                trendMode = trendMode.name
            )

            val response = apiService.predictSpotPrice(requestDto)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val strategy = when (dto.suggestedStrategy.uppercase()) {
                    "BUY CALL" -> StrategyType.BUY_CALL
                    "BUY PUT" -> StrategyType.BUY_PUT
                    "CALL SPREAD" -> StrategyType.CALL_SPREAD
                    "PUT SPREAD" -> StrategyType.PUT_SPREAD
                    "IRON CONDOR" -> StrategyType.IRON_CONDOR
                    "STRADDLE" -> StrategyType.STRADDLE
                    "WAIT" -> StrategyType.WAIT
                    else -> StrategyType.NO_TRADE
                }

                val result = PredictionResult(
                    symbol = dto.symbol,
                    currentSpot = dto.currentSpot,
                    predictedSpotPrice = dto.predictedSpotPrice,
                    expectedHigh = dto.expectedHigh,
                    expectedLow = dto.expectedLow,
                    confidencePct = dto.confidencePct,
                    probBullish = dto.probBullish,
                    probBearish = dto.probBearish,
                    probSideways = dto.probSideways,
                    expectedRangeLower = dto.expectedRangeLower,
                    expectedRangeUpper = dto.expectedRangeUpper,
                    targetPrice = dto.targetPrice,
                    stopLoss = dto.stopLoss,
                    expectedTimeToReach = dto.expectedTimeToReach,
                    marketStrength = dto.marketStrength,
                    volatilityLevel = dto.volatilityLevel,
                    riskLevel = dto.riskLevel,
                    suggestedStrategy = strategy,
                    strategyRationale = dto.strategyRationale,
                    advancedAnalysis = AdvancedAnalysis(
                        pcr = dto.advancedAnalysis.pcr,
                        oiShiftBias = dto.advancedAnalysis.oiShiftBias,
                        deltaExposureDex = dto.advancedAnalysis.deltaExposureDex,
                        gammaExposureGex = dto.advancedAnalysis.gammaExposureGex,
                        dealerPositioning = dto.advancedAnalysis.dealerPositioning,
                        maxPain = dto.advancedAnalysis.maxPain,
                        supportLevels = dto.advancedAnalysis.supportLevels,
                        resistanceLevels = dto.advancedAnalysis.resistanceLevels,
                        vwapPosition = dto.advancedAnalysis.vwapPosition,
                        atrVolatility = dto.advancedAnalysis.atrVolatility,
                        momentumScore = dto.advancedAnalysis.momentumScore,
                        trendScore = dto.advancedAnalysis.trendScore,
                        marketSentimentScore = dto.advancedAnalysis.marketSentimentScore
                    ),
                    modelBreakdown = ModelForecastBreakdown(
                        oiAnalysisSpot = dto.modelBreakdown.oiAnalysisSpot,
                        greeksSpot = dto.modelBreakdown.greeksSpot,
                        mlEnsembleSpot = dto.modelBreakdown.mlEnsembleSpot,
                        lstmSpot = dto.modelBreakdown.lstmSpot,
                        priceActionSpot = dto.modelBreakdown.priceActionSpot
                    ),
                    disclaimer = dto.disclaimer
                )

                Result.success(result)
            } else {
                Result.failure(Exception("Prediction API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPredictionHistory(): Flow<List<PredictionHistoryEntity>> {
        return historyDao.getAllPredictionsFlow()
    }

    override suspend fun savePrediction(prediction: PredictionResult, horizonLabel: String): Long {
        val entity = PredictionHistoryEntity(
            symbol = prediction.symbol,
            timestamp = System.currentTimeMillis(),
            horizon = horizonLabel,
            currentSpot = prediction.currentSpot,
            predictedSpot = prediction.predictedSpotPrice,
            targetPrice = prediction.targetPrice,
            stopLoss = prediction.stopLoss,
            confidencePct = prediction.confidencePct,
            suggestedStrategy = prediction.suggestedStrategy.label,
            probBullish = prediction.probBullish,
            probBearish = prediction.probBearish,
            status = "PENDING"
        )
        return historyDao.insertPrediction(entity)
    }

    override suspend fun recordActualOutcome(id: Long, actualSpot: Double, status: String) {
        historyDao.recordActualOutcome(id, actualSpot, status)
    }
}

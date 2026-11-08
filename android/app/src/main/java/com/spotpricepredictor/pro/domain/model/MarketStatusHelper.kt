package com.spotpricepredictor.pro.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class MarketStatusInfo(
    val isOpen: Boolean,
    val statusText: String, // "MARKET OPEN", "MARKET CLOSED", "PRE-OPEN"
    val sessionLabel: String, // "Live Regular Trading", "Official Market Close"
    val nextSession: String, // "Next: Mon 09:15 AM IST"
    val lastCloseTime: String // "31-Oct-2026 03:30 PM IST"
)

object MarketStatusHelper {

    private val IST_TIMEZONE: TimeZone = TimeZone.getTimeZone("Asia/Kolkata")

    fun getMarketStatus(symbol: String = "NIFTY"): MarketStatusInfo {
        val sym = symbol.uppercase().trim()

        // 1. 24/7 Global Crypto (BTC, ETH)
        if (sym == "BTC" || sym == "ETH") {
            val nowFmt = SimpleDateFormat("dd-MMM-yyyy hh:mm a 'IST'", Locale.ENGLISH).apply {
                timeZone = IST_TIMEZONE
            }
            return MarketStatusInfo(
                isOpen = true,
                statusText = "MARKET OPEN",
                sessionLabel = "24/7 Global Crypto Session",
                nextSession = "Continuous 24/7 Trading",
                lastCloseTime = nowFmt.format(Date())
            )
        }

        val cal = Calendar.getInstance(IST_TIMEZONE)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon, ..., 7 = Sat
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val minuteOfDay = hour * 60 + minute

        val isCommodity = sym in listOf("CRUDEOIL", "GOLD", "SILVER", "NATURALGAS", "COPPER", "ZINC", "ALUMINIUM")
        val isWeekday = dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY

        val nowFmt = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).apply {
            timeZone = IST_TIMEZONE
        }
        val datePrefix = nowFmt.format(cal.time)

        if (isCommodity) {
            // MCX hours: Mon-Fri 09:00 to 23:30
            if (isWeekday && minuteOfDay in (9 * 60) until (23 * 60 + 30)) {
                return MarketStatusInfo(
                    isOpen = true,
                    statusText = "MARKET OPEN",
                    sessionLabel = "Live MCX Commodity Session",
                    nextSession = "Closes today at 11:30 PM IST",
                    lastCloseTime = "$datePrefix 11:30 PM IST"
                )
            }
            val nextOpen = if (dayOfWeek == Calendar.FRIDAY && minuteOfDay >= 23 * 60 + 30 || dayOfWeek in listOf(Calendar.SATURDAY, Calendar.SUNDAY)) {
                "Mon 09:00 AM IST"
            } else {
                "Tomorrow 09:00 AM IST"
            }
            return MarketStatusInfo(
                isOpen = false,
                statusText = "MARKET CLOSED",
                sessionLabel = "Official MCX Commodity Close",
                nextSession = "Next: $nextOpen",
                lastCloseTime = "$datePrefix 11:30 PM IST"
            )
        }

        // Standard NSE / BSE Equities and Indices
        val marketOpen = 9 * 60 + 15   // 09:15 AM IST
        val marketClose = 15 * 60 + 30 // 03:30 PM IST
        val preOpen = 9 * 60           // 09:00 AM IST

        if (isWeekday) {
            if (minuteOfDay in marketOpen until marketClose) {
                return MarketStatusInfo(
                    isOpen = true,
                    statusText = "MARKET OPEN",
                    sessionLabel = "Live Regular Trading Session",
                    nextSession = "Closes today at 03:30 PM IST",
                    lastCloseTime = "$datePrefix 03:30 PM IST"
                )
            } else if (minuteOfDay in preOpen until marketOpen) {
                return MarketStatusInfo(
                    isOpen = false,
                    statusText = "PRE-OPEN",
                    sessionLabel = "Pre-Market Discovery Session",
                    nextSession = "Opens at 09:15 AM IST",
                    lastCloseTime = "$datePrefix 09:00 AM IST"
                )
            }
        }

        // Market is Closed
        val nextOpen = when {
            dayOfWeek == Calendar.FRIDAY && minuteOfDay >= marketClose -> "Mon 09:15 AM IST"
            dayOfWeek == Calendar.SATURDAY -> "Mon 09:15 AM IST"
            dayOfWeek == Calendar.SUNDAY -> "Mon 09:15 AM IST"
            minuteOfDay >= marketClose -> "Tomorrow 09:15 AM IST"
            else -> "Today 09:15 AM IST"
        }

        return MarketStatusInfo(
            isOpen = false,
            statusText = "MARKET CLOSED",
            sessionLabel = "Official Market Closing Session",
            nextSession = "Next: $nextOpen",
            lastCloseTime = "$datePrefix 03:30 PM IST"
        )
    }
}


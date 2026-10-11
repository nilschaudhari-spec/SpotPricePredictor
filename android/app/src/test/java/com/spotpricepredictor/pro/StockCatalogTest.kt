package com.spotpricepredictor.pro

import com.spotpricepredictor.pro.domain.model.StockCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StockCatalogTest {

    @Test
    fun `query shorter than 3 characters returns empty list`() {
        assertTrue(StockCatalog.search("").isEmpty())
        assertTrue(StockCatalog.search("T").isEmpty())
        assertTrue(StockCatalog.search("TA").isEmpty())
        assertTrue(StockCatalog.search("  t ").isEmpty())
    }

    @Test
    fun `typing initial 3 letters of Tata returns Tata group stocks`() {
        val results = StockCatalog.search("TAT")
        assertFalse(results.isEmpty())
        val symbols = results.map { it.symbol }
        assertTrue(symbols.contains("TATAMOTORS"))
        assertTrue(symbols.contains("TCS"))
        assertTrue(symbols.contains("TATASTEEL"))
    }

    @Test
    fun `typing lowercase tat performs case-insensitive autocomplete`() {
        val uppercaseResults = StockCatalog.search("TAT")
        val lowercaseResults = StockCatalog.search("tat")
        assertEquals(uppercaseResults.map { it.symbol }, lowercaseResults.map { it.symbol })
    }

    @Test
    fun `typing initial 3 letters of Reliance returns RELIANCE`() {
        val results = StockCatalog.search("REL")
        assertFalse(results.isEmpty())
        assertTrue(results.any { it.symbol == "RELIANCE" && it.name.contains("Reliance", ignoreCase = true) })
    }

    @Test
    fun `typing initial 3 letters of State Bank of India returns SBIN`() {
        val resultsByTicker = StockCatalog.search("SBI")
        assertTrue(resultsByTicker.any { it.symbol == "SBIN" })

        val resultsByName = StockCatalog.search("STA")
        assertTrue(resultsByName.any { it.symbol == "SBIN" && it.name.contains("State Bank", ignoreCase = true) })
    }

    @Test
    fun `typing initial 3 letters of HDFC returns HDFCBANK`() {
        val results = StockCatalog.search("HDF")
        assertFalse(results.isEmpty())
        assertTrue(results.any { it.symbol == "HDFCBANK" })
    }

    @Test
    fun `typing initial 3 letters of Infosys returns INFY`() {
        val results = StockCatalog.search("INF")
        assertFalse(results.isEmpty())
        assertTrue(results.any { it.symbol == "INFY" })
    }

    @Test
    fun `typing BAN returns Bank Nifty and bank stocks`() {
        val results = StockCatalog.search("BAN")
        val symbols = results.map { it.symbol }
        assertTrue(symbols.contains("BANKNIFTY") || symbols.contains("BANKBARODA") || symbols.contains("HDFCBANK"))
    }

    @Test
    fun `typing non-existent ticker returns empty list`() {
        val results = StockCatalog.search("ZZZ999")
        assertTrue(results.isEmpty())
    }
}

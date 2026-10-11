package com.spotpricepredictor.pro

import com.spotpricepredictor.pro.domain.model.StockCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StockCatalogTest {

    @Test
    fun `empty query returns empty list`() {
        assertTrue(StockCatalog.search("").isEmpty())
        assertTrue(StockCatalog.search("   ").isEmpty())
    }

    @Test
    fun `searching 2 letters LT returns Larsen and Toubro`() {
        val results = StockCatalog.search("LT")
        assertFalse(results.isEmpty())
        val symbols = results.map { it.symbol }
        assertTrue(symbols.contains("LT"))
    }

    @Test
    fun `searching 1 letter T returns Tata stocks`() {
        val results = StockCatalog.search("T")
        assertFalse(results.isEmpty())
        val symbols = results.map { it.symbol }
        assertTrue(symbols.contains("TATAMOTORS") || symbols.contains("TCS"))
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
    fun `searching any custom ticker outside catalog provides selectable dynamic stock item`() {
        val results = StockCatalog.search("CUSTOMCO")
        assertFalse(results.isEmpty())
        assertTrue(results.any { it.symbol == "CUSTOMCO" })
    }
}

package com.spotpricepredictor.pro.domain.model

data class StockItem(
    val symbol: String,
    val name: String,
    val sector: String,
    val exchange: String = "NSE"
)

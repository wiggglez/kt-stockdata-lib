package com.wiggglez.kt_stockdata_lib.marketdata.dataobjects

data class StockCompetitor(
    val ticker: String,
    val companyName: String,
    val pctChange: Double,
    val marketCap: Long,
    val marketCapAbbreviatedString: String
)
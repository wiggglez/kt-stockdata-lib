package com.wiggglez.kt_stockdata_lib.watchlistsdata.dataobjects

data class Watchlist(
    val name: String,
    val tickers: List<Ticker>
)

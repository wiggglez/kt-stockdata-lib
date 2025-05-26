package com.wiggglez.kt_stockdata_lib

import com.wiggglez.kt_stockdata_lib.historicchartdata.HistoricChartData
import com.wiggglez.kt_stockdata_lib.marketdata.MarketData
import com.wiggglez.kt_stockdata_lib.watchlistsdata.PopularWatchlistDataImpl

object KtStocks {
    val historicChartData = HistoricChartData()
    val marketData = MarketData()
    val watchlistData = PopularWatchlistDataImpl()
}

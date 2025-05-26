package com.wiggglez.kt_stockdata_lib

fun main() {
//    Chart data example
//    val kt = KtStocks.historicChartData.getHistoryAsSimpleStockChart("SPY")
//    println(kt!!.open)

    val kt = KtStocks

    val x = kt.marketData.getSnrLevels("SPY")
    println(x)
}
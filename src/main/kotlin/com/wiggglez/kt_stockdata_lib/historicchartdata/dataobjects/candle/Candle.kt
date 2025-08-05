package com.wiggglez.kt_stockdata_lib.historicchartdata.dataobjects.candle

import java.util.*

data class Candle(
    val datetime: Date,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)
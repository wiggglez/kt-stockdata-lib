package com.wiggglez.kt_stockdata_lib.historicchartdata.dataobjects.yfhistoryresp


import com.google.gson.annotations.SerializedName

internal data class Result(
    @SerializedName("indicators")
    val indicators: Indicators,
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("timestamp")
    val timestamp: List<Int>
)
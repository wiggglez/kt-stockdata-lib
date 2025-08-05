package com.wiggglez.kt_stockdata_lib.historicchartdata.dataobjects.yfhistoryresp


import com.google.gson.annotations.SerializedName

internal data class Quote(
    @SerializedName("close")
    val close: List<Double?>,
    @SerializedName("high")
    val high: List<Double?>,
    @SerializedName("low")
    val low: List<Double?>,
    @SerializedName("open")
    val `open`: List<Double?>,
    @SerializedName("volume")
    val volume: List<Long?>
)
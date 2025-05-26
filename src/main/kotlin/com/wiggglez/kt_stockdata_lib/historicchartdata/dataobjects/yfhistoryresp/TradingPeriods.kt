package com.wiggglez.kt_stockdata_lib.historicchartdata.dataobjects.yfhistoryresp


import com.google.gson.annotations.SerializedName

internal data class TradingPeriods(
    @SerializedName("post")
    val post: List<List<Post>>,
)
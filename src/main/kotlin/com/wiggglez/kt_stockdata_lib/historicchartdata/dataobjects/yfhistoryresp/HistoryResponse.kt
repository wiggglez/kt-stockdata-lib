package com.wiggglez.kt_stockdata_lib.historicchartdata.dataobjects.yfhistoryresp


import com.google.gson.annotations.SerializedName

internal data class HistoryResponse(
    @SerializedName("chart")
    val chart: Chart
)
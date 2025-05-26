package com.wiggglez.kt_stockdata_lib.marketdata.dataobjects

import com.wiggglez.kt_stockdata_lib.common.Log
import com.wiggglez.kt_stockdata_lib.common.parseDouble
import com.wiggglez.kt_stockdata_lib.common.parseLongFromBigAbbreviatedNumbers
import com.wiggglez.kt_stockdata_lib.common.w

data class Index(
    val ticker: String,
    val name: String,
    val lastPrice: Double,
    val changeDollar: Double,
    val changePercent: Double,
    val volume: Long
) {
    companion object {
        fun createFromList(data: List<String>): Index? {

            try {

                if (data[0][0].isDigit()) {
                    return null
                }
                val splitName = data[1].split(" ")

                val capitalizedNames = splitName.map { word ->

                    word.lowercase().replaceFirstChar {
                        it.uppercase()
                    }
                }

                return Index(
                    data[0].replace(" ", ""),
                    capitalizedNames.joinToString(separator = " "),
                    parseDouble(data[2]),
                    parseDouble(data[3]),
                    parseDouble(data[4].substringBefore("%")),
                    parseLongFromBigAbbreviatedNumbers(data[5])
                )


            } catch (e: Exception) {
                Log.w(
                    "Index", "Failed to create data object 'Index' from list of data." +
                            "List: $data, Exception: $e \nStacktrace: ${e.stackTraceToString()}"
                )
                return null
            }
        }
    }
}

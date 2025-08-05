package com.wiggglez.kt_stockdata_lib.historicchartdata

import com.wiggglez.kt_stockdata_lib.common.*
import com.wiggglez.kt_stockdata_lib.historicchartdata.charts.StockChartBase
import com.wiggglez.kt_stockdata_lib.historicchartdata.charts.advancedchart.AdvancedStockChart
import com.wiggglez.kt_stockdata_lib.historicchartdata.charts.simplechart.SimpleStockChart
import com.wiggglez.kt_stockdata_lib.historicchartdata.dataobjects.yfhistoryresp.HistoryResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.Request
import okhttp3.Response
import java.util.*
import kotlin.coroutines.coroutineContext




// TODO -- Its quittin time and i just stepped in a pile of Shite...
//      Tried to get a 5m AdvancedStockChart in 'tradebot' and got an error
//      saying that 24524524352452 (not really but similar) was too big to be an Int.
//      So basically I have to go through this whole project and replace the
//      volume: Int with volume: Long which is a nightmare...
//      I honestly think I should just write a YaFinChartsKt project lib where
//      The only purpose of the lib is to get charts as simply as possible
//      I can copy paste what i need from here and it'll be useful
//
//      TODO -- IMPORTANT
//          VERIFY That Yahoo Fin hasn't cockblocked you and is limiting/ giving old data!!!!
//          We need up-to-date real time chart data


/**
 * Retrieves Historical Stock Data from Yahoo Finance in the form of...
 *
 * SimpleStockChart - Contains data and useful getters
 *
 * AdvancedStockChart - Contains data and useful getters + methods for technical analysis
 */
class HistoricChartData : IHistoricChartData {

    private val baseUrl = "https://query2.finance.yahoo.com"

    override suspend fun getHistoryAsSimpleStockChartAsync(
        ticker: String,
        interval: String,
        periodRange: String,
        prepost: Boolean
    ): SimpleStockChart? {
        val tick = ticker.uppercase()
        val history = getHistoryAsync(
            tick,
            interval,
            periodRange,
            prepost
        ) ?: return null

        if (interval == "1m") {
            return formatOneMin(history, prepost, SimpleStockChart::class.java) as SimpleStockChart
        }

        return SimpleStockChart(
            ticker = tick,
            interval = history.chart.result[0].meta.dataGranularity,
            periodRange = history.chart.result[0].meta.range,
            prepost = prepost,
            datetime = convertTimestampsToDatetime(history.chart.result[0].timestamp),
            timestamp = history.chart.result[0].timestamp,
            open = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].open
            ),
            high = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].high
            ),
            low = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].low
            ),
            close = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].close
            ),
            volume = removeNullValuesFromLongsList(
                history.chart.result[0].indicators.quote[0].volume
            )
        )
    }

    override suspend fun getHistoryAsAdvancedStockChartAsync(
        ticker: String,
        interval: String,
        periodRange: String,
        prepost: Boolean
    ): AdvancedStockChart? {
        val tick = ticker.uppercase()
        val history = getHistoryAsync(
            tick,
            interval,
            periodRange,
            prepost
        ) ?: return null

        if (interval == "1m") {
            return formatOneMin(
                history,
                prepost,
                AdvancedStockChart::class.java
            ) as AdvancedStockChart
        }

        return AdvancedStockChart(
            ticker = tick,
            interval = history.chart.result[0].meta.dataGranularity,
            periodRange = history.chart.result[0].meta.range,
            prepost = prepost,
            datetime = convertTimestampsToDatetime(history.chart.result[0].timestamp),
            timestamp = history.chart.result[0].timestamp,
            open = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].open
            ),
            high = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].high
            ),
            low = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].low
            ),
            close = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].close
            ),
            volume = removeNullValuesFromLongsList(
                history.chart.result[0].indicators.quote[0].volume
            )
        )
    }

    /**
     * Retrieves the Historical Chart Data for the specified Ticker. Start time depends on period,
     * End time is always current
     *
     * ..................................................................................
     *
     * - Chart Behavior...
     *
     * - lastIndex : Candle that is currently being formed. (No Volume Information)
     *
     * - lastIndex - 1 : Candle which is the Last fully formed. (Complete Information)
     *
     * ..................................................................................
     *
     * :param: ticker - Ticker you want data for
     *
     * :param:
     * interval - Valid intervals: 1m,2m,5m,15m,60m,90m,1h,1d,5d,1wk,1mo,3mo.
     *
     * :param:
     * period - Valid period depends on interval 1d,5d,1mo,3mo,6mo,1y,2y,5y,10y,ytd,max.
     *
     * :param:
     * prepost - Include premarket data
     *
     * :return:
     * SimpleStockChart?
     */
    override fun getHistoryAsSimpleStockChart(
        ticker: String,
        interval: String,
        periodRange: String,
        prepost: Boolean
    ): SimpleStockChart? {
        val tick = ticker.uppercase()
        val history = getHistory(tick, interval, periodRange, prepost) ?: return null

        // Bug with 1m chart's where the second to last candle has null values
        if (interval == "1m") {
            return formatOneMin(history, prepost, SimpleStockChart::class.java) as SimpleStockChart
        }

        return SimpleStockChart(
            ticker = tick,
            interval = history.chart.result[0].meta.dataGranularity,
            periodRange = history.chart.result[0].meta.range,
            prepost = prepost,
            datetime = convertTimestampsToDatetime(history.chart.result[0].timestamp),
            timestamp = history.chart.result[0].timestamp,
            open = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].open
            ),
            high = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].high
            ),
            low = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].low
            ),
            close = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].close
            ),
            volume = removeNullValuesFromLongsList(
                history.chart.result[0].indicators.quote[0].volume
            )
        )
    }


    /**
     * Retrieves the Historical Chart Data for the specified Ticker. Start time depends on period,
     * End time is always current.
     *
     * ..................................................................................
     *
     * - Chart Behavior...
     *
     * - lastIndex : Candle that is currently being formed. (No Volume Information)
     *
     * - lastIndex - 1 : Candle which is the Last fully formed. (Complete Information)
     *
     * ..................................................................................
     *
     * :param: ticker - Ticker you want data for
     *
     * :param:
     * interval - Valid intervals: 1m,2m,5m,15m,60m,90m,1h,1d,5d,1wk,1mo,3mo.
     *
     * :param:
     * period - Valid period depends on interval 1d,5d,1mo,3mo,6mo,1y,2y,5y,10y,ytd,max.
     *
     * :param:
     * prepost - Include premarket data
     *
     * :return:
     * AdvancedStockChart?
     */
    override fun getHistoryAsAdvancedStockChart(
        ticker: String,
        interval: String,
        periodRange: String,
        prepost: Boolean
    ): AdvancedStockChart? {
        val tick = ticker.uppercase()
        val history = getHistory(tick, interval, periodRange, prepost) ?: return null
        if (interval == "1m") {
            return formatOneMin(
                history,
                prepost,
                AdvancedStockChart::class.java
            ) as AdvancedStockChart
        }
        return AdvancedStockChart(
            ticker = tick,
            interval = history.chart.result[0].meta.dataGranularity,
            periodRange = history.chart.result[0].meta.range,
            prepost = prepost,
            datetime = convertTimestampsToDatetime(history.chart.result[0].timestamp),
            timestamp = history.chart.result[0].timestamp,
            open = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].open
            ),
            high = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].high
            ),
            low = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].low
            ),
            close = removeNullValuesFromDoublesList(
                history.chart.result[0].indicators.quote[0].close
            ),
            volume = removeNullValuesFromLongsList(
                history.chart.result[0].indicators.quote[0].volume
            )
        )
    }


    /**
     * Retrieves the Historical Chart Data for the specified Ticker. Start time depends on period,
     * End time is always current

     * :param: ticker - Ticker you want data for
     *
     * :param:
     * interval - Valid intervals: 1m,2m,5m,15m,60m,90m,1h,1d,5d,1wk,1mo,3mo.
     *
     * :param:
     * period - Valid period depends on interval 1d,5d,1mo,3mo,6mo,1y,2y,5y,10y,ytd,max.
     *
     * :param:
     * prepost - Include premarket data
     *
     * :return:
     * HistoryResponse?
     */
    private fun getHistory(
        ticker: String,
        interval: String = "5m",
        periodRange: String = "5d",
        prepost: Boolean = true
    ): HistoryResponse? {
        try {
            // Period
            val params = HashMap<String, String>()
            params["range"] = periodRange

            // Interval
            params["interval"] = interval

            // prepost
            params["includePrePost"] = prepost.toString()

            // Events
            params["events"] = "div,splits"

            // Build url
            val urlString = "$baseUrl/v8/finance/chart/${ticker.uppercase()}"

            // Build Call / Execute / Check Success
            val call = NetworkClient.getClient().newCall(
                Request.Builder()
                    .url(addParamsToUrl(urlString, params))
                    .get()
                    .build()
            )

            val resp = call.execute()

            if (!resp.isSuccessful) {
                displayNonSuccessInfo(resp)
                return null
            }

            // Convert to HistoryResponse and return
            val rawJsonResponse = resp.body!!.string()
            val history = gson.fromJson(rawJsonResponse, HistoryResponse::class.java)

            val verifyIntegrity = {
                val data = history.chart.result[0].indicators.quote[0]
                val nCandles = history.chart.result[0].timestamp.size

                // Verify that all data lists have equal size
                data.open.size == nCandles &&
                        data.high.size == nCandles &&
                        data.low.size == nCandles &&
                        data.close.size == nCandles &&
                        data.volume.size == nCandles
            }

            // Verify that all data OHLCV & Ts lists are equal size
            if (verifyIntegrity()) {
                return history
            }

            return null
        } catch (e: Exception) {
            println(e.stackTraceToString())
            Log.w("EXCEPTION", e.stackTraceToString())

            return null
        }
    }


    private suspend fun getHistoryAsync(
        ticker: String,
        interval: String = "5m",
        periodRange: String = "5d",
        prepost: Boolean = true
    ): HistoryResponse? {
        try {
            // Period
            val params = HashMap<String, String>()
            params["range"] = periodRange

            // Interval
            params["interval"] = interval

            // prepost
            params["includePrePost"] = prepost.toString()

            // Events
            params["events"] = "div,splits"

            // Build url
            val urlString = "$baseUrl/v8/finance/chart/${ticker.uppercase()}"

            // Build Call / Execute / Check Success
            val call = NetworkClient.getClient().newCall(
                Request.Builder()
                    .url(addParamsToUrl(urlString, params))
                    .get()
                    .build()
            )

            // Launch the Async Request
            val deferredReq: Deferred<Response> =
                CoroutineScope(coroutineContext).async(Dispatchers.Unconfined) {
                    call.execute()
                }

            // Await it
            val resp = deferredReq.await()

            if (!resp.isSuccessful) {
                displayNonSuccessInfo(resp)
                return null
            }

            // Convert to HistoryResponse and return
            val rawJsonResponse = resp.body!!.string()
            val history = gson.fromJson(rawJsonResponse, HistoryResponse::class.java)

            val verifyIntegrity = {
                val data = history.chart.result[0].indicators.quote[0]
                val nCandles = history.chart.result[0].timestamp.size

                // Verify that all data lists have equal size
                data.open.size == nCandles &&
                        data.high.size == nCandles &&
                        data.low.size == nCandles &&
                        data.close.size == nCandles &&
                        data.volume.size == nCandles
            }

            // Verify that all data OHLCV & Ts lists are equal size
            if (verifyIntegrity()) {
                return history
            }

            return null
        } catch (e: Exception) {
            println(e.stackTraceToString())
            Log.w("EXCEPTION", e.stackTraceToString())

            return null
        }
    }


    /**
     * Used to fix a bug with 1m interval chart where the candle at (lastIndex - 1) was basically
     * empty. So now the 1m chart behaves like the others...
     *
     * lastIndex:     The current partially formed candle (Accurate Close, Zero Volume).
     *
     * lastIndex - 1: The last fully formed candle.
     */
    private fun <T : StockChartBase> formatOneMin(
        chart: HistoryResponse,
        prepost: Boolean,
        t: Class<T>
    ): StockChartBase? {

        val outerData = chart.chart.result[0]
        val priceData = outerData.indicators.quote[0]

        val ticker = outerData.meta.symbol
        val interval = outerData.meta.dataGranularity
        val periodRange = outerData.meta.range

        val dateTimes = mutableListOf<Date>()
        outerData.timestamp.map {
            dateTimes.add(Date(it.toLong() * 1000))
        }

        val ts = outerData.timestamp.toMutableList()
        ts.removeAt(ts.lastIndex - 1)

        val open = priceData.open.toMutableList()
        open.removeAt(open.lastIndex - 1)

        val high = priceData.high.toMutableList()
        high.removeAt(high.lastIndex - 1)

        val low = priceData.low.toMutableList()
        low.removeAt(low.lastIndex - 1)

        val close = priceData.close.toMutableList()
        close.removeAt(close.lastIndex - 1)

        val volume = priceData.volume.toMutableList()
        volume.removeAt(volume.lastIndex - 1)

        if (t == AdvancedStockChart::class.java) {
            return AdvancedStockChart(
                ticker, interval, periodRange, prepost, dateTimes, ts,
                removeNullValuesFromDoublesList(open),
                removeNullValuesFromDoublesList(high),
                removeNullValuesFromDoublesList(low),
                removeNullValuesFromDoublesList(close),
                removeNullValuesFromLongsList(volume)
            )
        } else if (t == SimpleStockChart::class.java) {
            return SimpleStockChart(
                ticker, interval, periodRange, prepost, dateTimes, ts,
                removeNullValuesFromDoublesList(open),
                removeNullValuesFromDoublesList(high),
                removeNullValuesFromDoublesList(low),
                removeNullValuesFromDoublesList(close),
                removeNullValuesFromLongsList(volume)
            )
        }
        return null
    }


    /**
     * Takes list of Epoch Seconds Timestamp <Int> and converts to Date objects
     */
    private fun convertTimestampsToDatetime(tsList: List<Long>): List<Date> {
        val dtList = mutableListOf<Date>()

        for (ts in tsList) {
            dtList.add(Date(ts.toLong() * 1000))
        }

        return dtList
    }


    /**
     * Displays the Error msg for a failed request.
     */
    private fun displayNonSuccessInfo(resp: Response) {
        val msg = """
            - NETWORK REQUEST FAILURE -
            *\-------------------------------------------------------------------------/*
            
            SUCCESS:     ${resp.isSuccessful}
            CODE:        ${resp.code}
            MESSAGE:     ${resp.message}
            BODY:        ${resp.body!!.string()}
            
            *\-------------------------------------------------------------------------/*
        """.trimIndent()

        Log.w("NETWORK", msg)
        println(msg)
    }


    /**
     * Replaces Null values with 0.0.
     */
    private fun removeNullValuesFromDoublesList(input: List<Double?>): List<Double> {


        val result = input.toMutableList()

        for (i in result.indices.reversed()) {
            if (result[i] == null) {
                // Find the next non-null value (after i)
                val nextValue = result.subList(i + 1, result.size).firstOrNull { it != null }
                if (nextValue != null) {
                    result[i] = nextValue
                } else {
                    // If no next value, fallback (you could choose to remove it or leave it as null)
                    result[i] = 0.0  // or throw exception / return empty
                }
            }
        }

        return result.map { it!! } // All nulls should be filled at this point
    }


    /**
     * Replaces Null values with a neighboring value.
     */
    private fun removeNullValuesFromLongsList(input: List<Long?>): List<Long> {

        val result = input.toMutableList()

        for (i in result.indices.reversed()) {
            if (result[i] == null) {
                // Find the next non-null value (after i)
                val nextValue = result.subList(i + 1, result.size).firstOrNull { it != null }
                if (nextValue != null) {
                    result[i] = nextValue
                } else {
                    // If no next value, fallback (you could choose to remove it or leave it as null)
                    result[i] = 0  // or throw exception / return empty
                }
            }
        }

        return result.map { it!! } // All nulls should be filled at this point
    }
}

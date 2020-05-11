package com.vladimirpetrovski.currencyconverter.data.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.math.BigDecimal

class BigDecimalJsonAdapter : JsonAdapter<BigDecimal>() {

    override fun toJson(writer: JsonWriter, value: BigDecimal?) {
        writer.value(value.toString())
    }

    override fun fromJson(reader: JsonReader): BigDecimal {
        return BigDecimal(reader.nextString())
    }
}

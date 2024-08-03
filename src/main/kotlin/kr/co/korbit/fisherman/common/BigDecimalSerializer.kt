package kr.co.korbit.fisherman.common

import com.google.gson.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.sql.Timestamp

class BigDecimalSerializer : JsonSerializer<BigDecimal>{
    override fun serialize(src: BigDecimal?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if( src == null ) {
            return JsonNull()
        }
        else {
            return JsonPrimitive(src.stripTrailingZeros().toPlainString())
        }
    }
}

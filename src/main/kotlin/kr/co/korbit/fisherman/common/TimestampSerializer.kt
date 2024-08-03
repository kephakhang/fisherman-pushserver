package kr.co.korbit.fisherman.common

import com.google.gson.*
import java.lang.reflect.Type
import java.sql.Timestamp

class TimestampSerializer : JsonSerializer<Timestamp>{
    override fun serialize(src: Timestamp?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if( src == null ) {
            return JsonNull()
        }
        else {
            return JsonPrimitive(src.time)
        }
    }
}

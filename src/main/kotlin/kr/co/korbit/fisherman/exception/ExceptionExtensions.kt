package kr.co.korbit.fisherman.exception

import java.io.PrintWriter
import java.io.StringWriter

// Extension property on Exception
val kotlin.Exception.stackTraceString: String
    get(): String {
        val stringWriter = StringWriter()
        this.printStackTrace(PrintWriter(stringWriter))
        val stackTrace = stringWriter.toString()
        return stackTrace
    }

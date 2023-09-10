package com.sinxn.spotify2yt.ytmibrary.mixins

import java.util.regex.Pattern

fun htmlToTxt(htmlText: String): String {
    val pattern = Pattern.compile("<[^>]+>")
    val matcher = pattern.matcher(htmlText)
    val result = StringBuffer()

    while (matcher.find()) {
        matcher.appendReplacement(result, "")
    }

    matcher.appendTail(result)
    return result.toString()
}






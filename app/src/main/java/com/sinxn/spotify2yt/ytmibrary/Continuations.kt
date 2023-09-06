package com.sinxn.spotify2yt.ytmibrary

import com.google.gson.JsonArray
import com.google.gson.JsonObject

suspend fun getContinuations(
    results: JsonObject,
    continuation_type: String,
    limit: Int?,
    requestFunc: suspend (String) -> JsonObject,
    parse_func: (JsonObject) -> JsonArray?,
    ctoken_path: String="",
    reloadable: Boolean =false
): JsonArray
{
    var results = results
    val items = JsonArray()
    while( results.getAsJsonObject("continuations")!=null && (limit == null || items.size() < limit))
    {
        val additionalParams = if (reloadable) getReloadableContinuationParams(results)
        else getContinuationParams(results, ctoken_path)
        val response = requestFunc(additionalParams)
        if (response.getAsJsonObject("continuationContents")!=null)
            results = response.getAsJsonObject("continuationContents").getAsJsonObject(continuation_type)
        else break
        val contents = getContinuationContents(results, parse_func)
        if (contents?.size()==0)
            break
        items.addAll(contents)
    }

    return items
}

fun getContinuationString(ctoken: String?): String {
    return if (!ctoken.isNullOrEmpty())"&ctoken=$ctoken&continuation=$ctoken" else ""
}

fun getContinuationContents(continuation: JsonObject, parseFunc: (JsonObject) -> JsonArray?): JsonArray? {
    val terms = listOf("contents", "items")

    for (term in terms) {
        if ( continuation.has(term)) {
            return parseFunc(continuation[term].asJsonObject)
        }
    }

    return JsonArray()
}


fun getContinuationParams(results: JsonObject, ctokenPath: String = ""): String {
    val ctoken = nav(
        results,
        listOf("continuations", 0, "next${ctokenPath}ContinuationData", "continuation")
    )?.asString
    return getContinuationString(ctoken)
}

fun getReloadableContinuationParams(results: JsonObject): String {
    val ctoken = nav(results, listOf("continuations", 0, "reloadContinuationData", "continuation"))?.asString
    return getContinuationString(ctoken)
}

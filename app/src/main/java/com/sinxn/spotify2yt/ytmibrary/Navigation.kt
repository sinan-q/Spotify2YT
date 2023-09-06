package com.sinxn.spotify2yt.ytmibrary

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun nav(root: JsonObject, items: List<Any>, none_if_absent: Boolean=false): JsonElement?
{
    """Access a nested object in root by item sequence."""
    var root: JsonElement = root
    return try {
        for (k in items) {
            root = if (root.isJsonObject && root.asJsonObject.has(k.toString()))
                root.asJsonObject[k.toString()]
            else if (root.isJsonArray && root.asJsonArray.size() > k.toString().toInt())
                root.asJsonArray.get(k.toString().toInt())
            else throw Exception("$items $k $root")
        }
        root
    }
    catch (err: Exception) {
        if (none_if_absent)
            null
        else
            throw Exception("Error wrong json type $err")
    }

}

fun findObjectByKey(objectList: JsonObject, key: String, nested: String? = null, isKey: Boolean = false): JsonObject {
    for (item in objectList.asJsonArray) {
        var currentItem = item.asJsonObject
        if (nested != null) {
            currentItem = item.asJsonObject.getAsJsonObject(nested)
        }
        if (currentItem.has(key)) {
            return if (isKey) {
                currentItem.getAsJsonObject(key)
            } else {
                currentItem
            }
        }
    }
    return JsonObject()
}

fun findObjectsByKey(objectList: JsonArray, key: String, nested: String? = null): JsonArray {
    val objects = JsonArray()
    for (item in objectList.asJsonArray) {
        var currentItem = item.asJsonObject
        if (nested != null) {
            currentItem = item.asJsonObject.getAsJsonObject(nested)
        }
        if (currentItem.has(key)) {
            objects.add(currentItem)
        }
    }
    return objects
}

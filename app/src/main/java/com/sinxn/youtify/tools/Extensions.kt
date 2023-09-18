package com.sinxn.youtify.tools

import com.adamratzman.spotify.models.Track

private val urlMap = mutableMapOf<Track, String?>()


var Track.url: String?
    get() = urlMap[this]
    set(value) {
        urlMap[this] = value
    }
var Track.ytId: String?
    get() = urlMap[this]
    set(value) {
        urlMap[this] = value
    }

fun String.similarityTo(that: String): Float {
    val m = this.length
    val n = that.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) {
        for (j in 0..n) {
            when {
                i == 0 -> dp[i][j] = j
                j == 0 -> dp[i][j] = i
                this[i - 1] == that[j - 1] -> dp[i][j] = dp[i - 1][j - 1]
                else -> dp[i][j] = 1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
            }
        }
    }
    return 1f - dp[m][n] / n.toFloat()
}
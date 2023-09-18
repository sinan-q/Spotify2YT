package com.sinxn.youtify.ytmibrary

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sinxn.youtify.ytmibrary.auth.isOAuth
import com.sinxn.youtify.ytmibrary.auth.loadHeadersFile
import com.sinxn.youtify.ytmibrary.auth.prepareHeaders
import com.sinxn.youtify.ytmibrary.parsers.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class YTMusic(
    private val auth: String? = null,
    private val user: String? = null,
    private val requestsSession: Boolean = true,
    private var proxies: JsonObject? = null,
    private var language: String = "en",
    private val location: String = ""
) {
    /**
     * Allows automated interactions with YouTube Music by emulating the YouTube web client's requests.
     * Permits both authenticated and non-authenticated requests.
     * Authentication header data must be provided on initialization.
     */
    var parser = Parser()

    private var inputDict: JsonObject? = null
    private var isOAuthAuth = false

    private val session: OkHttpClient = if (requestsSession) {
        OkHttpClient.Builder()
            .callTimeout(30L, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    private val cookies = JsonObject().apply { addProperty("CONSENT", "YES+1") }
    private var headers = JsonObject()

    private val context: JsonObject = initializeContext()


    init {
        runBlocking {

            if (auth != null) {
                val inputJson = loadHeadersFile(auth)
                inputJson.addProperty("filepath", auth)
                inputDict = inputJson
                isOAuthAuth = isOAuth(inputDict)
            }
            headers = prepareHeaders(session, proxies, inputDict)

            if (!headers.has("x-goog-visitor-id")) {
                headers.addProperty("X-Goog-Visitor-Id", getVisitorId { sendGetRequest(it) })
            }

            if (location.isNotEmpty()) {
                if (!YTAuth.SUPPORTED_LOCATIONS.contains(location)) {
                    throw Exception("Location not supported. Check the FAQ for supported locations.")
                }
                context.getAsJsonObject("context").getAsJsonObject("client")
                    .addProperty("gl", location)
            }

            if (!YTAuth.SUPPORTED_LANGUAGES.contains(language)) {
                throw Exception("Language not supported. Supported languages are ${YTAuth.SUPPORTED_LANGUAGES.joinToString()}.")
            }
            context.getAsJsonObject("context").getAsJsonObject("client").addProperty("hl", language)


            val localeDir = "${System.getProperty("user.dir")}/locales"
            //  val lang = ResourceBundle.getBundle("base", Locale(language))
            // parser = Parser(lang.toString())

            if (user != null) {
                context.getAsJsonObject("context").getAsJsonObject("user")
                    .addProperty("onBehalfOfUser", user)
            }

            //  val authHeader = headers["authorization"]
            //  val isBrowserAuth: Boolean = authHeader?.has("SAPISIDHASH") ?: false
        }

    }


    private suspend fun sendGetRequest(baseUrl: String, params: JsonObject? = null): Response {
        return withContext(Dispatchers.IO) {
            val urlWithParams = addParamsToUrl(baseUrl, params)
            val requestBuilder = Request.Builder().apply {
                url(urlWithParams)
                headers(headers.toHeaders())
                if (cookies != JsonObject()) headers(cookies.toHeaders())
                if (proxies != null && proxies != JsonObject()) headers(proxies!!.toHeaders())
            }


            // Add proxies and cookies here if needed

            val request = requestBuilder.build()
            session.newCall(request).execute()
        }
    }

    private fun addParamsToUrl(baseUrl: String, params: JsonObject?): String {
        val urlBuilder = baseUrl.toHttpUrlOrNull()?.newBuilder()
            ?: throw IllegalArgumentException("Invalid base URL")
        params?.let {
            urlBuilder.addQueryParameter("params", it.toString())
        }
        return urlBuilder.build().toString()
    }

    fun checkAuth() {
        if (auth == null) {
            throw Exception("Please provide authentication before using this function")
        }
    }

    suspend fun sendRequest(
        endpoint: String,
        body: JsonObject,
        additionalParams: String = ""
    ): JsonObject {
        return withContext(Dispatchers.IO) {

            if (isOAuthAuth) {
                headers = prepareHeaders(session, proxies, inputDict)
            }
            val bodys = body.plus(context)
            val params = YTAuth.YTM_PARAMS
            val requestBody = bodys.toString().toRequestBody(JSON_MEDIA_TYPE)


            val headers = headers
            val auth = headers.remove("Authorization")
            proxies?.let { headers.plus(it) }
            headers.addProperty("Cookie", "CONSENT=YES+1")
            val request = Request.Builder().apply {
                url(YTAuth.YTM_BASE_API + endpoint + params + additionalParams)
                post(requestBody)
                headers(headers.toHeaders())
                addHeader("Authorization", auth.asString)
            }.build()
            val response: Response = session.newBuilder().build().newCall(request).execute()
            if (response.code >= 400) {
                val message = "Server returned HTTP ${response.code}: ${response.message}.\n"
                throw Exception("$message")
            }

            return@withContext JsonParser().parse(response.body.string()).asJsonObject
        }
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }


}
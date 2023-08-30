package com.sinxn.spotify2yt.tools

object Routes {
    const val HOME_SCREEN = "home"
    const val SETUP_SCREEN = "setup"
}

object YTAuth {
    const val YTM_DOMAIN = "https://music.youtube.com"
    const val YTM_BASE_API = YTM_DOMAIN + "/youtubei/v1/"
    const val YTM_PARAMS = "?alt=json"
    const val YTM_PARAMS_KEY = "&key=AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30"
    const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0"
    val SUPPORTED_LANGUAGES = listOf(
        "ar", "de", "en", "es", "fr", "hi", "it", "ja", "ko", "nl", "pt", "ru", "tr", "ur", "zh_CN",
        "zh_TW"
    )

    val SUPPORTED_LOCATIONS = listOf(
        "AE", "AR", "AT", "AU", "AZ", "BA", "BD", "BE", "BG", "BH", "BO", "BR", "BY", "CA", "CH", "CL",
        "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "DZ", "EC", "EE", "EG", "ES", "FI", "FR", "GB", "GE",
        "GH", "GR", "GT", "HK", "HN", "HR", "HU", "ID", "IE", "IL", "IN", "IQ", "IS", "IT", "JM", "JO",
        "JP", "KE", "KH", "KR", "KW", "KZ", "LA", "LB", "LI", "LK", "LT", "LU", "LV", "LY", "MA", "ME",
        "MK", "MT", "MX", "MY", "NG", "NI", "NL", "NO", "NP", "NZ", "OM", "PA", "PE", "PG", "PH", "PK",
        "PL", "PR", "PT", "PY", "QA", "RO", "RS", "RU", "SA", "SE", "SG", "SI", "SK", "SN", "SV", "TH",
        "TN", "TR", "TW", "TZ", "UA", "UG", "US", "UY", "VE", "VN", "YE", "ZA", "ZW"

    )
    const val OAUTH_CLIENT_ID = "861556708454-d6dlm3lh05idd8npek18k6be8ba3oc68.apps.googleusercontent.com"
    const val OAUTH_CLIENT_SECRET = "SboVhoG9s0rNafixCSGGKXAT"
    const val OAUTH_SCOPE = "https://www.googleapis.com/auth/youtube"
    const val OAUTH_USER_AGENT = USER_AGENT + " Cobalt/Version"
}
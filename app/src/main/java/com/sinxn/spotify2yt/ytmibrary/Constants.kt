package com.sinxn.spotify2yt.ytmibrary

object YTAuth{
    const val YTM_DOMAIN = "https://music.youtube.com"
const val YTM_BASE_API = "$YTM_DOMAIN/youtubei/v1/"
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
    const val OAUTH_CODE_URL = "https://www.youtube.com/o/oauth2/device/code"

    const val OAUTH_TOKEN_URL = "https://oauth2.googleapis.com/token"
    const val OAUTH_USER_AGENT = "$USER_AGENT Cobalt/Version"

    val CONTENT = arrayListOf<Any>("contents", 0)
    val RUN_TEXT = arrayListOf<Any>("runs", 0, "text")
    val TAB_CONTENT = arrayListOf<Any>("tabs", 0, "tabRenderer", "content")
    val TAB_1_CONTENT = arrayListOf<Any>("tabs", 1, "tabRenderer", "content")
    val SINGLE_COLUMN = arrayListOf<Any>("contents", "singleColumnBrowseResultsRenderer")
    val SINGLE_COLUMN_TAB = SINGLE_COLUMN + TAB_CONTENT
    val SECTION_LIST = arrayListOf<Any>("sectionListRenderer", "contents")
    val SECTION_LIST_ITEM = arrayListOf<Any>("sectionListRenderer") + CONTENT
    val ITEM_SECTION = arrayListOf<Any>("itemSectionRenderer") + CONTENT
    val MUSIC_SHELF = arrayListOf<Any>("musicShelfRenderer")
    val GRID = arrayListOf<Any>("gridRenderer")
    val GRID_ITEMS = GRID + arrayListOf<Any>("items")
    val MENU = arrayListOf<Any>("menu", "menuRenderer")
    val MENU_ITEMS = MENU + arrayListOf<Any>("items")
    val MENU_LIKE_STATUS =
        MENU + arrayListOf<Any>("topLevelButtons", 0, "likeButtonRenderer", "likeStatus")
    val MENU_SERVICE = arrayListOf<Any>("menuServiceItemRenderer", "serviceEndpoint")
    val TOGGLE_MENU = arrayListOf<Any>("toggleMenuServiceItemRenderer")
    val PLAY_BUTTON = arrayListOf<Any>(
        "overlay",
        "musicItemThumbnailOverlayRenderer",
        "content",
        "musicPlayButtonRenderer"
    )
    val NAVIGATION_BROWSE = arrayListOf<Any>("navigationEndpoint", "browseEndpoint")
    val NAVIGATION_BROWSE_ID = NAVIGATION_BROWSE + arrayListOf<Any>("browseId")
    val PAGE_TYPE =
        arrayListOf<Any>(
            "browseEndpointContextSupportedConfigs",
            "browseEndpointContextMusicConfig",
            "pageType"
        )
    val WATCH_VIDEO_ID = arrayListOf<Any>("watchEndpoint", "videoId")
    val NAVIGATION_VIDEO_ID = arrayListOf<Any>("navigationEndpoint") + WATCH_VIDEO_ID
    val NAVIGATION_PLAYLIST_ID =
        arrayListOf<Any>("navigationEndpoint", "watchEndpoint", "playlistId")
    val NAVIGATION_WATCH_PLAYLIST_ID =
        arrayListOf<Any>("navigationEndpoint", "watchPlaylistEndpoint", "playlistId")
    val NAVIGATION_VIDEO_TYPE = arrayListOf<Any>(
        "watchEndpoint",
        "watchEndpointMusicSupportedConfigs",
        "watchEndpointMusicConfig",
        "musicVideoType"
    )
    val TITLE = arrayListOf<Any>("title", "runs", 0)
    val TITLE_TEXT = arrayListOf<Any>("title") + RUN_TEXT
    val TEXT_RUNS = arrayListOf<Any>("text", "runs")
    val TEXT_RUN = TEXT_RUNS + arrayListOf<Any>(0)
    val TEXT_RUN_TEXT = TEXT_RUN + arrayListOf<Any>("text")
    val SUBTITLE = arrayListOf<Any>("subtitle") + RUN_TEXT
    val SUBTITLE_RUNS = arrayListOf<Any>("subtitle", "runs")
    val SUBTITLE2 = SUBTITLE_RUNS + arrayListOf<Any>(2, "text")
    val SUBTITLE3 = SUBTITLE_RUNS + arrayListOf<Any>(4, "text")
    val THUMBNAIL = arrayListOf<Any>("thumbnail", "thumbnails")
    val THUMBNAILS = arrayListOf<Any>("thumbnail", "musicThumbnailRenderer") + THUMBNAIL
    val THUMBNAIL_RENDERER =
        arrayListOf<Any>("thumbnailRenderer", "musicThumbnailRenderer") + THUMBNAIL
    val THUMBNAIL_CROPPED =
        arrayListOf<Any>("thumbnail", "croppedSquareThumbnailRenderer") + THUMBNAIL
    val FEEDBACK_TOKEN = arrayListOf<Any>("feedbackEndpoint", "feedbackToken")
    val BADGE_PATH = arrayListOf<Any>(
        0,
        "musicInlineBadgeRenderer",
        "accessibilityData",
        "accessibilityData",
        "label"
    )
    val BADGE_LABEL = arrayListOf<Any>("badges") + BADGE_PATH
    val SUBTITLE_BADGE_LABEL = arrayListOf<Any>("subtitleBadges") + BADGE_PATH
    val CATEGORY_TITLE = arrayListOf<Any>("musicNavigationButtonRenderer", "buttonText") + RUN_TEXT
    val CATEGORY_PARAMS =
        arrayListOf<Any>("musicNavigationButtonRenderer", "clickCommand", "browseEndpoint", "params")
    val MRLIR = "musicResponsiveListItemRenderer"
    val MTRIR = "musicTwoRowItemRenderer"
    val TASTE_PROFILE_ITEMS = arrayListOf<Any>("contents", "tastebuilderRenderer", "contents")
    val TASTE_PROFILE_ARTIST = arrayListOf<Any>("title", "runs")
    val SECTION_LIST_CONTINUATION = arrayListOf<Any>("continuationContents", "sectionListContinuation")
    val MENU_PLAYLIST_ID =
        MENU_ITEMS + arrayListOf<Any>(0, "menuNavigationItemRenderer") + NAVIGATION_WATCH_PLAYLIST_ID
    val HEADER_DETAIL = arrayListOf<Any>("header", "musicDetailHeaderRenderer")
    val DESCRIPTION_SHELF = arrayListOf<Any>("musicDescriptionShelfRenderer")
    val DESCRIPTION = arrayListOf<Any>("description") + RUN_TEXT
    val CAROUSEL = arrayListOf<Any>("musicCarouselShelfRenderer")
    val IMMERSIVE_CAROUSEL = arrayListOf<Any>("musicImmersiveCarouselShelfRenderer")
    val CAROUSEL_CONTENTS = CAROUSEL + arrayListOf<Any>("contents")
    val CAROUSEL_TITLE = arrayListOf<Any>("header", "musicCarouselShelfBasicHeaderRenderer") + TITLE
    val CARD_SHELF_TITLE =
        arrayListOf<Any>("header", "musicCardShelfHeaderBasicRenderer") + TITLE_TEXT
    val FRAMEWORK_MUTATIONS = arrayListOf<Any>("frameworkUpdates", "entityBatchUpdate", "mutations")
}


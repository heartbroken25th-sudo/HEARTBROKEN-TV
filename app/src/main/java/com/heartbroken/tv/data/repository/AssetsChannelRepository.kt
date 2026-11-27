package com.heartbroken.tv.data.repository

import android.content.Context
import com.heartbroken.tv.data.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * [ChannelRepository] implementation that reads the bundled
 * `assets/channels.json` file.
 *
 * File format: a JSON array of channel objects.
 *
 *   [
 *     {
 *       "id": "ug-ntv-news",
 *       "name": "NTV Uganda",
 *       "streamUrl": "https://example.com/ntv.m3u8",
 *       "logoUrl": "https://example.com/ntv.png",
 *       "countryCode": "UG",
 *       "countryName": "Uganda",
 *       "category": "News",
 *       "language": "English",
 *       "isLive": true,
 *       "description": "Uganda's leading news and entertainment channel."
 *     }
 *   ]
 *
 * Fields marked required ("id", "name", "streamUrl", "countryCode",
 * "countryName", "category") cause the whole load to fail if missing from a
 * single entry. That is intentional: a malformed catalog should surface as an
 * error, not silently drop channels.
 *
 * Optional fields ("logoUrl", "language", "description") fall back to null
 * when absent. "isLive" defaults to true when absent.
 *
 * An empty array "[]" is a valid catalog — the app will show the empty state,
 * not an error. This lets you ship an APK with no channels while you build out
 * the rest of the app.
 */
class AssetsChannelRepository(
    private val context: Context
) : ChannelRepository {

    override suspend fun getChannels(): Result<List<Channel>> = withContext(Dispatchers.IO) {
        try {
            val jsonText = context.assets
                .open(ASSET_FILE_NAME)
                .bufferedReader()
                .use { it.readText() }

            val array = JSONArray(jsonText)
            val channels = ArrayList<Channel>(array.length())

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                channels.add(obj.toChannel())
            }

            Result.success(channels)
        } catch (e: Exception) {
            // Any parse error, missing field, or missing asset file lands here.
            // The UI will surface this as a proper error state with a retry.
            Result.failure(e)
        }
    }

    private fun JSONObject.toChannel(): Channel = Channel(
        id = getString("id"),
        name = getString("name"),
        streamUrl = getString("streamUrl"),
        logoUrl = optStringOrNull("logoUrl"),
        countryCode = getString("countryCode").uppercase(),
        countryName = getString("countryName"),
        category = getString("category"),
        language = optStringOrNull("language"),
        isLive = optBoolean("isLive", true),
        description = optStringOrNull("description")
    )

    private fun JSONObject.optStringOrNull(key: String): String? {
        if (!has(key) || isNull(key)) return null
        val value = optString(key)
        return value.takeIf { it.isNotBlank() }
    }

    private companion object {
        const val ASSET_FILE_NAME = "channels.json"
    }
}

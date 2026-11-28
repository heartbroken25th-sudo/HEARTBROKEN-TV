package com.heartbroken.tv.data.repository

import com.heartbroken.tv.data.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class M3uChannelRepository(
    private val countryCode: String = "ug" // Default to Uganda
) : ChannelRepository {

    private val client = OkHttpClient()

    override suspend fun getChannels(): Result<List<Channel>> = withContext(Dispatchers.IO) {
        try {
            val url = "https://iptv-org.github.io/iptv/countries/$countryCode.m3u"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to download playlist: HTTP ${response.code}"))
            }

            val m3uText = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val channels = parseM3u(m3uText, countryCode)
            
            Result.success(channels)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseM3u(m3u: String, countryCode: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        var currentName = ""
        var currentLogo: String? = null
        var currentGroup = "General"
        var currentId = ""
        var skipCurrent = false

        m3u.lines().forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("#EXTINF:") -> {
                    skipCurrent = false
                    
                    val nameMatch = Regex(",(.*)$").find(trimmed)
                    currentName = nameMatch?.groupValues?.get(1)?.trim() ?: "Unknown"
                    
                    if (currentName.contains("[Not 24/7]", ignoreCase = true)) {
                        skipCurrent = true
                    }

                    val logoMatch = Regex("tvg-logo=\"(.*?)\"").find(trimmed)
                    currentLogo = logoMatch?.groupValues?.get(1)?.takeIf { it.isNotBlank() }

                    val groupMatch = Regex("group-title=\"(.*?)\"").find(trimmed)
                    currentGroup = groupMatch?.groupValues?.get(1)?.takeIf { it.isNotBlank() } ?: "General"

                    val idMatch = Regex("tvg-id=\"(.*?)\"").find(trimmed)
                    currentId = idMatch?.groupValues?.get(1)?.takeIf { it.isNotBlank() } ?: currentName
                }
                
                trimmed.startsWith("http") && currentName.isNotEmpty() -> {
                    if (!skipCurrent) {
                        val cleanName = currentName.replace(Regex("\\(\\d+p\\)"), "").trim()
                        
                        channels.add(
                            Channel(
                                id = currentId,
                                name = cleanName,
                                streamUrl = trimmed,
                                logoUrl = currentLogo,
                                countryCode = countryCode.uppercase(),
                                countryName = getCountryName(countryCode),
                                category = currentGroup,
                                isLive = true
                            )
                        )
                    }
                    currentName = ""
                    currentLogo = null
                }
            }
        }
        return channels
    }

    private fun getCountryName(code: String): String {
        return when (code.lowercase()) {
            "ug" -> "Uganda"
            "ke" -> "Kenya"
            "tz" -> "Tanzania"
            "us" -> "United States"
            "uk" -> "United Kingdom"
            else -> code.uppercase()
        }
    }
}

package com.heartbroken.tv.data.model

/**
 * A single television channel in the HEARTBROKEN TV catalog.
 *
 * This model is the single source of truth for channel data across the entire
 * app — the Home discovery screen, the Live TV channel grid, and the player all
 * read from this shape.
 *
 * Design rules:
 *  - [streamUrl] is REQUIRED — a channel without a real stream URL is not a
 *    playable channel and should not exist in the catalog.
 *  - [logoUrl] is OPTIONAL — many real channels have no logo, and the UI must
 *    gracefully fall back to a branded tile.
 *  - [countryCode] is the ISO 3166-1 alpha-2 country code (e.g. "UG" for
 *    Uganda, "KE" for Kenya). Grouping and filtering depend on this being
 *    stable and uppercase.
 *  - [countryName] is the human-readable name shown in the UI. It is kept
 *    alongside the code so that the UI never has to look up a country display
 *    name from a separate table.
 *  - [category] is free-form for now (e.g. "News", "Sports", "Entertainment").
 *    Categories are derived from the actual data — do not invent categories
 *    that no channel uses.
 *  - [isLive] defaults to true. Some sources mark a channel as offline; that is
 *    reflected here so the grid can dim unavailable channels.
 */
data class Channel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val countryCode: String,
    val countryName: String,
    val category: String,
    val language: String? = null,
    val isLive: Boolean = true,
    val description: String? = null
)

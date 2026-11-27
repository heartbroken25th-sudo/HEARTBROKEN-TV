package com.heartbroken.tv.data.repository

import com.heartbroken.tv.data.model.Channel

/**
 * Source of channel data for the entire application.
 *
 * The Home screen, Live TV grid, and future player all read channels through
 * this interface. Concrete implementations (assets-backed, remote-backed,
 * cached) plug into the same contract — so swapping data sources later is a
 * one-line change at the DI boundary, never a rewrite of the UI.
 *
 * All methods return [Result] so callers can present honest error states
 * instead of pretending data exists. There is no silent fallback to fake data
 * anywhere in this contract.
 */
interface ChannelRepository {

    /**
     * Returns every channel currently available in the catalog.
     *
     * On success the returned list may be empty — that means "catalog is
     * genuinely empty", not "an error happened". The UI must distinguish
     * between those two cases with different screens.
     */
    suspend fun getChannels(): Result<List<Channel>>
}

package com.spotify.client.spotify

class SearchResponse(
        val artists: Artists,
        val tracks: Tracks
)

class Tracks(
        val href: String,
        val items: List<TrackItem>,
        val limit: Int,
        val next: String,
        val offset: Int,
        val previous: String,
        val total: Int
)

class TrackItem(
        val album: Album,
        val artists: List<Artist>,
        val disc_number: Int,
        val duration_ms: Int,
        val explicit: Boolean,
        val external_ids: ExternalIds,
        val external_urls: ExternalUrls,
        val href: String,
        val id: String,
        val is_local: Boolean,
        val is_playable: Boolean,
        val name: String,
        val popularity: Int,
        val preview_url: String,
        val track_number: Int,
        val type: String,
        val uri: String
)

class Artist(
        val external_urls: ExternalUrls,
        val href: String,
        val id: String,
        val name: String,
        val type: String,
        val uri: String
)

class Album(
        val album_type: String,
        val artists: List<Artist>,
        val external_urls: ExternalUrls,
        val href: String,
        val id: String,
        val images: List<Image>,
        val name: String,
        val release_date: String,
        val release_date_precision: String,
        val total_tracks: Int,
        val type: String,
        val uri: String
)

class Artists(
        val href: String,
        val items: List<ArtistItems>,
        val limit: Int,
        val next: String,
        val offset: Int,
        val previous: String,
        val total: Int
)

class ArtistItems(
        val external_urls: ExternalUrls,
        val genres: List<Any>,
        val href: String,
        val id: String,
        val images: List<Image>,
        val name: String,
        val popularity: Int,
        val type: String,
        val uri: String
)

class TracksResponse(
        val tracks: List<Track>
)

class Track(
        val album: TrackAlbum,
        val artists: List<TrackArtist>,
        val available_markets: List<String>,
        val disc_number: Int,
        val duration_ms: Int,
        val explicit: Boolean,
        val external_ids: ExternalIds,
        val external_urls: ExternalUrls,
        val href: String,
        val id: String,
        val is_local: Boolean,
        val name: String,
        val popularity: Int,
        val preview_url: String,
        val track_number: Int,
        val type: String,
        val uri: String
)

class ExternalUrls(
        val spotify: String
)

class TrackArtist(
        val external_urls: ExternalUrls,
        val href: String,
        val id: String,
        val name: String,
        val type: String,
        val uri: String
)

class ExternalIds(
        val isrc: String
)

class TrackAlbum(
        val album_type: String,
        val artists: List<Artist>,
        val available_markets: List<String>,
        val external_urls: ExternalUrls,
        val href: String,
        val id: String,
        val images: List<Image>,
        val name: String,
        val release_date: String,
        val release_date_precision: String,
        val type: String,
        val uri: String
)

class Image(
        val height: Int?,
        val url: String,
        val width: Int?
)

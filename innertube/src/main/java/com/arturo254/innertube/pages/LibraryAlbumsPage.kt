package com.huayin.music.innertube.pages

import com.huayin.music.innertube.models.Album
import com.huayin.music.innertube.models.AlbumItem
import com.huayin.music.innertube.models.Artist
import com.huayin.music.innertube.models.ArtistItem
import com.huayin.music.innertube.models.MusicResponsiveListItemRenderer
import com.huayin.music.innertube.models.MusicTwoRowItemRenderer
import com.huayin.music.innertube.models.PlaylistItem
import com.huayin.music.innertube.models.SongItem
import com.huayin.music.innertube.models.YTItem
import com.huayin.music.innertube.models.oddElements
import com.huayin.music.innertube.utils.parseTime

data class LibraryAlbumsPage(
    val albums: List<AlbumItem>,
    val continuation: String?,
) {
    companion object {
        fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): AlbumItem? {
            return AlbumItem(
                        browseId = renderer.navigationEndpoint.browseEndpoint?.browseId ?: return null,
                        playlistId = renderer.thumbnailOverlay?.musicItemThumbnailOverlayRenderer?.content
                            ?.musicPlayButtonRenderer?.playNavigationEndpoint
                            ?.watchPlaylistEndpoint?.playlistId ?: return null,
                        title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                        artists = null,
                        year = renderer.subtitle?.runs?.lastOrNull()?.text?.toIntOrNull(),
                        thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        explicit = renderer.subtitleBadges?.find {
                            it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                        } != null
                    )
        }
    }
}

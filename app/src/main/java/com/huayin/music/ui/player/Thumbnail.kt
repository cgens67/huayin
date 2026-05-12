// File: huayin-main/app/src/main/java/com/huayin/music/ui/player/Thumbnail.kt
package com.huayin.music.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.C
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.huayin.music.LocalPlayerConnection
import com.huayin.music.constants.*
import com.huayin.music.ui.component.AppConfig
import com.huayin.music.utils.rememberEnumPreference
import com.huayin.music.utils.rememberPreference
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Thumbnail(
    sliderPositionProvider: () -> Long?,
    onOpenFullscreenLyrics: () -> Unit,
    modifier: Modifier = Modifier,
    isPlayerExpanded: Boolean = true,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val context = LocalContext.current

    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val error by playerConnection.error.collectAsState()

    val swipeThumbnail by rememberPreference(SwipeThumbnailKey, true)
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsState()
    val canSkipNext by playerConnection.canSkipNext.collectAsState()

    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.DEFAULT
    )

    val isAppleMusicStyle = playerBackground == PlayerBackgroundStyle.APPLE_MUSIC

    var thumbnailCornerRadius by remember { mutableStateOf(16f) }
    LaunchedEffect(Unit) {
        thumbnailCornerRadius = AppConfig.getThumbnailCornerRadius(context)
    }

    val timeline = playerConnection.player.currentTimeline
    val currentIndex = playerConnection.player.currentMediaItemIndex
    val shuffleModeEnabled = playerConnection.player.shuffleModeEnabled

    val previousMediaMetadata = if (swipeThumbnail && !timeline.isEmpty) {
        val index = timeline.getPreviousWindowIndex(currentIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
        if (index != C.INDEX_UNSET) playerConnection.player.getMediaItemAt(index) else null
    } else null

    val nextMediaMetadata = if (swipeThumbnail && !timeline.isEmpty) {
        val index = timeline.getNextWindowIndex(currentIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
        if (index != C.INDEX_UNSET) playerConnection.player.getMediaItemAt(index) else null
    } else null

    val currentMediaItem = playerConnection.player.currentMediaItem
    val mediaItems = listOfNotNull(previousMediaMetadata, currentMediaItem, nextMediaMetadata)

    Box(modifier = modifier) {

        AnimatedVisibility(
            visible = error == null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize().statusBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isAppleMusicStyle) {
                    Spacer(modifier = Modifier.height(24.dp))
                } else {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val size = maxWidth - PlayerHorizontalPadding * 2
                    val startIndex = if (previousMediaMetadata != null) 1 else 0

                    val pagerState = rememberPagerState(
                        initialPage = startIndex,
                        pageCount = { mediaItems.size }
                    )

                    // Snap back to the center page when the current media item changes
                    LaunchedEffect(currentMediaItem) {
                        pagerState.scrollToPage(if (previousMediaMetadata != null) 1 else 0)
                    }

                    LaunchedEffect(pagerState.settledPage) {
                        val currentStartIndex = if (previousMediaMetadata != null) 1 else 0
                        if (pagerState.settledPage != currentStartIndex) {
                            if (pagerState.settledPage < currentStartIndex && canSkipPrevious) {
                                playerConnection.player.seekToPreviousMediaItem()
                            } else if (pagerState.settledPage > currentStartIndex && canSkipNext) {
                                playerConnection.player.seekToNext()
                            } else {
                                pagerState.scrollToPage(currentStartIndex)
                            }
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = swipeThumbnail && isPlayerExpanded,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val item = mediaItems.getOrNull(page)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { onOpenFullscreenLyrics() },
                                        onDoubleTap = { offset ->
                                            if (offset.x < size.toPx() / 2) {
                                                playerConnection.player.seekBack()
                                            } else {
                                                playerConnection.player.seekForward()
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {

                            if (isAppleMusicStyle) {
                                // CARÁTULA OCULTA
                                Box(modifier = Modifier.size(size))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(size)
                                        .clip(RoundedCornerShape(thumbnailCornerRadius.dp * 2))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(item?.mediaMetadata?.artworkUri?.toString())
                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .networkCachePolicy(CachePolicy.ENABLED)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        var showSeekEffect by remember { mutableStateOf(false) }
        var seekDirection by remember { mutableStateOf("") }

        LaunchedEffect(showSeekEffect) {
            if (showSeekEffect) {
                delay(1000)
                showSeekEffect = false
            }
        }

        AnimatedVisibility(
            visible = showSeekEffect,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = seekDirection,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        }
    }
}
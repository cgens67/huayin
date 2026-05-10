package com.huayin.music.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.huayin.music.LocalDatabase
import com.huayin.music.LocalPlayerAwareWindowInsets
import com.huayin.music.LocalPlayerConnection
import com.huayin.music.R
import com.huayin.music.constants.AccountNameKey
import com.huayin.music.constants.InnerTubeCookieKey
import com.huayin.music.constants.ListThumbnailSize
import com.huayin.music.db.entities.Album
import com.huayin.music.db.entities.Artist
import com.huayin.music.db.entities.LocalItem
import com.huayin.music.db.entities.Playlist
import com.huayin.music.db.entities.Song
import com.huayin.music.extensions.togglePlayPause
import com.huayin.music.innertube.models.AlbumItem
import com.huayin.music.innertube.models.ArtistItem
import com.huayin.music.innertube.models.PlaylistItem
import com.huayin.music.innertube.models.SongItem
import com.huayin.music.innertube.models.WatchEndpoint
import com.huayin.music.innertube.models.YTItem
import com.huayin.music.innertube.utils.parseCookieString
import com.huayin.music.models.toMediaMetadata
import com.huayin.music.playback.queues.LocalAlbumRadio
import com.huayin.music.playback.queues.YouTubeAlbumRadio
import com.huayin.music.playback.queues.YouTubeQueue
import com.huayin.music.ui.component.ChipsRow
import com.huayin.music.ui.component.HideOnScrollFAB
import com.huayin.music.ui.component.LocalMenuState
import com.huayin.music.ui.component.NavigationTitle
import com.huayin.music.ui.component.shimmer.GridItemPlaceHolder
import com.huayin.music.ui.component.shimmer.ShimmerHost
import com.huayin.music.ui.component.shimmer.TextPlaceholder
import com.huayin.music.ui.menu.AlbumMenu
import com.huayin.music.ui.menu.ArtistMenu
import com.huayin.music.ui.menu.SongMenu
import com.huayin.music.ui.menu.YouTubeAlbumMenu
import com.huayin.music.ui.menu.YouTubeArtistMenu
import com.huayin.music.ui.menu.YouTubePlaylistMenu
import com.huayin.music.ui.menu.YouTubeSongMenu
import com.huayin.music.utils.rememberPreference
import com.huayin.music.viewmodels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactLocalItem(
    item: LocalItem,
    isActive: Boolean,
    isPlaying: Boolean,
    onNavigate: () -> Unit,
    onMenuShow: () -> Unit
) {
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(300),
        label = "overlayAlpha"
    )

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .width(88.dp)
            .combinedClickable(onClick = onNavigate, onLongClick = onMenuShow)
    ) {
        Box(
            modifier = Modifier
                .height(88.dp)
                .fillMaxWidth()
                .clip(if (item is Artist) CircleShape else RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (overlayAlpha > 0f) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f * overlayAlpha)),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                        null,
                        tint = Color.White.copy(alpha = overlayAlpha)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactYTGridItem(
    item: YTItem,
    isActive: Boolean,
    isPlaying: Boolean,
    onNavigate: () -> Unit,
    onMenuShow: () -> Unit
) {
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(300),
        label = "overlayAlpha"
    )

    Column(
        modifier = Modifier
            .width(88.dp)
            .combinedClickable(onClick = onNavigate, onLongClick = onMenuShow)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(if (item is ArtistItem) CircleShape else RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (overlayAlpha > 0f) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f * overlayAlpha)),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                        null,
                        tint = Color.White.copy(alpha = overlayAlpha)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if(item is ArtistItem) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val database = LocalDatabase.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val haptic = LocalHapticFeedback.current

    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val quickPicks by viewModel.quickPicks.collectAsState()
    val forgottenFavorites by viewModel.forgottenFavorites.collectAsState()
    val keepListening by viewModel.keepListening.collectAsState()
    val similarRecommendations by viewModel.similarRecommendations.collectAsState()
    val accountPlaylists by viewModel.accountPlaylists.collectAsState()
    val homePage by viewModel.homePage.collectAsState()
    val explorePage by viewModel.explorePage.collectAsState()

    val allLocalItems by viewModel.allLocalItems.collectAsState()
    val allYtItems by viewModel.allYtItems.collectAsState()

    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    val accountName by rememberPreference(AccountNameKey, "")
    val accountImageUrl by viewModel.accountImageUrl.collectAsState()
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) { "SAPISID" in parseCookieString(innerTubeCookie) }
    val url = if (isLoggedIn) accountImageUrl else null

    val scope = rememberCoroutineScope()
    val lazylistState = rememberLazyListState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop =
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()

    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            lazylistState.animateScrollToItem(0)
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh
            ),
        contentAlignment = Alignment.TopStart
    ) {

        LazyColumn(
            state = lazylistState,
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.systemBars.only(androidx.compose.foundation.layout.WindowInsetsSides.Horizontal))
                        .fillMaxWidth()
                        .animateItem()
                ) {
                    ChipsRow(
                        chips = listOfNotNull(
                            Pair("history", "历史流与统计"),
                            Pair("stats", "报告趋势统计"),
                            Pair("liked", "全部喜爱的"),
                            Pair("downloads", "缓存本地全库"),
                            if (isLoggedIn) Pair("account", "外接云归属库集") else null
                        ),
                        currentValue = "",
                        onValueUpdate = { value ->
                            when (value) {
                                "history" -> navController.navigate("history")
                                "stats" -> navController.navigate("stats")
                                "liked" -> navController.navigate("auto_playlist/liked")
                                "downloads" -> navController.navigate("auto_playlist/downloaded")
                                "account" -> if (isLoggedIn) navController.navigate("account")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }
            }

            // Quick Picks: Redesigned for verticality and proper edge padding
            quickPicks?.takeIf { it.isNotEmpty() }?.let { quickPicksList ->
                item {
                    NavigationTitle(
                        title = "即选发现灵感库 (发现选点)",
                        modifier = Modifier.animateItem()
                    )
                }

                item {
                    val carouselState = rememberCarouselState { quickPicksList.size }
                    HorizontalMultiBrowseCarousel(
                        state = carouselState,
                        preferredItemWidth = 186.dp, // Balanced width for taller layout
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 20.dp), // Prevents touching screen borders
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp) // Vertically longer as requested
                            .animateItem()
                    ) { index ->
                        val originalSong = quickPicksList[index]
                        val song by database.song(originalSong.id).collectAsState(initial = originalSong)
                        val isActive = song!!.id == mediaMetadata?.id

                        Card(
                            modifier = Modifier
                                .maskClip(RoundedCornerShape(24.dp)) // Correct way to handle M3 Carousel clipping
                                .fillMaxSize()
                                .combinedClickable(
                                    onClick = {
                                        if (isActive) playerConnection.player.togglePlayPause()
                                        else playerConnection.playQueue(YouTubeQueue.radio(song!!.toMediaMetadata()))
                                    },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        menuState.show {
                                            SongMenu(
                                                originalSong = song!!,
                                                navController = navController,
                                                onDismiss = menuState::dismiss
                                            )
                                        }
                                    }
                                ),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = song!!.song.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Dark gradient for text readability
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.6f)
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f),
                                                    Color.Black.copy(alpha = 0.9f)
                                                )
                                            )
                                        )
                                )

                                // Playing overlay
                                val overlayAlpha by animateFloatAsState(
                                    targetValue = if (isActive) 1f else 0f,
                                    animationSpec = tween(300),
                                    label = "overlayAlpha"
                                )
                                if (overlayAlpha > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f * overlayAlpha)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = overlayAlpha),
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }

                                // Song details overlaid
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = song!!.song.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            lineHeight = 18.sp
                                        ),
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = song!!.artists.joinToString { it.name },
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = Color.White.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Categories: Shrunk to 88.dp width to show more content
            keepListening?.takeIf { it.isNotEmpty() }?.let { keepList ->
                item { NavigationTitle(title = "不曾暂停续放队列") }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.animateItem()
                    ) {
                        items(keepList) { litem ->
                            CompactLocalItem(
                                item = litem,
                                isActive = litem.id == mediaMetadata?.id || litem.id == mediaMetadata?.album?.id,
                                isPlaying = isPlaying,
                                onNavigate = {
                                    when (litem) {
                                        is Song -> {
                                            if (litem.id == mediaMetadata?.id) playerConnection.player.togglePlayPause()
                                            else playerConnection.playQueue(YouTubeQueue.radio(litem.toMediaMetadata()))
                                        }
                                        is Album -> navController.navigate("album/${litem.id}")
                                        is Artist -> navController.navigate("artist/${litem.id}")
                                        else -> {}
                                    }
                                },
                                onMenuShow = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    menuState.show {
                                        when (litem) {
                                            is Song -> SongMenu(litem, navController = navController, onDismiss = menuState::dismiss)
                                            is Album -> AlbumMenu(litem, navController = navController, onDismiss = menuState::dismiss)
                                            is Artist -> ArtistMenu(litem, coroutineScope = scope, onDismiss = menuState::dismiss)
                                            else -> {}
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            accountPlaylists?.takeIf { it.isNotEmpty() }?.let { accountPlists ->
                item {
                    NavigationTitle(
                        label = "账号所建集合",
                        title = accountName,
                        thumbnail = {
                            if (url != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(url).crossfade(true).build(),
                                    placeholder = painterResource(id = R.drawable.person),
                                    error = painterResource(id = R.drawable.person),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(ListThumbnailSize).clip(CircleShape)
                                )
                            }
                        },
                        onClick = { navController.navigate("account") },
                        modifier = Modifier.animateItem()
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(accountPlists) {
                            CompactYTGridItem(
                                item = it,
                                isActive = it.id in listOf(mediaMetadata?.album?.id, mediaMetadata?.id),
                                isPlaying = isPlaying,
                                onNavigate = { navController.navigate("online_playlist/${it.id}") },
                                onMenuShow = {
                                    menuState.show { YouTubePlaylistMenu(it as com.huayin.music.innertube.models.PlaylistItem, coroutineScope= scope, onDismiss = menuState::dismiss) }
                                }
                            )
                        }
                    }
                }
            }

            similarRecommendations?.forEach { similarData ->
                item {
                    NavigationTitle(
                        label = "发掘相似音频指纹库选品特征向...",
                        title = similarData.title.title,
                        thumbnail = similarData.title.thumbnailUrl?.let { urlString ->
                            { AsyncImage(model=urlString, contentDescription=null, modifier=Modifier.size(48.dp).clip(if(similarData.title is Artist) CircleShape else RoundedCornerShape(8.dp))) }
                        },
                        onClick = {
                            when (val local = similarData.title) {
                                is Song -> navController.navigate("album/${local.album!!.id}")
                                is Album -> navController.navigate("album/${local.id}")
                                is Artist -> navController.navigate("artist/${local.id}")
                                is Playlist -> {}
                            }
                        }
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(similarData.items) { yi ->
                            CompactYTGridItem(
                                item = yi,
                                isActive = yi.id == mediaMetadata?.id || yi.id == mediaMetadata?.album?.id,
                                isPlaying = isPlaying,
                                onNavigate = {
                                    when(yi){
                                        is SongItem -> playerConnection.playQueue(YouTubeQueue(yi.endpoint?:WatchEndpoint(videoId=yi.id), yi.toMediaMetadata()))
                                        is AlbumItem -> navController.navigate("album/${yi.id}")
                                        is ArtistItem -> navController.navigate("artist/${yi.id}")
                                        is com.huayin.music.innertube.models.PlaylistItem -> navController.navigate("online_playlist/${yi.id}")
                                    }
                                },
                                onMenuShow = {
                                    menuState.show {
                                        when(yi){
                                            is SongItem -> YouTubeSongMenu(yi, navController, menuState::dismiss)
                                            is AlbumItem -> YouTubeAlbumMenu(yi, navController, menuState::dismiss)
                                            is ArtistItem -> YouTubeArtistMenu(yi, menuState::dismiss)
                                            is com.huayin.music.innertube.models.PlaylistItem -> YouTubePlaylistMenu(yi, coroutineScope = scope, onDismiss = menuState::dismiss)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            homePage?.sections?.forEach { sectionInfo ->
                item {
                    NavigationTitle(title = sectionInfo.title, label = sectionInfo.label)
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(sectionInfo.items) { itemVal ->
                            CompactYTGridItem(
                                item = itemVal,
                                isActive = itemVal.id == mediaMetadata?.id || itemVal.id == mediaMetadata?.album?.id,
                                isPlaying = isPlaying,
                                onNavigate = {
                                    when(itemVal){
                                        is SongItem -> playerConnection.playQueue(YouTubeQueue(itemVal.endpoint?:WatchEndpoint(videoId=itemVal.id), itemVal.toMediaMetadata()))
                                        is AlbumItem -> navController.navigate("album/${itemVal.id}")
                                        is ArtistItem -> navController.navigate("artist/${itemVal.id}")
                                        is com.huayin.music.innertube.models.PlaylistItem -> navController.navigate("online_playlist/${itemVal.id}")
                                    }
                                },
                                onMenuShow = {
                                    menuState.show {
                                        when(itemVal){
                                            is SongItem -> YouTubeSongMenu(itemVal, navController, menuState::dismiss)
                                            is AlbumItem -> YouTubeAlbumMenu(itemVal, navController, menuState::dismiss)
                                            is ArtistItem -> YouTubeArtistMenu(itemVal, menuState::dismiss)
                                            is com.huayin.music.innertube.models.PlaylistItem -> YouTubePlaylistMenu(itemVal, coroutineScope = scope, onDismiss = menuState::dismiss)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            explorePage?.newReleaseAlbums?.let { newR ->
                item { NavigationTitle(title="崭新呈现的全新唱盘特编专合辑合订专辑合...", onClick={navController.navigate("new_release")}) }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(newR) { al->
                            CompactYTGridItem(
                                item = al,
                                isActive = mediaMetadata?.album?.id == al.id,
                                isPlaying = isPlaying,
                                onNavigate = { navController.navigate("album/${al.id}") },
                                onMenuShow = { menuState.show { YouTubeAlbumMenu(al, navController, menuState::dismiss) } }
                            )
                        }
                    }
                }
            }

            forgottenFavorites?.takeIf { it.isNotEmpty() }?.let { favList ->
                item { NavigationTitle(title = "从流迹打碎的回响遗存收集列表") }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(favList){ flocal ->
                            val sval by database.song(flocal.id).collectAsState(initial = flocal)
                            sval?.let { resolved -> 
                                CompactLocalItem(
                                    item = resolved,
                                    isActive = resolved.id == mediaMetadata?.id,
                                    isPlaying = isPlaying,
                                    onNavigate = {
                                        if (resolved.id == mediaMetadata?.id) playerConnection.player.togglePlayPause()
                                        else playerConnection.playQueue(YouTubeQueue.radio(resolved.toMediaMetadata()))
                                    },
                                    onMenuShow = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        menuState.show { SongMenu(resolved, navController = navController, onDismiss = menuState::dismiss) }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    ShimmerHost {
                        TextPlaceholder(
                            height = 36.dp,
                            modifier = Modifier.padding(12.dp).width(250.dp),
                        )
                        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                            items(4) { GridItemPlaceHolder(modifier=Modifier.width(88.dp), fillMaxWidth=true) }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(300.dp)) }
        }

        HideOnScrollFAB(
            visible = allLocalItems.isNotEmpty() || allYtItems.isNotEmpty(),
            lazyListState = lazylistState,
            icon = R.drawable.shuffle,
            onClick = {
                val local = when {
                    allLocalItems.isNotEmpty() && allYtItems.isNotEmpty() -> Random.nextFloat() < 0.5
                    allLocalItems.isNotEmpty() -> true
                    else -> false
                }
                scope.launch(Dispatchers.Main) {
                    if (local) {
                        when (val luckyItem = allLocalItems.random()) {
                            is Song -> playerConnection.playQueue(YouTubeQueue.radio(luckyItem.toMediaMetadata()))
                            is Album -> {
                                val albumWithSongs = withContext(Dispatchers.IO) {
                                    database.albumWithSongs(luckyItem.id).first()
                                }
                                albumWithSongs?.let { playerConnection.playQueue(LocalAlbumRadio(it)) }
                            }
                            else -> {}
                        }
                    } else {
                        when (val luckyItem = allYtItems.random()) {
                            is SongItem -> playerConnection.playQueue(YouTubeQueue.radio(luckyItem.toMediaMetadata()))
                            is AlbumItem -> playerConnection.playQueue(YouTubeAlbumRadio(luckyItem.playlistId))
                            is ArtistItem -> luckyItem.radioEndpoint?.let { playerConnection.playQueue(YouTubeQueue(it)) }
                            is PlaylistItem -> luckyItem.playEndpoint?.let { playerConnection.playQueue(YouTubeQueue(it)) }
                        }
                    }
                }
            }
        )

        Indicator(
            isRefreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter).padding(LocalPlayerAwareWindowInsets.current.asPaddingValues()),
        )
    }
}
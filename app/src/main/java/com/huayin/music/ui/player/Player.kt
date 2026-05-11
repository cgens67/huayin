package com.huayin.music.ui.player

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.huayin.music.LocalPlayerConnection
import com.huayin.music.R
import com.huayin.music.constants.*
import com.huayin.music.extensions.togglePlayPause
import com.huayin.music.extensions.toggleRepeatMode
import com.huayin.music.models.MediaMetadata
import com.huayin.music.ui.component.*
import com.huayin.music.ui.menu.PlayerMenu
import com.huayin.music.utils.makeTimeString
import com.huayin.music.utils.rememberEnumPreference
import com.huayin.music.utils.rememberPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomSheetPlayer(
    state: BottomSheetState,
    navController: NavController,
    onOpenFullscreenLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val playbackState by playerConnection.playbackState.collectAsState()
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val repeatMode by playerConnection.repeatMode.collectAsState()
    val isShuffleEnabled by playerConnection.shuffleModeEnabled.collectAsState()

    var position by rememberSaveable(playbackState) { mutableLongStateOf(playerConnection.player.currentPosition) }
    var duration by rememberSaveable(playbackState) { mutableLongStateOf(playerConnection.player.duration) }
    var sliderPosition by remember { mutableStateOf<Long?>(null) }

    val sleepTimerEnabled = playerConnection.service.sleepTimer.isActive

    LaunchedEffect(playbackState) {
        if (playbackState == Player.STATE_READY) {
            while (isActive) {
                delay(100)
                position = playerConnection.player.currentPosition
                duration = playerConnection.player.duration
            }
        }
    }

    BottomSheet(
        state = state,
        modifier = modifier,
        background = {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
        },
        collapsedContent = { MiniPlayer(position = position, duration = duration) },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Vertical))
        ) {
            // 1. HEADER TITLE
            Spacer(Modifier.height(8.dp))
            Text(
                text = "正在播放",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            mediaMetadata?.let {
                Text(
                    text = it.title.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(24.dp))

            // 2. ALBUM ARTWORK
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Thumbnail(
                    sliderPositionProvider = { sliderPosition },
                    onOpenFullscreenLyrics = onOpenFullscreenLyrics,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(28.dp))

            // 3. TITLE / ARTIST + ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mediaMetadata?.title ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mediaMetadata?.artists?.joinToString { it.name } ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallCircularAction(R.drawable.share) { /* Share Logic */ }
                    SmallCircularAction(
                        icon = if (playerConnection.isLiked.collectAsState().value) R.drawable.favorite else R.drawable.favorite_border,
                        tint = if (playerConnection.isLiked.collectAsState().value) MaterialTheme.colorScheme.error else null
                    ) { playerConnection.toggleLike() }
                    SmallCircularAction(R.drawable.more_horiz) {
                        menuState.show {
                            PlayerMenu(mediaMetadata, navController, state, onShowDetailsDialog = {}, onDismiss = { menuState.dismiss() })
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 4. CUSTOM SEEK BAR
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Vertical Bar at the start
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(24.dp)
                            .background(Color.Black, RoundedCornerShape(2.dp))
                    )

                    Slider(
                        value = (sliderPosition ?: position).toFloat(),
                        valueRange = 0f..(if (duration <= 0) 1f else duration.toFloat()),
                        onValueChange = { sliderPosition = it.toLong() },
                        onValueChangeFinished = {
                            sliderPosition?.let { playerConnection.player.seekTo(it) }
                            sliderPosition = null
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.DarkGray,
                            activeTrackColor = Color.DarkGray,
                            inactiveTrackColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color.Black, CircleShape)
                            )
                        },
                        track = { sliderState ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .padding(start = 6.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (duration > 0) (position.toFloat() / duration.toFloat()) else 0f)
                                        .fillMaxHeight()
                                        .background(Color.LightGray)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(makeTimeString(position), style = MaterialTheme.typography.labelMedium)
                    Text(makeTimeString(duration), style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(Modifier.height(32.dp))

            // 5. MAIN CONTROLS (Expressive Row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                ControlIcon(R.drawable.shuffle, isShuffleEnabled) { playerConnection.toggleShuffle() }
                
                // Previous
                ControlIcon(R.drawable.skip_previous, size = 48.dp) { playerConnection.seekToPrevious() }

                // Play/Pause (Large Black Square)
                Surface(
                    onClick = { playerConnection.player.togglePlayPause() },
                    modifier = Modifier.size(82.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Black
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }

                // Next
                ControlIcon(R.drawable.skip_next, size = 48.dp) { playerConnection.seekToNext() }

                // Repeat
                ControlIcon(
                    icon = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                        else -> R.drawable.repeat
                    },
                    active = repeatMode != Player.REPEAT_MODE_OFF
                ) { playerConnection.player.toggleRepeatMode() }
            }

            Spacer(Modifier.height(48.dp))

            // 6. BOTTOM QUICK ACTION PILLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionPill(
                    icon = R.drawable.queue_music,
                    label = "队列",
                    modifier = Modifier.weight(1.2f)
                ) { state.expandSoft() }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { /* Sleep Timer Dialog */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(R.drawable.bedtime), null, modifier = Modifier.size(24.dp))
                }

                ActionPill(
                    icon = R.drawable.lyrics,
                    label = "歌词",
                    modifier = Modifier.weight(1.2f)
                ) { onOpenFullscreenLyrics() }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SmallCircularAction(icon: Int, tint: Color? = null, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(42.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = tint ?: MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ControlIcon(icon: Int, active: Boolean = false, size: androidx.compose.ui.unit.Dp = 42.dp, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ActionPill(icon: Int, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(painterResource(icon), null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}
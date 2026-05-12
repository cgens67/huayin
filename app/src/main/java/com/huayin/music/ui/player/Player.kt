package com.huayin.music.ui.player

import android.content.Intent
import androidx.compose.foundation.*
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.media3.common.Player
import androidx.navigation.NavController
import com.huayin.music.LocalPlayerConnection
import com.huayin.music.R
import com.huayin.music.constants.SmallButtonsShapeKey
import com.huayin.music.constants.PlayPauseButtonShapeKey
import com.huayin.music.constants.DefaultSmallButtonsShape
import com.huayin.music.constants.DefaultPlayPauseButtonShape
import com.huayin.music.extensions.togglePlayPause
import com.huayin.music.extensions.toggleRepeatMode
import com.huayin.music.ui.component.*
import com.huayin.music.ui.menu.PlayerMenu
import com.huayin.music.utils.getSmallButtonShape
import com.huayin.music.utils.getPlayPauseShape
import com.huayin.music.utils.makeTimeString
import com.huayin.music.utils.rememberPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

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
    val isLiked by playerConnection.isLiked.collectAsState()

    var position by rememberSaveable(playbackState) { mutableLongStateOf(playerConnection.player.currentPosition) }
    var duration by rememberSaveable(playbackState) { mutableLongStateOf(playerConnection.player.duration) }
    var sliderPosition by remember { mutableStateOf<Long?>(null) }

    val sleepTimerEnabled = playerConnection.service.sleepTimer.isActive
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var sleepTimerValue by remember { mutableFloatStateOf(30f) }

    val smallButtonsShapeState by rememberPreference(SmallButtonsShapeKey, DefaultSmallButtonsShape)
    val playPauseShapeState by rememberPreference(PlayPauseButtonShapeKey, DefaultPlayPauseButtonShape)
    
    // Extracted out of the remember block because toShape() is a composable call!
    val smallButtonsPolygon = remember(smallButtonsShapeState) { getSmallButtonShape(smallButtonsShapeState) }
    val smallButtonsShape = smallButtonsPolygon.toShape()
    
    val playPausePolygon = remember(playPauseShapeState) { getPlayPauseShape(playPauseShapeState) }
    val playPauseShape = playPausePolygon.toShape()

    LaunchedEffect(playbackState) {
        if (playbackState == Player.STATE_READY) {
            while (isActive) {
                delay(100)
                position = playerConnection.player.currentPosition
                duration = playerConnection.player.duration
            }
        }
    }

    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            icon = { Icon(painterResource(R.drawable.bedtime), null) },
            title = { Text(stringResource(R.string.sleep_timer)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSleepTimerDialog = false
                        playerConnection.service.sleepTimer.start(sleepTimerValue.roundToInt())
                    },
                ) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) { Text(stringResource(android.R.string.cancel)) }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = pluralStringResource(R.plurals.minute, sleepTimerValue.roundToInt(), sleepTimerValue.roundToInt()))
                    Slider(value = sleepTimerValue, onValueChange = { sleepTimerValue = it }, valueRange = 5f..120f, steps = 22)
                }
            },
        )
    }

    BoxWithConstraints(modifier = modifier) {
        val maxHeight = maxHeight
        val queueBottomSheetState = rememberBottomSheetState(
            dismissedBound = 0.dp,
            expandedBound = maxHeight,
        )

        BottomSheet(
            state = state,
            onDismiss = {
                playerConnection.player.stop()
                playerConnection.player.clearMediaItems()
            },
            background = {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
            },
            collapsedContent = { MiniPlayer(position = position, duration = duration) },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(queueBottomSheetState.preUpPostDownNestedScrollConnection) // Fixed Nested Scrolling
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
                val topTitle = playerConnection.queueTitle.collectAsState().value ?: mediaMetadata?.album?.title ?: ""
                Text(
                    text = topTitle.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
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
                        SmallCircularAction(R.drawable.share) {
                            mediaMetadata?.let {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "https://music.youtube.com/watch?v=${it.id}")
                                }
                                context.startActivity(Intent.createChooser(intent, null))
                            }
                        }
                        SmallCircularAction(
                            icon = if (isLiked) R.drawable.favorite else R.drawable.favorite_border,
                            tint = if (isLiked) MaterialTheme.colorScheme.error else null
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
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Vertical Bar at the start
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(24.dp)
                                .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(4.dp))
                        Slider(
                            value = (sliderPosition ?: position).toFloat(),
                            valueRange = 0f..(if (duration <= 0) 1f else duration.toFloat()),
                            onValueChange = { sliderPosition = it.toLong() },
                            onValueChangeFinished = {
                                sliderPosition?.let { playerConnection.player.seekTo(it) }
                                sliderPosition = null
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.onSurface,
                                activeTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(makeTimeString(sliderPosition ?: position), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(makeTimeString(duration), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(32.dp))

                // 5. MAIN CONTROLS
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ControlIcon(R.drawable.shuffle, isShuffleEnabled, shape = smallButtonsShape) { playerConnection.toggleShuffle() }
                    
                    ControlIcon(R.drawable.skip_previous, size = 52.dp, shape = smallButtonsShape) { playerConnection.seekToPrevious() }

                    // Play/Pause (Dynamic Shape)
                    Surface(
                        onClick = { playerConnection.player.togglePlayPause() },
                        modifier = Modifier.size(80.dp),
                        shape = playPauseShape,
                        color = MaterialTheme.colorScheme.onSurface
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    }

                    ControlIcon(R.drawable.skip_next, size = 52.dp, shape = smallButtonsShape) { playerConnection.seekToNext() }

                    ControlIcon(
                        icon = when (repeatMode) {
                            Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                            else -> R.drawable.repeat
                        },
                        active = repeatMode != Player.REPEAT_MODE_OFF,
                        shape = smallButtonsShape
                    ) { playerConnection.player.toggleRepeatMode() }
                }

                Spacer(Modifier.height(48.dp))

                // 6. BOTTOM QUICK ACTION PILLS
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionPill(
                        icon = R.drawable.queue_music,
                        label = stringResource(R.string.queue),
                        modifier = Modifier.weight(1.2f)
                    ) { queueBottomSheetState.expandSoft() }

                    Surface(
                        onClick = { showSleepTimerDialog = true },
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = if (sleepTimerEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.bedtime), 
                                contentDescription = null, 
                                modifier = Modifier.size(24.dp),
                                tint = if (sleepTimerEnabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    ActionPill(
                        icon = R.drawable.lyrics,
                        label = stringResource(R.string.lyrics),
                        modifier = Modifier.weight(1.2f)
                    ) { onOpenFullscreenLyrics() }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
        
        Queue(
            state = queueBottomSheetState,
            playerBottomSheetState = state,
            navController = navController,
            backgroundColor = MaterialTheme.colorScheme.surface,
            onBackgroundColor = MaterialTheme.colorScheme.onSurface,
            textBackgroundColor = MaterialTheme.colorScheme.onSurface,
        )
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
fun ControlIcon(icon: Int, active: Boolean = false, size: Dp = 46.dp, shape: androidx.compose.ui.graphics.Shape = CircleShape, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(size),
        shape = shape,
        color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
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
            Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
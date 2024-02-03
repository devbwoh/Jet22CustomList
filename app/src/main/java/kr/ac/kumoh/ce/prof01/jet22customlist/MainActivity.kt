package kr.ac.kumoh.ce.prof01.jet22customlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kr.ac.kumoh.ce.prof01.jet22customlist.ui.theme.Jet22CustomListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Jet22CustomListTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SongScreen()
    }
}

@Composable
fun SongScreen() {
    var dialogOpened by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        floatingActionButton = {
            ButtonSongAdd {
                dialogOpened = true
            }
        }
    ) { paddingValues ->
        SongList(paddingValues)

        // 여러 개의 Composable이 state에 따라 보일지 말지 결정할 때 사용 권장
        when {
            dialogOpened -> {
                DialogSongAdd {
                    dialogOpened = false
                }
            }
        }
    }
}

@Composable
fun SongList(paddingValues: PaddingValues) {
    val viewModel: SongViewModel = viewModel()
    val songs by viewModel.songs.collectAsState()

    LazyColumn(
        Modifier.padding(paddingValues),
    ) {
        items(
            items = songs,
            key = { it.id },
        ) {
            SongItem(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(song: Song) {
    val viewModel: SongViewModel = viewModel()

    var deleted by remember {
        mutableStateOf(false)
    }
    val state = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                // DONE: 데이터베이스에서 삭제
                viewModel.delete(song.id)
                deleted = true
            }
            it == DismissValue.DismissedToStart
        }
    )

    var expanded by remember {
        mutableStateOf(false)
    }

    SwipeToDismiss(
        state = state,
        background = {
            // state.dismissDirection으로 방향 확인 가능
            //      DismissDirection.StartToEnd
            //      DismissDirection.EndToStart
            val color = when(state.dismissDirection) {
                DismissDirection.EndToStart -> MaterialTheme.colorScheme.secondaryContainer
                else -> Color.Transparent
            }

            Box(
                Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                    .background(color),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    "삭제 아이콘",
                )
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                onClick = {
                    expanded = !expanded
                }
            ) {
                Column(
                    Modifier.padding(8.dp)
                ) {
                    SongTitle(song.title)
                    AnimatedVisibility(visible = expanded) {
                        //SingerName(song.singer)
                        ExpandedSongContent(song)
                    }
                }
            }
        }
    )
}

@Composable
fun ExpandedSongContent(song: Song) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = "https://picsum.photos/300/300?random=${song.singer}",
            contentDescription = "가수 (더미) 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            SingerName(song.singer)
        }
        IconButton(
            onClick = {
                val uri = Uri.parse("https://www.youtube.com/results?"
                        + "search_query=노래방+${song.title}+${song.singer}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(context, intent, null)
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "노래방 검색 아이콘",
                tint = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
fun SongTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 20.sp,
        lineHeight = 25.sp,
    )
}

@Composable
fun SingerName(name: String) {
    Text(name)
}

@Composable
fun ButtonSongAdd(onOpenDialog: () -> Unit) {
    FloatingActionButton(
        onClick = { onOpenDialog()  },
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "추가 버튼",
        )
    }
}

@Composable
fun DialogSongAdd(onCloseDialog: () -> Unit) {
    val viewModel: SongViewModel = viewModel()
    var title by remember {
        mutableStateOf("")
    }
    var singer by remember {
        mutableStateOf("")
    }

    AlertDialog(
        title = {
            Text("노래 추가")
        },
        text = {
            Column {
                TextField(
                    value = title,
                    label = { Text("노래 제목") },
                    onValueChange = {
                        title = it
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Favorite,
                            "노래 아이콘"
                        )
                    },
                    maxLines = 3,
                )
                Spacer(Modifier.size(16.dp))
                TextField(
                    value = singer,
                    label = { Text("가수") },
                    onValueChange = {
                        singer = it
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            "노래 아이콘"
                        )
                    },
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // DONE: 데이터베이스에 삽입
                    viewModel.add(title, singer)
                    onCloseDialog()
                }
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onCloseDialog() }
            ) {
                Text("취소")
            }
        },
        onDismissRequest = {
            onCloseDialog()
        },
    )
}
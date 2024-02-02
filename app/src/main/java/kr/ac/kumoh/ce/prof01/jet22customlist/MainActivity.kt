package kr.ac.kumoh.ce.prof01.jet22customlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.ac.kumoh.ce.prof01.jet22customlist.ui.theme.Jet22CustomListTheme
import java.util.UUID

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
    var dialogOpened by remember {
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

//        if (dialogOpened) {
//            DialogSongAdd {
//                dialogOpened = false
//            }
//        }
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
    LazyColumn(
        Modifier.padding(paddingValues)
    ) {
        items(30) {
            SongItem()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem() {
    var deleted by remember {
        mutableStateOf(false)
    }

    val state = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                // TODO: 데이터베이스에서 삭제
                deleted = true
            }
            it == DismissValue.DismissedToStart
        }
    )

    var expanded by remember {
        mutableStateOf(false)
    }

    val title = UUID.randomUUID().toString()
    val singer = UUID.randomUUID().toString()

    SwipeToDismiss(
        state = state,
        background = {
            val color = when(state.dismissDirection) {
                DismissDirection.EndToStart ->
                    MaterialTheme.colorScheme.secondaryContainer
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
                    "삭제 아이콘"
                )
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                onClick = { expanded = !expanded },
            ) {
                Column(
                    Modifier.padding(8.dp)
                ) {
                    SongTitle(title)
                    AnimatedVisibility(visible = expanded) {
                        SingerName(singer)
                    }
                }
            }
        }
    )
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
        onClick = { onOpenDialog() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "추가 버튼"
        )
    }
}

@Composable
fun DialogSongAdd(onCloseDialog: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var singer by remember { mutableStateOf("") }

    AlertDialog(
        title = { Text("노래 추가") },
        text = {
            Column {
                TextField(
                    value = title,
                    label = { Text("노래 제목") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Favorite,
                            "노래 아이콘"
                        )
                    },
                    maxLines = 3,
                    onValueChange = { title = it }
                )
                Spacer(modifier = Modifier.size(16.dp))
                TextField(
                    value = singer,
                    label = { Text("가수") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            "가수 아이콘"
                        )
                    },
                    maxLines = 3,
                    onValueChange = { singer = it }
                )
            }
        },
        onDismissRequest = {
            onCloseDialog()
        },
        confirmButton = {
            Button(
                onClick = {
                    // TODO: 데이터베이스에 삽입
                    onCloseDialog()
                }
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(onClick = { onCloseDialog() }) {
                Text("취소")
            }
        }
    )
}
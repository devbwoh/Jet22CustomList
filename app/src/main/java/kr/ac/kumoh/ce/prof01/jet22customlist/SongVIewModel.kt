package kr.ac.kumoh.ce.prof01.jet22customlist

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel

data class Song(
    val id: String,
    val title: String,
    val singer: String,
)

class SongViewModel(application: Application) : AndroidViewModel(application) {
    private val _songs = mutableStateListOf<Song>()
    val songs: List<Song>
        get() = _songs

    init {
        add("소주 한 잔", "임창정")
    }

    fun add(title: String, singer: String) {
        _songs.add(
            Song(
                _songs.size.toString(),
                title,
                singer
            )
        )
    }

    fun delete(id: String) {
        _songs.remove(_songs.find { id == it.id })
    }
}
package kr.ac.kumoh.ce.prof01.jet22customlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.ac.kumoh.ce.prof01.jet22customlist.pocketbase.SongPocketRepository

data class Song(
    val id: String,
    val title: String,
    val singer: String,
)

class SongViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var repository: SongRepository

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>>
        get() = _songs

    init {
        setRepository(SongPocketRepository)
        select()
    }

    fun setRepository(repo: SongRepository) {
        repository = repo
        repository.initRepository(getApplication())
        select()
    }

    fun select() {
        viewModelScope.launch(Dispatchers.IO) {
            _songs.value = repository.select()
        }
    }

    fun add(title: String, singer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(title, singer)
            _songs.value = repository.select()
        }
    }

    fun delete(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(id)
            _songs.value = repository.select()
        }
    }
}
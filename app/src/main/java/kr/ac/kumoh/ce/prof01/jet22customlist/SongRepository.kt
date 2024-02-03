package kr.ac.kumoh.ce.prof01.jet22customlist

import android.content.Context

interface SongRepository {
    fun initRepository(context: Context)
    suspend fun select(): List<Song>
    suspend fun insert(title: String, singer: String)
    suspend fun delete(id: String)
}
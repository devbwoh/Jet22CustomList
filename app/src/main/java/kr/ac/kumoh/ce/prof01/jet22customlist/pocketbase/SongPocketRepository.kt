package kr.ac.kumoh.ce.prof01.jet22customlist.pocketbase

import android.content.Context
import android.util.Log
import kr.ac.kumoh.ce.prof01.jet22customlist.SongRepository
import kr.ac.kumoh.ce.prof01.jet22customlist.Song
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class SongResponse(
    val page: Int,
    val perPage: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<Song>,
)

interface SongApi {
    @GET("api/collections/song/records?fields=id,title,singer")
    suspend fun getSongs(): SongResponse

    @POST("api/collections/song/records")
    suspend fun insertSong(@Body params: HashMap<String, String>)

    @DELETE("api/collections/song/records/{id}")
    suspend fun deleteSong(@Path("id") id: String): Int
}

object SongPocketRepository : SongRepository {
    //private const val serverUrl = "https://[본인의 서버].pockethost.io/"
    private const val serverUrl = "https://jetpack-server.pockethost.io/"
    private lateinit var songApi: SongApi

    override fun initRepository(context: Context) {
        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        songApi = retrofit.create(SongApi::class.java)
    }

    override suspend fun select(): List<Song> {
        lateinit var response: SongResponse

        try {
            response = songApi.getSongs()
            Log.i("SongPocketRepository::select()", response.toString())
        } catch (e: Exception) {
            Log.e("Error SongPocketRepository::select()", e.toString())
            return emptyList()
        }

        return response.items
    }

    override suspend fun insert(title: String, singer: String) {
        val body = hashMapOf(
            "title" to title,
            "singer" to singer
        )

        songApi.insertSong(body)
    }

    override suspend fun delete(id: String) {
        try {
            songApi.deleteSong(id)
        } catch (e: Exception) {
            Log.e("SongPocketRepository::delete()", e.toString())
        }
    }
}
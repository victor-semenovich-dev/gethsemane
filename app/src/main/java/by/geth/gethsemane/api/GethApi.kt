package by.geth.gethsemane.api

import by.geth.gethsemane.api.response.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GethApi {
    @GET("/News")
    fun getNewsList(@Query("page") page: Int, @Query("limit") limit: Int):
            Call<List<NewsListItemResponse>>

    @GET("/News/{id}")
    fun getNewsDetails(@Path("id") id: Long): Call<NewsDetailsResponse>

    @GET("/Pages/WorshipNotes")
    fun getWorshipNotesList(@Query("page") page: Int, @Query("limit") limit: Int):
            Call<List<ArticleListItemResponse>>

    @GET("/Pages/SeeChristArticles")
    fun getToSeeChristList(@Query("page") page: Int, @Query("limit") limit: Int):
            Call<List<ArticleListItemResponse>>

    @GET("/Pages/{id}")
    fun getWorshipNotesDetails(@Path("id") id: Long): Call<ArticleDetailsResponse>

    @GET("/MusicGroups")
    fun getMusicGroups(): Call<List<MusicGroupResponse>>

    @GET("/MusicGroups/{id}")
    fun getMusicGroup(@Path("id") id: Long): Call<MusicGroupResponse>

    @GET("/Sermons")
    suspend fun getSermons(
        @Query("category") category: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String?,
    ): List<SermonResponse>
}
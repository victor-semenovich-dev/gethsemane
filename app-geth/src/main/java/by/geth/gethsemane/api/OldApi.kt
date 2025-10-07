package by.geth.gethsemane.api

import by.geth.gethsemane.api.response.BirthdaysResponse
import by.geth.gethsemane.data.Song
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OldApi {
    @GET("/birthdays")
    fun getBirthdays(): Call<List<BirthdaysResponse>>

    @GET("/mobile/songs/{groupId}")
    fun getSongs(@Path("groupId") groupId: Long): Call<List<Song>>
}
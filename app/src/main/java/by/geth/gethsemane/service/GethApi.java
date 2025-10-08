package by.geth.gethsemane.service;

import java.util.List;

import by.geth.gethsemane.data.Album;
import by.geth.gethsemane.data.Author;
import by.geth.gethsemane.data.Category;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.data.Sermon;
import by.geth.gethsemane.data.Witness;
import by.geth.gethsemane.data.Worship;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GethApi {
    @GET("/sermoners")
    Call<List<Author>> getAuthorList();

    @GET("/sermoners")
    Call<List<Author>> getAuthor(@Query("id") long id);

    @GET("/mobile/worship/{id}")
    Call<Worship> getWorship(@Path("id") long id);

    @GET("/events")
    Call<List<by.geth.gethsemane.data.Event>> getEvents();

    @GET("/events")
    Call<List<by.geth.gethsemane.data.Event>> getEvents(@Query("date") String from);

    @GET("/mobile/sermons")
    Call<List<Sermon>> getSermonsList();

    @GET("/mobile/sermons")
    Call<List<Sermon>> getSermonsList(@Query("from_date") String from);

    @GET("/mobile/sermon/{id}")
    Call<Sermon> getSermon(@Path("id") long id);

    @GET("/mobile/witnesses")
    Call<List<Witness>> getWitnessesList();

    @GET("/mobile/witnesses")
    Call<List<Witness>> getWitnessesList(@Query("from_date") String from);

    @GET("/mobile/witness/{id}")
    Call<Witness> getWitness(@Path("id") long id);

    @GET("/categories")
    Call<List<Category>> getCategoryList();

    @GET("/categories")
    Call<List<Category>> getCategoryList(@Query("type") String type);

    @GET("/albums")
    Call<List<Album>> getAlbumList(@Query("category") long categoryId);

    @GET("/album/{id}")
    Call<Album> getAlbum(@Path("id") long id);

    @GET("/photos")
    Call<List<Photo>> getPhotoList(@Query("album") long albumId);

    @GET("/photos")
    Call<List<Photo>> getPhotoList(@Query("ids") String ids);

    @GET("/last-photos")
    Call<ResponseBody> getRecentPhotoList(@Query("page") int page, @Query("per_page") int perPage);
}

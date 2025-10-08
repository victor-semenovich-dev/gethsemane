package by.geth.gethsemane.api

import android.os.Handler
import android.os.Looper
import android.util.Base64
import by.geth.gethsemane.BuildConfig
import by.geth.gethsemane.data.MusicGroup
import by.geth.gethsemane.data.Song
import by.geth.gethsemane.data.base.merge
import by.geth.gethsemane.data.model.Birthday
import by.geth.gethsemane.data.model.articles.ArticleDetails
import by.geth.gethsemane.data.model.articles.ArticleListItem
import by.geth.gethsemane.data.model.news.NewsDetails
import by.geth.gethsemane.data.model.news.NewsListItem
import by.geth.gethsemane.util.JsonDateDeserializer
import com.activeandroid.ActiveAndroid
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Modifier
import java.util.Date
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object Server {
    private const val HTTP_CODE_OK = 200
    private const val WHAT_STARTED = 0
    private const val WHAT_COMPLETED = 1

    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    private const val KEEP_ALIVE_TIME = 1L
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS

    private val workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()
    private val threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
            NUMBER_OF_CORES,       // Initial pool size
            NUMBER_OF_CORES,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue
    )
    private val handler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            WHAT_STARTED -> {
                val request = it.obj as BaseRequest
                callbackList.forEach { callback -> callback.onStarted(request) }
            }
            WHAT_COMPLETED -> {
                val obj = it.obj as HandlerMessageObj<*>
                requestList.remove(obj.request)
                callbackList.forEach { callback ->
                    when {
                        obj.result != null -> callback.onSuccess(obj.request, obj.result)
                        obj.code == HTTP_CODE_OK -> callback.onSuccess(obj.request, obj.result)
                        obj.exception == null -> callback.onFailure(obj.request, obj.code!!, obj.message!!)
                        else -> callback.onFailure(obj.request, obj.exception)
                    }
                }
            }
        }
        true
    }

    interface Callback {
        fun onStarted(request: BaseRequest) {}
        fun onSuccess(request: BaseRequest, result: Any?) {}
        fun onFailure(request: BaseRequest, code: Int, message: String) {}
        fun onFailure(request: BaseRequest, t: Throwable) {}
    }

    open class SimpleCallback: Callback

    class PageResult<T> (
        val loadedItems: List<T>,
        val allItems: List<T>
    )

    private val requestList = ArrayList<BaseRequest>()
    val api: GethApi
    private val oldApi: OldApi

    private val callbackList = ArrayList<Callback>()

    init {
        api = createGethApi()
        oldApi = createOldGethApi()
    }

    //----------------------------------------------------------------------------------
    //
    // Public methods
    //
    //----------------------------------------------------------------------------------
    fun addCallback(callback: Callback) { callbackList.add(callback) }

    fun removeCallback(callback: Callback) { callbackList.remove(callback) }

    fun isRunning(request: BaseRequest) = requestList.contains(request)

    fun getBirthdays(isSync: Boolean = false): List<Birthday>? {
        return performRequest(GetBirthdaysRequest(), oldApi.getBirthdays(), isSync) { responseList ->
            val newBirthdaysList = ArrayList<Birthday>()
            responseList.forEach {
                newBirthdaysList.add(it.dbEntity)
            }
            ActiveAndroid.beginTransaction()
            Delete().from(Birthday::class.java).execute<Birthday>()
            newBirthdaysList.forEach { it.save() }
            ActiveAndroid.setTransactionSuccessful()
            ActiveAndroid.endTransaction()
            newBirthdaysList
        }
    }

    fun getNewsList(page: Int = 1, limit: Int = 20, isSync: Boolean = false): PageResult<NewsListItem>? {
        return performRequest(GetNewsListRequest(page, limit), api.getNewsList(page, limit), isSync) { responseItemList ->
            val newItemList = ArrayList<NewsListItem>()
            responseItemList.forEach { newItemList.add(it.dbEntity) }
            merge(NewsListItem::class.java, newItemList, page)
            PageResult(newItemList, Select().from(NewsListItem::class.java).execute())
        }
    }

    fun getNewsDetails(id: Long, isSync: Boolean = false): NewsDetails? {
        return performRequest(GetNewsRequest(id), api.getNewsDetails(id), isSync) {
            val newItem = it.dbEntity
            val oldItem = Select().from(NewsDetails::class.java)
                    .where("${NewsDetails.COLUMN_EXTERNAL_ID} = ${newItem.externalId}")
                    .executeSingle<NewsDetails>()
            if (oldItem != null) {
                oldItem.updateWith(newItem)
                oldItem.save()
                oldItem
            } else {
                newItem.save()
                newItem
            }
        }
    }

    fun getWorshipNotesList(page: Int = 1, limit: Int = 20, isSync: Boolean = false): PageResult<ArticleListItem>? {
        return performRequest(GetWorshipNotesListRequest(page, limit), api.getWorshipNotesList(page, limit), isSync) {
            responseItemList ->
            val newItemList = ArrayList<ArticleListItem>()
            responseItemList.forEach { newItemList.add(it.dbEntity.apply {
                category = ArticleListItem.CATEGORY_WORSHIP_NOTES })
            }
            merge(ArticleListItem::class.java, newItemList, page, Select().from(ArticleListItem::class.java)
                .where("${ArticleListItem.COLUMN_CATEGORY} = '${ArticleListItem.CATEGORY_WORSHIP_NOTES}'")
                .execute())
            PageResult(newItemList, Select().from(ArticleListItem::class.java)
                .where("${ArticleListItem.COLUMN_CATEGORY} = '${ArticleListItem.CATEGORY_WORSHIP_NOTES}'")
                .execute())
        }
    }

    fun getToSeeChristList(page: Int = 1, limit: Int = 20, isSync: Boolean = false): PageResult<ArticleListItem>? {
        return performRequest(GetToSeeChristListRequest(page, limit), api.getToSeeChristList(page, limit), isSync) {
            responseItemList ->
            val newItemList = ArrayList<ArticleListItem>()
            responseItemList.forEach { newItemList.add(it.dbEntity.apply {
                category = ArticleListItem.CATEGORY_TO_SEE_CHRIST })
            }
            merge(ArticleListItem::class.java, newItemList, page, Select().from(ArticleListItem::class.java)
                    .where("${ArticleListItem.COLUMN_CATEGORY} = '${ArticleListItem.CATEGORY_TO_SEE_CHRIST}'")
                    .execute())
            PageResult(newItemList, Select().from(ArticleListItem::class.java)
                    .where("${ArticleListItem.COLUMN_CATEGORY} = '${ArticleListItem.CATEGORY_TO_SEE_CHRIST}'")
                    .execute())
        }
    }

    fun getArticleDetails(id: Long, isSync: Boolean = false): ArticleDetails? {
        return performRequest(GetArticleDetailsRequest(id), api.getWorshipNotesDetails(id), isSync) {
            val newItem = it.dbEntity
            val oldItem = Select().from(ArticleDetails::class.java)
                    .where("${ArticleDetails.COLUMN_PAGE_ID} = ${newItem.pageId}")
                    .executeSingle<ArticleDetails>()
            if (oldItem != null) {
                oldItem.updateWith(newItem)
                oldItem.save()
                oldItem
            } else {
                newItem.save()
                newItem
            }
        }
    }

    fun getMusicGroups(isSync: Boolean = false): List<MusicGroup>? {
        return performRequest(GetMusicGroupsRequest(), api.getMusicGroups(), isSync) { response ->
            val musicGroupList = ArrayList<MusicGroup>()
            response.forEach { musicGroupList.add(it.dbEntity) }
            merge(MusicGroup::class.java, musicGroupList)
            musicGroupList
        }
    }

    fun getMusicGroup(id: Long, isSync: Boolean = false): MusicGroup? {
        return performRequest(GetMusicGroupRequest(id), api.getMusicGroup(id), isSync) { response ->
            val musicGroup = response.dbEntity
            val oldData = Select().from(MusicGroup::class.java)
                .where("${MusicGroup.COLUMN_EXTERNAL_ID} = ${musicGroup.externalId}")
                .executeSingle<MusicGroup>()
            if (oldData == null) {
                musicGroup.save()
            } else {
                oldData.updateWith(musicGroup)
                oldData.save()
            }
            musicGroup
        }
    }

    fun getSongs(groupId: Long, isSync: Boolean = false): List<Song>? {
        return performRequest(GetSongsRequest(groupId), oldApi.getSongs(groupId), isSync) { response ->
            ActiveAndroid.beginTransaction()
            response.forEach { song ->
                val existingSong: Song? = Select()
                    .from(Song::class.java)
                    .where("${Song.COLUMN_ID} = ${song.externalId()}")
                    .executeSingle()
                if (existingSong == null) {
                    song.save()
                } else {
                    existingSong.groupID = song.groupID
                    existingSong.setTitle(song.getTitle())
                    existingSong.audioUri = song.audioUri
                    existingSong.setDate(song.getDate())
                    existingSong.save()
                }
            }
            ActiveAndroid.setTransactionSuccessful()
            ActiveAndroid.endTransaction()
            response
        }
    }

    //----------------------------------------------------------------------------------
    //
    // Private methods
    //
    //----------------------------------------------------------------------------------
    private fun createGethApi(): GethApi {
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val wrappedRequest = originalRequest.newBuilder()
                .header("X-Api-Key", BuildConfig.X_API_KEY)
                .method(originalRequest.method(), originalRequest.body())
                .build()
            chain.proceed(wrappedRequest)
        }
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.gethsemane.by")
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(httpClient)
            .build()
        return retrofit.create(GethApi::class.java)
    }

    private fun createOldGethApi(): OldApi {
        val credentials = "${BuildConfig.API_BASE_AUTH_LOGIN}:${BuildConfig.API_BASE_AUTH_PASS}"
        val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val originalRequest = chain.request()
            val wrappedRequest = originalRequest.newBuilder()
                .header("Authorization", basic)
                .header("Accept", "applicaton/json")
                .method(originalRequest.method(), originalRequest.body())
                .build()
            chain.proceed(wrappedRequest)
        }.addInterceptor(loggingInterceptor).build()

        val gson = GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .registerTypeAdapter(Date::class.java, JsonDateDeserializer())
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.geth.by")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit.create(OldApi::class.java)
    }

    private fun <T, R> performRequest(
            request: BaseRequest,
            call: Call<T>,
            isSync: Boolean,
            processResponse: (T) -> R): R? {

        if (isRunning(request)) return null

        return if (isSync) {
            executeSyncRequest(request, call, processResponse).result
        } else {
            requestList.add(request)
            val startedMessage = handler.obtainMessage(WHAT_STARTED, request)
            handler.sendMessage(startedMessage)
            threadPoolExecutor.execute {
                val messageObj = executeSyncRequest(request, call, processResponse)
                val completedMessage = handler.obtainMessage(WHAT_COMPLETED, messageObj)
                handler.sendMessage(completedMessage)
            }
            null
        }
    }

    private fun <T, R> executeSyncRequest(
            request: BaseRequest,
            call: Call<T>,
            processResponseBody: (T) -> R): HandlerMessageObj<R> {
        try {
            val response = call.execute()
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody == null)
                    HandlerMessageObj(request, code = response.code(), message = response.message())
                else
                    HandlerMessageObj(request, code = response.code(), message = response.message(),
                            result = processResponseBody(responseBody))
            } else {
                HandlerMessageObj(request, code = response.code(), message = response.message())
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            return HandlerMessageObj(request, exception = t)
        }
    }

    private class HandlerMessageObj<R>(
            val request: BaseRequest,
            val result: R? = null,
            val code: Int? = null,
            val message: String? = null,
            val exception: Throwable? = null
    )
}
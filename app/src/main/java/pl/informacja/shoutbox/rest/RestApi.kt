package pl.informacja.shoutbox.rest

import pl.informacja.shoutbox.model.MessageModel
import retrofit2.Call
import retrofit2.http.*

interface RestApi {
    @Headers("Content-Type: application/json")

    @GET("messages")
    fun getMessages(): Call<List<MessageModel?>?>?

    @POST("message")
    fun sendMessage(@Body messageDto: MessageModel): Call<MessageModel>

    @PUT("message/{id}")
    fun editMessage(@Path("id") id: String?, @Body messageDto: MessageModel?): Call<MessageModel?>?

    @PATCH("message/{id}")
    fun patchEditMessage(@Path("id") id: String?, @Body messageDto: MessageModel?): Call<MessageModel?>?
}
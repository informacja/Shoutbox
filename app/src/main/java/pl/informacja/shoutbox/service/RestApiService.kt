package pl.informacja.shoutbox.service

import pl.informacja.shoutbox.model.MessageModel
import pl.informacja.shoutbox.rest.RestApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    private val retrofit =
        ServiceBuilder.buildService(
            RestApi::class.java
        )
    fun sendMessage(message: MessageModel, onResult: (MessageModel?) -> Unit) {
        retrofit.sendMessage(message).enqueue(
            object  : Callback<MessageModel> {
                override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {
                    val sendedMessage = response.body()
                    onResult(sendedMessage)
                }
                override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                    onResult(null)
                }
            }
        )
    }

//    fun editMessage(id: String, message: MessageDto, onResult: (MessageDto?) -> Unit) {
//        retrofit.editMessage(id,message).enqueue(
//            object  : Callback<MessageDto> {
//                override fun onResponse(call: Call<MessageDto>, response: Response<MessageDto>) {
//                    val sendedMessage = response.body()
//                    onResult(sendedMessage)
//                }
//                override fun onFailure(call: Call<MessageDto>, t: Throwable) {
//                    onResult(null)
//                }
//            }
//        )
//    }
}
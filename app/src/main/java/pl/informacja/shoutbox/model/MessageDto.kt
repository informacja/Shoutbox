package pl.informacja.shoutbox.model

import com.google.gson.annotations.SerializedName

class MessageModel(content: String, login: String) {
    @SerializedName("content")
    val content: String? = null

    @SerializedName("login")
    var login: String? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("id")
    var id: String? = null
}

//class MessageDto(val content: String, val login: String) {
//}
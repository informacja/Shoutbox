package pl.informacja.shoutbox

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_edit.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

import android.widget.*
import com.informacja.shoutbox.R

class MessageEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val content: String = intent.getStringExtra("CONTENT")
        var name: String = intent.getStringExtra("NAME")
        val date: String = intent.getStringExtra("DATE")
        val id: String = intent.getStringExtra("ID")

        val sharedPref = getSharedPreferences("SP_INFO_ROW", Context.MODE_PRIVATE)
        val myLogin = sharedPref.getString("LOGIN","")

        val JSON = MediaType.parse("application/json; charset=utf-8")

        val tvLogin = findViewById<TextView>(R.id.login_textView_edit)
        val tvDate = findViewById<TextView>(R.id.date_textView_edit)
        val tvContent = findViewById<TextView>(R.id.content_textView_edit)
        val bShowLogin = findViewById<Button>(R.id.showLogin_button_edit)
        val newMessage = newMessage_editText_edit.text


        tvLogin.text = name.toString()
        tvDate.text = date.toString()
        tvContent.text = content.toString()

        bShowLogin.setOnClickListener {
            Toast.makeText(this,"Your login is: " + myLogin + ", Message: "+ newMessage.toString(),Toast.LENGTH_LONG).show()

        }

        acceptNewMessage_button_edit.setOnClickListener {
            Toast.makeText(this,"Your login is: " + myLogin + ", Message: "+ newMessage.toString(),Toast.LENGTH_LONG).show()
            val url = "http://tgryl.pl/shoutbox/message/"+id
            var client = OkHttpClient()
            var changeJson = JSONObject()
            changeJson.put("content",newMessage.toString())
            changeJson.put("login",myLogin)

            val body =  RequestBody.create(JSON,changeJson.toString())
            val requestBuilder = Request.Builder()
                .url(url)
                .put(body)
                .build()
            client.newCall(requestBuilder).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    finish()
//                startActivity(intent)
                }
                override fun onFailure(call: Call, e: IOException) {
                    println("Execute request")
                }
            })
        }

    }

}

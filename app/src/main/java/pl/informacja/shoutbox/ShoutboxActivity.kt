package pl.informacja.shoutbox

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.GsonBuilder
import com.informacja.shoutbox.R
import kotlinx.android.synthetic.main.activity_shoutbox.*
import kotlinx.android.synthetic.main.message_row.view.*
import okhttp3.*
import pl.informacja.shoutbox.model.Message
import pl.informacja.shoutbox.model.MessageModel
import pl.informacja.shoutbox.service.RestApiService
import java.io.IOException


class ShoutboxActivity : AppCompatActivity() {

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var myName: String? = null
//    private var rowEdit: ConstraintLayout? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoutbox)
        val bShowLogin = findViewById<Button>(R.id.showLogin_button_shoutbox)
        val iSendMessage = findViewById<ImageView>(R.id.sendMessage_imageView_shoutbox)
        val etMessage = findViewById<EditText>(R.id.message_editText_shoutbox)
        swipeRefreshLayout = findViewById(R.id.swipe_swipeRefreshLayout_shoutbox)

        recyclerView_shoutbox.layoutManager = LinearLayoutManager(this)
        // get data from Shared Pref
        val sharedPref = getSharedPreferences("SP_INFO", Context.MODE_PRIVATE)
        val name = sharedPref.getString("LOGIN","")
        myName = name

        swipeToRefresh()
        fetchJson()
        refreshEveryHalfMinute()

        bShowLogin.setOnClickListener {
            val msg = etMessage.text.toString()
            Toast.makeText(this,"Your login is: " + name+" Message: "+msg,Toast.LENGTH_LONG).show()
        }

        iSendMessage.setOnClickListener {
            if (isInternetConnection()) {
                val msg = etMessage.text.toString()
                Toast.makeText(this,"Your login is: " + name+" Message: "+msg,Toast.LENGTH_LONG).show()
                if (name != null) {
                    sendMessageToShoutbox(msg,name)
                }
            } else {
                Toast.makeText(this,"You don't have Internet Connection!",Toast.LENGTH_LONG).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageToShoutbox(msg: String, lgin: String) {

        val apiService = RestApiService()
        val messageToSend = MessageModel(content = msg, login = lgin)

        apiService.sendMessage(messageToSend) {
            if (it?.content != "") {
                // it = newly added user parsed as response
                // it?.id = newly added user ID
                Log.d("ShoutboxActivity", "Sended!")
            } else {
                Log.d("ShoutboxActivity", "Error sending a message!")
            }
        }
    }

    fun isInternetConnection(): Boolean {
        val cm =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }

    private fun swipeToRefresh() {
        swipeRefreshLayout?.setOnRefreshListener(OnRefreshListener {
            fetchJson()
            Handler().postDelayed({ swipeRefreshLayout!!.setRefreshing(false) }, 2000)
        })
    }

    private fun refreshEveryHalfMinute() {
        var thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(30000) // 30000 ms = 30s
                        runOnUiThread {
                            fetchJson()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
        thread.start()
    }


    private fun swipeToDelete() {
        if (isInternetConnection()) {
            recyclerView_shoutbox.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

            val swipeHandler = object : SwipeToDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = recyclerView_shoutbox.adapter as MainAdapter
                    val currentId = adapter.currentId(viewHolder.adapterPosition)
                    val currentName = adapter.currentName(viewHolder.adapterPosition)
                    Log.d("ResponseCmn","Id $currentId, name: $currentName, adapterPosition: ${viewHolder.adapterPosition}")

                    if (currentName != null) {
                        if (currentId != null) {
                            deletePost(currentId,currentName)
                        }
                    }
                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(recyclerView_shoutbox)
        } else {
            Toast.makeText(this,"You don't have Internet Connection!",Toast.LENGTH_LONG).show()
        }
    }

    private fun deletePost(id:String,myLogin:String) {
        val url = "http://tgryl.pl/shoutbox/message/$id"
        var client = OkHttpClient()

        val requestBuilder = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(requestBuilder).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
//                startActivity(intent)
                Log.d("Response","Id is: $id, LOGIN :  $myLogin")
            }
            override fun onFailure(call: Call, e: IOException) {
                println("Execute request")
            }
        })
        Toast.makeText(this,"Deleted",Toast.LENGTH_LONG).show()
    }


    fun fetchJson() {
        if (isInternetConnection()) {
        Toast.makeText(this, "Refreshed!", Toast.LENGTH_SHORT).show()
        println("Attempting to fetch JSON")

        val url = "http://tgryl.pl/shoutbox/messages"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                println("OKhttp"+body)

                val gson = GsonBuilder().create()
//                val shoutboxData = gson.fromJson(body,ShoutboxData::class.java)

                val messages: Array<Message> = gson.fromJson(
                    body,
                    Array<Message>::class.java
                )
                runOnUiThread {
//                    recyclerView_shoutbox.adapter = MainAdapter(messages)
                    recyclerView_shoutbox.adapter =
                        MainAdapter(
                            messages.toCollection(ArrayList())
                        )
                    swipeToDelete()
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println("Execute request")
            }
        })
        } else {
            Toast.makeText(this,"You don't have Internet Connection!",Toast.LENGTH_LONG).show()
        }
    }

    fun editField(view: View) {
        Toast.makeText(this,"U want to edit this field",Toast.LENGTH_LONG).show()
//
        val sharedPref = getSharedPreferences("SP_INFO_ROW",Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        // putting data into sharedPref
        editor.putString("LOGIN",myName)

        editor.apply()

        val intent = Intent(this, MessageEditActivity::class.java)
        intent.putExtra("NAME", view.name_textView_row.text)
        intent.putExtra("CONTENT", view.message_textView_row.text)
        intent.putExtra("DATE", view.date_textView_row.text)
        intent.putExtra("ID", view.id_textView_row.text)
        startActivity(intent)
    }
}

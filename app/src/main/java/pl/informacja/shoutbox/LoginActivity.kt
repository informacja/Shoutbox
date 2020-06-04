package pl.informacja.shoutbox

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.informacja.shoutbox.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val bSetLogin = findViewById<Button>(R.id.setLoginButton)
        val etLogin = findViewById<EditText>(R.id.loginInput)

        bSetLogin.setOnClickListener {
            val login = etLogin.text.toString()

            Toast.makeText(this,"Hello, " + login,Toast.LENGTH_LONG).show()

            //Shared Preferences
            val sharedPref = getSharedPreferences("SP_INFO",Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("LOGIN", login)
            editor.apply()

            val intent = Intent(this, ShoutboxActivity::class.java)
            intent.putExtra("LOGIN", login)
            startActivity(intent)
        }
    }
}

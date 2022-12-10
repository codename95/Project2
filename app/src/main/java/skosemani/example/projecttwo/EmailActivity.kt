package skosemani.example.projecttwo

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class EmailActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var btn: Button
    lateinit var edtText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)
        btn = findViewById(R.id.btnSendEmail)
        btn.setOnClickListener(this)

        edtText = findViewById(R.id.txtEmailAddress)

        val sharedPreference = getSharedPreferences("MAPSTORE", Context.MODE_PRIVATE)
        var result = sharedPreference.getString("email_address", "")
        if(result == ""){
            edtText.setText("sample@domain.com")
        }
        else{
            edtText.setText(result)
        }
    }

    override fun onClick(view: View?) {

        val to = arrayOf(edtText.text.toString())
        val address = getSharedPreferences("ADDRESS")
        val latitude = getSharedPreferences("LAT")
        val longitude = getSharedPreferences("LON")

        val body = "LAT: $latitude LON: $longitude \nADDRESS: $address"

        composeCustomEmail(to, "My Location - Coordinates", body)

        val sharedPreference = getSharedPreferences("MAPSTORE", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("email_address", edtText.text.toString())
        editor.commit()

        Toast.makeText(this, "Let's send the email.",
            Toast.LENGTH_SHORT).show();

    }

    private fun getSharedPreferences(key: String): String {
        val sharedPreference = getSharedPreferences("MAPSTORE", Context.MODE_PRIVATE)
        var result = sharedPreference.getString(key, "")
        return result.toString()
    }
    private fun composeCustomEmail(addresses: Array<String>, subject: String, body: String){
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.data = Uri.parse("mailto:")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                this, "There is no application that support this action",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
package skosemani.example.projecttwo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import skosemani.example.projecttwo.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() , View.OnClickListener{

    private lateinit var binding: ActivityAboutUsBinding

    lateinit var btn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        val binding = ActivityAboutUsBinding.inflate(layoutInflater)
        btn = findViewById(R.id.about_close_btn)
        btn.setOnClickListener(this)
        getSharedPreferences("LON")
        getSharedPreferences("LAT")
    }

    private fun getSharedPreferences(key: String): String {
        val sharedPreference = getSharedPreferences("MAPSTORE", Context.MODE_PRIVATE)
        var result = sharedPreference.getString(key, "")
        return result.toString()
    }

    override fun onClick(view: View?) {
       finish()
    }
}
package my.edu.tarc.okuappg11.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.okuappg11.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fAuth = FirebaseAuth.getInstance()

        binding.btnLogoutSettings.setOnClickListener {
            //HomeActivity().destroyActivity()

            val preferences = getSharedPreferences("sharedLogin", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()

            finish()
            fAuth.signOut()
            //supportFragmentManager.beginTransaction().replace(R.id.containerSettings, SignInFragment()).commit()
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }

        binding.btnBackSetting.setOnClickListener {
            finish()
            onBackPressed()
        }

        binding.lyAboutUs.setOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }

        binding.lyContactUs.setOnClickListener {
            val intent = Intent(this, ContactUsActivity::class.java)
            startActivity(intent)
        }

        binding.lyProfileSetting.setOnClickListener {
            val intent = Intent(this, ProfileSettingActivity::class.java)
            startActivity(intent)
        }
    }
}
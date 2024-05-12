package com.example.esp_v3

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.esp_v3.databinding.ActivitySettingsBinding
import dagger.android.support.DaggerAppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.title_settings)

        /*api_button.setOnClickListener {
            val address : String? = preferences.address()
            dialog = DialogUtils.dialogEditText(this@SettingsActivity, getString(R.string.pref_address), address!!,
                object : DialogTextView.ViewListener {
                    override fun onTextChange(value: String?) {
                        if(!TextUtils.isEmpty(value)) {
                            preferences.address(value!!)
                            api_text.text = preferences.address()
                        }
                    }
                    override fun onCancel() {
                        dialog?.dismiss()
                    }
                })
        }
        password_button.setOnClickListener {
            val password : String? = preferences.password()
            dialog = DialogUtils.dialogPasswordText(this@SettingsActivity, getString(R.string.pref_password), password!!,
                object : DialogTextView.ViewListener {
                    override fun onTextChange(value: String?) {
                        if(!TextUtils.isEmpty(value)) {
                            preferences.password(value!!)
                        }
                    }
                    override fun onCancel() {
                        dialog?.dismiss()
                    }
                })
        }*/
        binding.wifiScan.setOnClickListener {
            val intent = Intent(this@SettingsActivity, WifiScannerActivity::class.java)
            startActivity(intent)
        }

        binding.IPsScan.setOnClickListener {
            val intent = Intent(this@SettingsActivity, ReachableDevices::class.java)
            startActivity(intent)
        }

        /*binding.sendImageBut.setOnClickListener {
            val intent = Intent(this@SettingsActivity, SendImageActivity::class.java)
            startActivity(intent)
        }*/

        binding.sendImageBut.setOnClickListener {
            val intent = Intent(this@SettingsActivity, SendImageAct::class.java)
            startActivity(intent)
        }
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(newIntent(activity))
        }
        private fun newIntent(context: Context): Intent {
            return IntentUtils.newIntent(context, SettingsActivity::class.java)
        }
    }
}
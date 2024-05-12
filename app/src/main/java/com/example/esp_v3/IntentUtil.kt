package com.example.esp_v3

import android.content.Context
import android.content.Intent
import android.os.Bundle

class IntentUtils private constructor() {

    init {
        throw IllegalStateException("No instances")
    }

    companion object {


        fun startActivity(context: Context, kls: Class<*>) {
            context.startActivity(newIntent(context, kls))
        }

        fun startActivity(context: Context, kls: Class<*>, bundle: Bundle) {
            context.startActivity(newIntent(context, kls, bundle))
        }

        @JvmOverloads
        fun newIntent(context: Context, kls: Class<*>, bundle: Bundle = Bundle()): Intent {
            val intent = Intent(context, kls)
            intent.putExtras(bundle)

            return intent
        }
    }
}
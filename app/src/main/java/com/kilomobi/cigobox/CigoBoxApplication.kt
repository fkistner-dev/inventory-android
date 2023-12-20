package com.kilomobi.cigobox

import android.app.Application
import android.content.Context

class CigoBoxApplication: Application() {
    init { app = this }

    companion object {
        private lateinit var app: CigoBoxApplication

        fun getAppContext(): Context = app.applicationContext
    }
}
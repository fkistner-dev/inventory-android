/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

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
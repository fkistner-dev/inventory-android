/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

enum class Category {
    TOUT,
    ALCOOL,
    SOFT,
    FRAIS,
    SEC
}

fun String.toCategory() : Category {
    return Category.valueOf(this)
}

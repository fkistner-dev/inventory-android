package com.kilomobi.cigobox

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

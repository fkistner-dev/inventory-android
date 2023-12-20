package com.kilomobi.cigobox

enum class Category {
    TOUT,
    BOISSONS,
    NOURRITURES,
    CADENAS,
    FOURNISSEURS
}

fun String.toCategory() : Category {
    return Category.valueOf(this)
}

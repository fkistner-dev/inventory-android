package com.kilomobi.cigobox

data class Appetizer(
    val id: Int,
    val title: String,
    val provider: String,
    val category: Category,
    val quantity: Int,
    val imageUrl: String? = null,
    val isEditable: Boolean = false,
    val isVisible: Boolean = true,
    val usedInBox: List<Int> = listOf()
)

val dummyAppetizers = listOf(
    Appetizer(0, "Bouteille 75cl", "Alain Dubois", Category.BOISSONS, 1),
    Appetizer(1, "Limonade 75cl", "Serge Marc", Category.BOISSONS, 1),
    Appetizer(2, "Jus de fruits", "Hervé Palas", Category.BOISSONS, 1),
    Appetizer(3, "Mignonette", "George George", Category.BOISSONS, 2),
    Appetizer(4, "Bière blonde", "Mathias Galois", Category.BOISSONS, 3),
    Appetizer(5, "Crémant 75cl", "Serge Lamah", Category.BOISSONS, 2),
    Appetizer(6, "Saucisson", "Jack Cerf", Category.NOURRITURES, 2),
    Appetizer(7, "Master", "Le Serrurier", Category.CADENAS, 8)
)
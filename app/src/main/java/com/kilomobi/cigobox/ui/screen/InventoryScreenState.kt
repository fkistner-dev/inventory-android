/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 30/12/2023 17:42.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.ui.screen

import com.kilomobi.cigobox.model.Appetizer
import com.kilomobi.cigobox.model.Category

data class InventoryScreenState(
    val appetizers: List<Appetizer>,
    val isLoading: Boolean,
    val error: String? = null,
    val allowEdit: Boolean,
    val isBoxScreen: Boolean,
    val selectedFilter: Category,
    val selectedPlayerBox: Int
)

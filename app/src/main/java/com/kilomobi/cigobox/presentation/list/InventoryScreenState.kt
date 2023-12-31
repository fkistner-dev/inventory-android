/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 01:09.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.presentation.list

import com.kilomobi.cigobox.domain.Appetizer
import com.kilomobi.cigobox.domain.Category

data class InventoryScreenState(
    val appetizers: List<Appetizer>,
    val isLoading: Boolean,
    val error: String? = null,
    val allowEdit: Boolean,
    val isBoxScreen: Boolean,
    val selectedFilter: Category,
    val selectedPlayerBox: Int
)

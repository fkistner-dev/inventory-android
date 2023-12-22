/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kilomobi.cigobox.model.Category
import com.kilomobi.cigobox.ui.theme.CigOrange
import com.kilomobi.cigobox.ui.theme.CigoGrey

@Composable
fun HorizontalFilterRow(
    filters: List<Category>,
    selectedFilter: Category?,
    onFilterSelected: (Category) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterButton(
                filter = filter.name,
                isSelected = filter == selectedFilter,
                onFilterSelected = { onFilterSelected(it) }
            )
        }
    }
}

@Composable
fun FilterButton(
    filter: String,
    isSelected: Boolean,
    onFilterSelected: (Category) -> Unit
) {
    Button(
        onClick = {
            onFilterSelected(Category.valueOf(filter))
        },
        border = BorderStroke(2.dp, CigOrange),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) CigOrange else CigoGrey
        )
    ) {
        Text(text = filter, color = Color.Black)
    }
}
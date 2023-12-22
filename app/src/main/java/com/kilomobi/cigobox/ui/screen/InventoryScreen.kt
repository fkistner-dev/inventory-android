/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kilomobi.cigobox.model.Appetizer
import com.kilomobi.cigobox.model.BoxOperation
import com.kilomobi.cigobox.model.Category
import com.kilomobi.cigobox.viewmodel.InventoryViewModel
import com.kilomobi.cigobox.ui.theme.CigOrange
import com.kilomobi.cigobox.ui.theme.CigoGrey

@Composable
fun InventoryScreen() {
    val viewModel: InventoryViewModel = viewModel()

    LazyColumn(
        contentPadding = PaddingValues(
            vertical = 8.dp,
            horizontal = 8.dp
        )
    ) {
        item {
            HeaderItem(
                viewModel.allowEdit.value,
                viewModel.isBoxScreen.value,
                viewModel.selectedPlayerBox.value,
                { viewModel.toggleEdit() },
                { viewModel.subtractBox() },
                { viewModel.validateStock() })
        }

        if (viewModel.isBoxScreen.value) {
            val boxList = listOf(3, 4, 5, 6, 7)
            items(boxList) { playerCount ->
                val boxOperations = viewModel.getBoxOperationList(playerCount)
                BoxItem(
                    playerCount.toString(),
                    boxOperations,
                    viewModel.selectedPlayerBox.value
                ) { id -> viewModel.selectBoxAction(id) }
            }
        } else {
            item {
                val filterList = listOf(
                    Category.TOUT,
                    Category.ALCOOL,
                    Category.SOFT,
                    Category.FRAIS,
                    Category.SEC
                )

                HorizontalFilterRow(
                    filterList,
                    selectedFilter = viewModel.selectedFilter.value,
                    onFilterSelected = { selectedFilter ->
                        viewModel.filterAction(selectedFilter)
                    })
            }
            items(viewModel.items.value) { appetizer ->
                if (appetizer.isVisible) {
                    AppetizerItem(
                        item = appetizer,
                        viewModel.allowEdit.value,
                        onIncreaseAction = { id -> viewModel.increaseQuantity(id) },
                        onDecreaseAction = { id -> viewModel.decreaseQuantity(id) })
                }
            }
        }
    }
}

@Composable
fun AppetizerItem(
    item: Appetizer,
    isEditable: Boolean,
    onIncreaseAction: (id: Int) -> Unit,
    onDecreaseAction: (id: Int) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier.padding(8.dp, 4.dp),
        colors = CardDefaults.cardColors(containerColor = CigoGrey)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if (isEditable) {
                AppetizerIcon(
                    Icons.Filled.KeyboardArrowDown,
                    Modifier.weight(0.15f)
                ) { onDecreaseAction(item.id) }
                AppetizerQuantity(item.quantity, item.bufferSize, modifier = Modifier.weight(0.20f))
                AppetizerDetails(item.title, item.supplier, Modifier.weight(0.50f))
                AppetizerIcon(
                    Icons.Filled.KeyboardArrowUp,
                    Modifier.weight(0.15f)
                ) { onIncreaseAction(item.id) }
            } else {
                AppetizerQuantity(item.quantity, item.bufferSize, modifier = Modifier.weight(0.20f))
                AppetizerDetails(item.title, item.supplier, Modifier.weight(0.80f))
            }
        }
    }
}

@Composable
fun BoxItem(
    playerCount: String,
    boxOperations: List<BoxOperation>,
    selectedBox: Int,
    onBoxSelected: (id: Int) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .clickable {
                onBoxSelected(playerCount.toInt())
            },
        colors = CardDefaults.cardColors(containerColor = if (selectedBox == playerCount.toInt()) CigOrange else CigoGrey)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            val appetizersText = boxOperations
                .filter { it.withdrawQuantity > 0 }
                .groupBy { it.supplier }
                .map { (_, groupOperations) ->
                    groupOperations.joinToString(separator = " | ") {
                        "${it.withdrawQuantity} ${it.title}" + if (it.withdrawQuantity > 1 && it.title.last() != 's') "s" else ""
                    }
                }
                .joinToString(separator = " | ")

            AppetizerDetails("Box pour $playerCount", appetizersText, Modifier.weight(0.80f))
        }
    }
}

@Composable
private fun AppetizerQuantity(quantity: Int = 1, bufferSize: Int? = null, modifier: Modifier) {
    val context = LocalContext.current
    var hasBuffer = false
    bufferSize?.let {
        if (quantity <= it)
            hasBuffer = true
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = quantity.toString(),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = CigOrange,
            textAlign = TextAlign.Center,
            modifier = Modifier.alignByBaseline()
        )

        if (hasBuffer) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                Icons.Outlined.Warning,
                contentDescription = "stock alert",
                Modifier.clickable {
                    val message = "Le stock est inférieur au stock tampon ($bufferSize)"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun AppetizerIcon(icon: ImageVector, modifier: Modifier, onClick: () -> Unit = { }) {
    Image(
        imageVector = icon,
        contentDescription = "Appetizer icon",
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
    )
}

@Composable
private fun AppetizerDetails(
    title: String,
    description: String,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
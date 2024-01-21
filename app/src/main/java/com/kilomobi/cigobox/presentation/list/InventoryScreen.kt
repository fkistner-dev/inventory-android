/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 01:07.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.presentation.list

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kilomobi.cigobox.domain.Appetizer
import com.kilomobi.cigobox.domain.BoxOperation
import com.kilomobi.cigobox.domain.Category
import com.kilomobi.cigobox.domain.usecase.GetBoxItemTextUseCase
import com.kilomobi.cigobox.presentation.ui.HeaderItem
import com.kilomobi.cigobox.presentation.ui.HorizontalFilterRow
import com.kilomobi.cigobox.ui.theme.CigOrange
import com.kilomobi.cigobox.ui.theme.CigoGrey

@Composable
fun InventoryScreen(
    state: InventoryScreenState,
    toggleEdit: () -> Unit,
    subtractBox: () -> Unit,
    validateStock: () -> Unit,
    getBoxOperationList: (playerCount: Int) -> List<BoxOperation>,
    selectBoxAction: (playerCount: Int) -> Unit,
    filterAction: (category: Category) -> Unit,
    updateQuantity: (id: Int, quantity: Int) -> Unit,
    loadInventory: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            vertical = 8.dp,
            horizontal = 8.dp
        )
    ) {
        item {
            HeaderItem(
                state.allowEdit,
                state.isBoxScreen,
                state.selectedPlayerBox,
                { toggleEdit() },
                { subtractBox() },
                { validateStock() })
        }

        if (state.isBoxScreen) {
            val boxList = listOf(3, 4, 5, 6, 7)
            items(boxList) { playerCount ->
                val boxOperations = getBoxOperationList(playerCount)
                BoxItem(
                    playerCount.toString(),
                    boxOperations,
                    state.selectedPlayerBox
                ) { id -> selectBoxAction(id) }
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
                    selectedFilter = state.selectedFilter,
                    onFilterSelected = { selectedFilter ->
                        filterAction(selectedFilter)
                    })
            }
            items(state.appetizers) { appetizer ->
                if (appetizer.isVisible) {
                    AppetizerItem(
                        item = appetizer,
                        state.allowEdit,
                        onIncreaseAction = { id, quantity ->
                            updateQuantity(
                                id,
                                quantity
                            )
                        },
                        onDecreaseAction = { id, quantity ->
                            updateQuantity(
                                id,
                                quantity
                            )
                        })
                }
            }
        }
    }
    if (state.isLoading) LinearProgressIndicator()
    if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = state.error,
                modifier = Modifier.align(Alignment.Center),
            )
            Button(
                onClick = { loadInventory() },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
fun AppetizerItem(
    item: Appetizer,
    isEditable: Boolean,
    onIncreaseAction: (id: Int, quantity: Int) -> Unit,
    onDecreaseAction: (id: Int, quantity: Int) -> Unit
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
                ) { onDecreaseAction(item.id, item.quantity - 1) }
                AppetizerQuantity(item.quantity, item.bufferSize, modifier = Modifier.weight(0.20f))
                AppetizerDetails(item.title, item.supplier, Modifier.weight(0.50f))
                AppetizerIcon(
                    Icons.Filled.KeyboardArrowUp,
                    Modifier.weight(0.15f)
                ) { onIncreaseAction(item.id, item.quantity + 1) }
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
            val appetizersText = GetBoxItemTextUseCase().invoke(boxOperations)
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
                imageVector = Icons.Outlined.Warning,
                tint = Color.Black,
                contentDescription = "stock alert",
                modifier = Modifier.clickable {
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
            color = Color.Black,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = description,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
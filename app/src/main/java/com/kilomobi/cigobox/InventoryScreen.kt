package com.kilomobi.cigobox

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                { viewModel.toggleEdit() },
                { viewModel.validateStock() })
        }
        item {
            val filterList = listOf(
                Category.BOISSONS,
                Category.NOURRITURES,
                Category.CADENAS,
                Category.FOURNISSEURS
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
                    onIncreaseAction = { id -> viewModel.increaseQuantity(id) },
                    onDecreaseAction = { id -> viewModel.decreaseQuantity(id) })
            }
        }
    }
}

@Composable
fun AppetizerItem(
    item: Appetizer,
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
            if (item.isEditable) {
                AppetizerIcon(
                    Icons.Filled.KeyboardArrowDown,
                    Modifier.weight(0.15f)
                ) { onDecreaseAction(item.id) }
                AppetizerQuantity(item.quantity.toString(), Modifier.weight(0.10f))
                AppetizerDetails(item.title, item.supplier, Modifier.weight(0.60f))
                AppetizerIcon(
                    Icons.Filled.KeyboardArrowUp,
                    Modifier.weight(0.15f)
                ) { onIncreaseAction(item.id) }
            } else {
                AppetizerQuantity(item.quantity.toString(), Modifier.weight(0.20f))
                AppetizerDetails(item.title, item.supplier, Modifier.weight(0.80f))
            }
        }
    }
}

@Composable
private fun AppetizerQuantity(quantity: String, modifier: Modifier) {
    Text(
        modifier = modifier,
        text = quantity,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = CigOrange,
        textAlign = TextAlign.Center
    )
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
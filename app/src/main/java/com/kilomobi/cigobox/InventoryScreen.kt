package com.kilomobi.cigobox

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kilomobi.cigobox.ui.theme.CigOrange
import com.kilomobi.cigobox.ui.theme.CigoBoxTheme
import com.kilomobi.cigobox.ui.theme.CigoGreen
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

@Composable
fun HeaderItem(
    allowEdit: Boolean, onEditAction: () -> Unit,
    onValidateAction: () -> Unit
) {
    Row {
        Column(Modifier.weight(0.5f), Arrangement.Center) {
            Image(painterResource(R.drawable.logo_cigobox), "logo", Modifier.size(200.dp))
        }
        Column(Modifier.weight(0.5f), Arrangement.Center, Alignment.CenterHorizontally) {
            Button(
                border = BorderStroke(2.dp, CigOrange),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allowEdit) CigOrange else Color.White
                ),
                onClick = {
                    onEditAction()
                }
            ) {
                Text(
                    stringResource(id = R.string.btn_edit_stock),
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
            if (!allowEdit) {
                Button(
                    border = BorderStroke(2.dp, CigOrange),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    onClick = { }
                ) {
                    Text(
                        stringResource(id = R.string.btn_substract_box), color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
            if (allowEdit) {
                Button(
                    border = BorderStroke(2.dp, CigoGreen),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    onClick = {
                        onValidateAction()
                    }
                ) {
                    Text(
                        stringResource(id = R.string.btn_validate), color = Color.Black,
                        fontSize = 16.sp
                    )
                }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CigoBoxTheme {
        AppetizerDetails("Hello", "World", Modifier.padding(8.dp))
    }
}
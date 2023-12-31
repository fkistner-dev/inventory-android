/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 01:11.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.kilomobi.cigobox.domain.Category
import com.kilomobi.cigobox.presentation.list.InventoryScreen
import com.kilomobi.cigobox.presentation.list.InventoryScreenState
import com.kilomobi.cigobox.presentation.list.InventoryViewModel
import com.kilomobi.cigobox.ui.theme.CigoBoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CigoBoxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val viewModel: InventoryViewModel = hiltViewModel()
                    InventoryScreen(
                        state = viewModel.state.value,
                        toggleEdit = { viewModel.toggleEdit() },
                        subtractBox = { viewModel.subtractBox() },
                        validateStock = { viewModel.validateStock() },
                        getBoxOperationList = { playerCount ->
                            viewModel.getBoxOperationList(playerCount)
                        },
                        selectBoxAction = { playerCount -> viewModel.selectBoxAction(playerCount) },
                        filterAction = { category -> viewModel.filterAction(category) },
                        updateQuantity = { id, quantity -> viewModel.updateQuantity(id, quantity) },
                        loadInventory = { viewModel.loadInventory() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CigoBoxTheme {
        InventoryScreen(
            state = InventoryScreenState(listOf(), true, null, false, false, Category.TOUT, 0),
            toggleEdit = { },
            subtractBox = { },
            validateStock = { },
            getBoxOperationList = { _ -> listOf() },
            selectBoxAction = { _ -> },
            filterAction = { _ -> },
            updateQuantity = { _, _ -> },
            loadInventory = { }
        )
    }
}
/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 01/01/2024 20:22.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox

import com.kilomobi.cigobox.data.InventoryRepository
import com.kilomobi.cigobox.domain.Category
import com.kilomobi.cigobox.domain.usecase.GetAppetizersUseCase
import com.kilomobi.cigobox.domain.usecase.GetBoxOperationsUseCase
import com.kilomobi.cigobox.domain.usecase.GetFilteredAppetizersUseCase
import com.kilomobi.cigobox.domain.usecase.GetInitialAppetizersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class GetAppetizersUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    // Setup useCase
    private val inventoryRepository = InventoryRepository(
        FakeApiService(),
        FakeRoomDao(),
        dispatcher
    )

    @Test
    fun getAppetizers_getList() = scope.runTest {
        // Setup useCase
        val useCase = GetAppetizersUseCase(inventoryRepository)

        // Preload data
        inventoryRepository.loadInventory()
        advanceUntilIdle()

        // Execute useCase
        val appetizers = DummyContent.getDomainAppetizers()
        val targetItem = appetizers[0]
        val appetizersReceived = useCase()
        advanceUntilIdle()

        // Assertion
        assert(targetItem == appetizersReceived[0])
    }

    @Test
    fun filteredAppetizers_getItemFromCategory() = scope.runTest {
        // Setup useCase
        val filteredAppetizers = GetFilteredAppetizersUseCase(GetInitialAppetizersUseCase(inventoryRepository))

        // Preload data
        inventoryRepository.loadInventory()
        advanceUntilIdle()

        // Execute useCase
        val appetizers = DummyContent.getDomainAppetizers()
        val targetItem = appetizers[3]
        val useCase = filteredAppetizers(Category.SEC)
        advanceUntilIdle()

        // Assertion
        assert(targetItem == useCase.firstOrNull { it.isVisible })
    }

    @Test
    fun getBoxOperations_isFilled() = scope.runTest {
        // Preload data
        inventoryRepository.loadInventory()
        advanceUntilIdle()

        // Execute useCase
        val boxOperations = DummyContent.getBoxOperations()
        val useCase = GetBoxOperationsUseCase(GetInitialAppetizersUseCase(inventoryRepository)).invoke(3)
        advanceUntilIdle()

        // Assertion
        assert(boxOperations == useCase)
    }
}

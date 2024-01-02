/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 02/01/2024 12:27.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox

import com.kilomobi.cigobox.data.InventoryRepository
import com.kilomobi.cigobox.domain.usecase.UpdateQuantityUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class UpdateQuantityUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val inventoryRepository = InventoryRepository(
        FakeApiService(),
        FakeRoomDao(),
        dispatcher
    )
    @Test
    fun updateAppetizer_isQuantityUpdated() = scope.runTest {
        // Setup useCase
        val useCase = UpdateQuantityUseCase(
            inventoryRepository
        )

        // Preload data
        inventoryRepository.loadInventory()
        advanceUntilIdle()

        // Execute useCase
        val appetizers = DummyContent.getDomainAppetizers()
        val targetItem = appetizers[0]
        val targetQuantity = 3
        val updatedAppetizer = useCase(appetizers, targetItem.id, targetQuantity)
        advanceUntilIdle()

        // Assertion
        appetizers[0] = targetItem.copy(quantity = targetQuantity, isQuantityUpdated = true)
        assert(appetizers[0] == updatedAppetizer[0])
    }
}

/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 01/01/2024 20:20.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain.usecase

import com.kilomobi.cigobox.domain.Appetizer
import com.kilomobi.cigobox.domain.Category
import com.kilomobi.cigobox.domain.toCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFilteredAppetizersUseCase @Inject constructor(
    private val getInitialAppetizersUseCase: GetInitialAppetizersUseCase
) {
    suspend operator fun invoke(
        category: Category
    ): List<Appetizer> {
        return withContext(Dispatchers.IO) {
            val appetizers = getInitialAppetizersUseCase()
            val filteredList =
                appetizers.filter { it.category.toCategory().name == category.name || category.name == Category.TOUT.name }
            val visibleList = appetizers.map { item ->
                item.copy(isVisible = item in filteredList)
            }

            return@withContext visibleList
        }
    }
}
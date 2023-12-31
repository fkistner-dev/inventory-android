/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 11:27.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

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
                item.copy(isVisible = item in filteredList, category = category.name)
            }

            return@withContext visibleList
        }
    }
}
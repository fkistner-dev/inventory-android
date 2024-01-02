/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 15:47.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain.usecase

import com.kilomobi.cigobox.domain.Appetizer
import javax.inject.Inject

class GetUpdatedAppetizersUseCase @Inject constructor() {
    operator fun invoke(
        appetizers: List<Appetizer>
    ): List<Appetizer> {
        return appetizers.filter { it.isQuantityUpdated }
    }
}
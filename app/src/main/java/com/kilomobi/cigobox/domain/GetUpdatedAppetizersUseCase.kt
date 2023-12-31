/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 11:46.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

class GetUpdatedAppetizersUseCase {
    operator fun invoke(
        appetizers: List<Appetizer>
    ): List<Appetizer> {
        return appetizers.filter { it.isQuantityUpdated }
    }
}
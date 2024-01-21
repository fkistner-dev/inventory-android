/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 21/01/2024 14:43.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain.usecase

import com.kilomobi.cigobox.domain.BoxOperation

class GetBoxItemTextUseCase {
    operator fun invoke(boxOperations: List<BoxOperation>): String {
        return boxOperations
            .filter { it.withdrawQuantity > 0 }
            .groupBy { it.supplier }
            .map { (_, groupOperations) ->
                groupOperations.joinToString(separator = " | ") {
                    "${it.withdrawQuantity} ${it.title}" + if (it.withdrawQuantity > 1 && it.title.last() != 's') "s" else ""
                }
            }
            .joinToString(separator = " | ")
    }
}
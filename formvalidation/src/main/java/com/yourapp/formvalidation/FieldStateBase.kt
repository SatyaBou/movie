package com.yourapp.formvalidation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

abstract class FieldStateBase<T>(
    initialValue: T
) {
    var value by mutableStateOf(initialValue)
    var error by mutableStateOf<String?>(null)

    abstract fun validate(): Boolean
}
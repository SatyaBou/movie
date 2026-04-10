package com.yourapp.formvalidation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AsyncFormFieldState<T>(
    initialValue: T,
    private val validators: List<Validator<T>>,
    private val asyncValidator: suspend (T) -> ValidationResult
) : FieldStateBase<T>(initialValue) {

    override fun validate(): Boolean {
        for (validator in validators) {
            val result = validator.validate(value)
            if (!result.isValid) {
                error = result.errorMessage
                return false
            }
        }
        return true
    }

    fun validateAsync(scope: CoroutineScope, onResult: (Boolean) -> Unit) {
        if (!validate()) {
            onResult(false)
            return
        }

        scope.launch {
            val result = asyncValidator(value)
            error = result.errorMessage
            onResult(result.isValid)
        }
    }
}
package com.yourapp.formvalidation

class FormFieldState<T>(
    initialValue: T,
    private val validators: List<Validator<T>>
) : FieldStateBase<T>(initialValue) {

    override fun validate(): Boolean {
        for (validator in validators) {
            val result = validator.validate(value)
            if (!result.isValid) {
                error = result.errorMessage
                return false
            }
        }
        error = null
        return true
    }
}
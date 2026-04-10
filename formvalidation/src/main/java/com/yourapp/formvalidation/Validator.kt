package com.yourapp.formvalidation

interface Validator<T> {
    fun validate(value: T): ValidationResult
}


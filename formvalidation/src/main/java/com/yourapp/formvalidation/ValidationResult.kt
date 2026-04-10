package com.yourapp.formvalidation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
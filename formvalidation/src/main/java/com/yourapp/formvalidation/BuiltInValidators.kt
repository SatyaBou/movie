package com.yourapp.formvalidation

class NotEmptyValidator(private val message: String = "Field cannot be empty") : Validator<String> {
    override fun validate(value: String) =
        if (value.isNotBlank()) ValidationResult(true)
        else
            ValidationResult(false, message)
}

class EmailValidator(private val message: String = "Invalid email") : Validator<String> {
    override fun validate(value: String) =
        if (value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")))
            ValidationResult(true)
        else
            ValidationResult(false, message)
}

class MinLengthValidator(private val min: Int, private val message: String? = null) :
    Validator<String> {
    override fun validate(value: String) =
        if (value.length >= min) ValidationResult(true)
        else ValidationResult(false, message ?: "Minimum length is $min")
}


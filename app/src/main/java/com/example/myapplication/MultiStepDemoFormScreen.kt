package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourapp.formvalidation.AsyncFormFieldState
import com.yourapp.formvalidation.EmailValidator
import com.yourapp.formvalidation.FormFieldState
import com.yourapp.formvalidation.MinLengthValidator
import com.yourapp.formvalidation.NotEmptyValidator
import com.yourapp.formvalidation.ValidatedTextField
import com.yourapp.formvalidation.ValidationResult

@Composable
fun MultiStepDemoFormScreen() {
    val scope = rememberCoroutineScope()

    val emailState = remember {
        FormFieldState(
            "", listOf(
                NotEmptyValidator(message = "Email is required"), EmailValidator()
            )
        )
    }
    val usernameState = remember {
        AsyncFormFieldState("", listOf(NotEmptyValidator())) { value ->
            if (value.lowercase() == "taken") ValidationResult(false, "Username already taken")
            else ValidationResult(true)
        }
    }
    val passwordState = remember {
        FormFieldState(
            "", listOf(
                NotEmptyValidator(),
                MinLengthValidator(6)
            )
        )
    }
    val confirmPasswordState = remember { FormFieldState("", listOf(NotEmptyValidator())) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Account Registration", style = MaterialTheme.typography.titleLarge)
        
        ValidatedTextField("Email", emailState)
        ValidatedTextField("Username", usernameState)
        ValidatedTextField("Password", passwordState, isPassword = true)
        ValidatedTextField("Confirm Password", confirmPasswordState, isPassword = true)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val isEmailValid = emailState.validate()
                val isPasswordValid = passwordState.validate()
                val isConfirmPasswordValid = confirmPasswordState.validate()

                if (isEmailValid && isPasswordValid && isConfirmPasswordValid) {
                    usernameState.validateAsync(scope) { isUsernameValid ->
                        if (isUsernameValid) {
                            if (passwordState.value == confirmPasswordState.value) {
                                println("Form submitted successfully: ${emailState.value}, ${usernameState.value}")
                            } else {
                                confirmPasswordState.error = "Passwords do not match"
                            }
                        }
                    }
                }
            }
        ) {
            Text("Submit")
        }
    }
}

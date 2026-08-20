package com.jpromi.spaceview.elements.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppColor

data class TextInputRules(
    val regex: Regex? = null,
    val maxLength: Int? = null,
    val allowEmpty: Boolean = true,
    val errorMessage: String = "Ungueltige Eingabe",
) {
    fun sanitize(value: String): String {
        return maxLength?.let(value::take) ?: value
    }

    fun isValid(value: String): Boolean {
        if (value.isEmpty()) {
            return allowEmpty
        }

        if (maxLength != null && value.length > maxLength) {
            return false
        }

        if (regex != null && !regex.matches(value)) {
            return false
        }

        return true
    }
}

@Composable
fun SettingsTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    rules: TextInputRules = TextInputRules(),
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    placeholder: String? = null,
) {
    val sanitizedValue = rules.sanitize(value)
    val isValid = rules.isValid(sanitizedValue)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = AppColor.textColor,
            modifier = Modifier.padding(start = 5.dp)
        )

        OutlinedTextField(
            value = sanitizedValue,
            onValueChange = { input ->
                onValueChange(rules.sanitize(input))
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            shape = RoundedCornerShape(8.dp),
            isError = !isValid,
            singleLine = singleLine,
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        color = AppColor.textColor.copy(alpha = .5f),
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AppColor.textColor,
                unfocusedTextColor = AppColor.textColor,
                disabledTextColor = AppColor.textColor.copy(alpha = .75f),
                errorTextColor = AppColor.textColor,
                focusedContainerColor = AppColor.background,
                unfocusedContainerColor = AppColor.background,
                disabledContainerColor = AppColor.background,
                errorContainerColor = AppColor.background,
                focusedBorderColor = AppColor.borderSettings,
                unfocusedBorderColor = AppColor.borderSettings,
                disabledBorderColor = AppColor.borderSettings,
                errorBorderColor = AppColor.busyTagBackground,
                cursorColor = AppColor.textColor,
                errorCursorColor = AppColor.busyTagBackground,
            ),
            supportingText = {
                if (!isValid) {
                    Text(
                        text = rules.errorMessage,
                        color = AppColor.busyTagBackground,
                    )
                }
            },
        )
    }
}

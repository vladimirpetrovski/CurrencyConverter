package com.vladimirpetrovski.currencyconverter.ui.utils

import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun Fragment.showSnackBar(
    snackBarText: String,
    timeLength: Int,
    action: String? = null,
    listener: () -> Unit?
) {
    activity?.let {
        val snackbar = Snackbar.make(
            it.findViewById(android.R.id.content),
            snackBarText,
            timeLength
        )
        if (action != null) {
            snackbar.setAction(action) {
                listener()
            }
        }
        snackbar.show()
    }
}

fun EditText.placeCursorToEnd() {
    this.setSelection(this.text.length)
}
package com.example.nwow.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object DialogController {

    private lateinit var dialogConfirmation: AlertDialog

    fun showDialogConfirmation(
        context: Context,
        title: String,
        msg: String,
        negativeBtnText: String,
        positiveBtnText: String,
        negativeCallback: () -> Unit,
        positiveCallback: () -> Unit

    ): AlertDialog {
        dialogConfirmation = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setNegativeButton(negativeBtnText) { _, _ -> negativeCallback() }
            .setPositiveButton(positiveBtnText) { dialog: DialogInterface, _ -> positiveCallback() }
            .setCancelable(false)
            .create()
            .apply {
                setCanceledOnTouchOutside(false)
                show()
            }
        return dialogConfirmation
    }

}
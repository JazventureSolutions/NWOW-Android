package com.example.nwow.utils

import android.app.Activity
import android.app.ProgressDialog
import android.app.UiModeManager
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import com.google.android.material.snackbar.Snackbar

class Utility {

    companion object {

        fun initProgressDialog(context: Context?): ProgressDialog {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Please Wait...")
            progressDialog.setCancelable(false)
            return progressDialog
        }

        fun showSnackBarOnRelative(relativeLayout: RelativeLayout, message: String?) {
            Snackbar.make(relativeLayout, message!!, Snackbar.LENGTH_SHORT).show()
        }

        fun showSnackBar(constraintLayout: ConstraintLayout, message: String?) {
            Snackbar.make(constraintLayout, message!!, Snackbar.LENGTH_SHORT).show()
        }

        fun turnOffDarkTheme(context: Context?) {
            val uiModeManager = context?.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
                uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
            }
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            activity.currentFocus?.let {
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }
}
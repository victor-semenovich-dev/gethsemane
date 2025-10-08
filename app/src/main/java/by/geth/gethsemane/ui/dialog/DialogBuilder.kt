package by.geth.gethsemane.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import by.geth.gethsemane.R
import by.geth.gethsemane.util.dpToPx

class DialogBuilder(context: Context): AlertDialog.Builder(context) {

    private var mDontAskAgain = false

    interface OnClickListener {
        fun onClick(dontAskAgain: Boolean)
    }

    override fun setTitle(titleId: Int): DialogBuilder { super.setTitle(titleId); return this }

    override fun setMessage(messageId: Int): DialogBuilder { super.setMessage(messageId); return this }

    fun setPositiveButton(textId: Int, listener: OnClickListener): DialogBuilder {
        super.setPositiveButton(textId) { _, _ ->
            listener.onClick(mDontAskAgain)
        }
        return this
    }

    fun setNegativeButton(textId: Int, listener: OnClickListener): DialogBuilder {
        super.setNegativeButton(textId) { _, _ ->
            listener.onClick(mDontAskAgain)
        }
        return this
    }

    override fun setNeutralButton(textId: Int, listener: DialogInterface.OnClickListener?): DialogBuilder {
        super.setNeutralButton(textId, listener)
        return this
    }

    fun addDontAskAgainOption(): DialogBuilder {
        val checkbox = CheckBox(context)
        val lparams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        checkbox.layoutParams = lparams
        checkbox.setText(R.string.dont_ask_again)
        checkbox.setOnCheckedChangeListener {_, isChecked ->
            mDontAskAgain = isChecked
        }
        val checkboxContainer = FrameLayout(context)
        checkboxContainer.setPadding(16.dpToPx, 0, 0, 0)
        checkboxContainer.addView(checkbox)
        setView(checkboxContainer)
        return this
    }
}
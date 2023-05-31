package academy.bangkit.capstone.dietin.components

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout

class DietinTextInputLayout: TextInputLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun setError(error: CharSequence?) {
        super.setError(error)
        isErrorEnabled = error != null
    }

    init {
        isHintEnabled = true
    }
}
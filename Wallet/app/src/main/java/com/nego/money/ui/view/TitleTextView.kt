package com.nego.money.ui.view

import android.content.Context
import android.widget.TextView
import android.graphics.Typeface
import android.content.res.TypedArray
import android.util.AttributeSet
import com.nego.money.R


/**
 * Created by tommaso on 22/07/17.
 */

class TitleTextView : TextView {
    constructor (context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context): super(context)

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            setTypeface(Typeface.createFromAsset(context.assets, "fonts/title_typeface.ttf"))
        }
    }

}

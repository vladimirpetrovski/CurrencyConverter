package com.vladimirpetrovski.currencyconverter.ui.utils

import android.graphics.Bitmap
import com.squareup.picasso.Transformation

class CropBitmapTransformation : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val croppedBitmap = Bitmap.createBitmap(
            source,
            0,
            40,
            source.width,
            source.height - 80
        )

        source.recycle()

        return croppedBitmap
    }

    override fun key(): String {
        return "CropCircleTransformation()"
    }
}
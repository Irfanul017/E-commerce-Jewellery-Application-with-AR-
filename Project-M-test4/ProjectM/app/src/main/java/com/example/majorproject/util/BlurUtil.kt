package com.example.majorproject.util

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import android.renderscript.Element

object BlurUtil {
    fun applyBlur(context: Context, imageView: ImageView, radius: Float) {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createFromBitmap(rs, outputBitmap)
        val blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blur.setRadius(radius)
        blur.setInput(input)
        blur.forEach(output)
        output.copyTo(outputBitmap)

        imageView.setImageBitmap(outputBitmap)
        rs.destroy()
    }
}

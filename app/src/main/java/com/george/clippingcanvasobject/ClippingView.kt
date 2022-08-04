package com.george.clippingcanvasobject

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.green

class ClippingView @JvmOverloads constructor(
    context: Context,
    attrs:AttributeSet? = null,
    defStyleAttr: Int = 0
) :View (context, attrs){

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    private val path = Path()

    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    private val rejectRow = rowFour + rectInset + 2*clipRectBottom

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        with(canvas) {
            drawBackAndUnclippedRectangle()
            drawDifferenceClippingExample()
            drawCircularClippingExample()
            drawIntersectionClippingExample()
            drawCombinedClippingExample()
            drawRoundedRectangleClippingExample()
            drawOutsideClippingExample()
            drawSkewedTextExample()
            drawTranslatedTextExample()
            drawQuickRejectExample()
        }

    }

    private fun Canvas.drawClippedRectangle() {
        drawRect(
            clipRectLeft,clipRectTop,clipRectRight,clipRectBottom,paint
        )
        drawColor(Color.WHITE)
        paint.color = Color.RED
        drawLine(clipRectLeft,clipRectTop,clipRectRight,clipRectBottom,paint)
        paint.color = Color.GREEN
        drawCircle(circleRadius, clipRectBottom - circleRadius, circleRadius, paint)
        paint.color = Color.BLUE
        paint.textAlign = Paint.Align.RIGHT
        drawText(context.getString(R.string.clipping),clipRectRight,textOffset,paint)
    }

    private fun Canvas.drawBackAndUnclippedRectangle() {
        drawColor(Color.GRAY)
        save()
        translate(columnOne, rowOne)
        drawClippedRectangle()
        restore()
    }

    private fun Canvas.drawDifferenceClippingExample() {
        save()
        // Move the origin to the right for the next rectangle.
        translate(columnOne, rowOne)
        // Use the subtraction of two clipping rectangles to create a frame.
        clipRect(
            2 * rectInset, 2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .DIFFERENCE) was deprecated in API level 26. The recommended
        // alternative method is clipOutRect(float, float, float, float),
        // which is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            clipRect(
                4 * rectInset , 4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
                Region.Op.DIFFERENCE
            )
        } else {
            clipOutRect(
                4 * rectInset , 4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
            )
        }

        drawClippedRectangle()
        restore()
    }

    private fun Canvas.drawCircularClippingExample() {
        save()
        translate(columnOne, rowTwo)
        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()
        path.addCircle(
            circleRadius,clipRectBottom - circleRadius,
            circleRadius,Path.Direction.CCW
        )
        // The method clipPath(path, Region.Op.DIFFERENCE) was deprecated in
        // API level 26. The recommended alternative method is
        // clipOutPath(Path), which is currently available in
        // API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            clipPath(path, Region.Op.DIFFERENCE)
        } else {
            clipOutPath(path)
        }
        drawClippedRectangle()
        restore()
    }

    private fun Canvas.drawIntersectionClippingExample() {
        save()
        translate(columnTwo,rowTwo)
        clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .INTERSECT) was deprecated in API level 26. The recommended
        // alternative method is clipRect(float, float, float, float), which
        // is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom,
                Region.Op.INTERSECT
            )
        } else {
            clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom
            )
        }
        drawClippedRectangle()
        restore()
    }

    private fun Canvas.drawCombinedClippingExample() {
        save()
        translate(columnOne, rowThree)
        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + circleRadius + rectInset,
            circleRadius,Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset,Path.Direction.CCW
        )
        clipPath(path)
        drawClippedRectangle()
        restore()
    }

    private fun Canvas.drawRoundedRectangleClippingExample() {
        save()
        translate(columnTwo,rowThree)
        path.rewind()
        path.addRoundRect(
            rectF,clipRectRight / 4,
            clipRectRight / 4, Path.Direction.CCW
        )
        clipPath(path)
        drawClippedRectangle()
        restore()
    }

    private fun Canvas.drawOutsideClippingExample() {
        save()
        translate(columnOne,rowFour)
        clipRect(2 * rectInset,2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset)
        drawClippedRectangle()
        restore()
    }

    private fun Canvas.drawSkewedTextExample() {
        save()
        paint.color = Color.YELLOW
        paint.textAlign = Paint.Align.RIGHT
        // Position text.
        translate(columnTwo, textRow)
        // Apply skew transformation.
        skew(0.2f, 0.3f)
        drawText(context.getString(R.string.skewed),
            clipRectLeft, clipRectTop, paint)
        restore()
    }

    private fun Canvas.drawTranslatedTextExample() {
        save()
        paint.color = Color.GREEN
        // Align the RIGHT side of the text with the origin.
        paint.textAlign = Paint.Align.LEFT
        // Apply transformation to canvas.
        translate(columnTwo,textRow)
        // Draw text.
        drawText(context.getString(R.string.translated),
            clipRectLeft,clipRectTop,paint)
        restore()
    }

    private fun Canvas.drawQuickRejectExample() {
        val inClipRectangle = RectF(clipRectRight / 2,
            clipRectBottom / 2,
            clipRectRight * 2,
            clipRectBottom * 2)

        val notInClipRectangle = RectF(RectF(clipRectRight+1,
            clipRectBottom+1,
            clipRectRight * 2,
            clipRectBottom * 2))

        save()
        translate(columnOne, rejectRow)
        clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )
        if (quickReject(
                inClipRectangle, Canvas.EdgeType.AA)) {
            drawColor(Color.WHITE)
        }
        else {
            drawColor(Color.BLACK)
            drawRect(inClipRectangle, paint
            )
        }
        restore()
    }
}
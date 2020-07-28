package kusu.thegreenway.common.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kusu.thegreenway.common.R
import kusu.thegreenway.common.dp


class DiscreteLineView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private var mPaint: Paint = Paint()
    private var mPath = Path()
    private val fillBottom: Boolean

    init {
        val segment: Float
        val deviation: Float
        val color: Int
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.DiscreteLineViewAttrs)
            try {
                fillBottom = attributes.getBoolean(R.styleable.DiscreteLineViewAttrs_fillBottom, DEFAULT_FILL_BOTTOM)
                segment = attributes.getDimension(R.styleable.DiscreteLineViewAttrs_segment, DEFAULT_SEGMENT)
                deviation = attributes.getDimension(R.styleable.DiscreteLineViewAttrs_deviation, DEFAULT_DEVIATION)
                color = attributes.getColor(R.styleable.DiscreteLineViewAttrs_fillColor, DEFAULT_COLOR)
            } finally {
                attributes.recycle()
            }
        } else {
            fillBottom = DEFAULT_FILL_BOTTOM
            segment = DEFAULT_SEGMENT
            deviation = DEFAULT_DEVIATION
            color = DEFAULT_COLOR
        }
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.pathEffect = DiscretePathEffect(segment, deviation)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mPath.reset()
        mPath.moveTo(-DELTA, if (fillBottom) -DELTA else (measuredHeight + DELTA))
        mPath.lineTo(-DELTA, measuredHeight / 2f)
        mPath.lineTo(measuredWidth + DELTA, measuredHeight / 2f)
        mPath.lineTo(measuredWidth + DELTA, if (fillBottom) -DELTA else (measuredHeight + DELTA))
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
    }

    companion object {
        val DELTA = 50f.dp
        val DEFAULT_SEGMENT = 2f.dp
        val DEFAULT_DEVIATION = 2f.dp
        val DEFAULT_FILL_BOTTOM = true
        val DEFAULT_COLOR = Color.WHITE
    }
}
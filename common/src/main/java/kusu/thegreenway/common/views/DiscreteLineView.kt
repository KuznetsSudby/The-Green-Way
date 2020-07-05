package kusu.thegreenway.common.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kusu.thegreenway.common.R
import kusu.thegreenway.common.dp


class DiscreteLineView : View {

    private var mPaint: Paint = Paint()
    private var mPath = Path()
    private val top: Boolean

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val segment: Float
        val deviation: Float
        val color: Int

        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.DiscreteLineViewAttrs)
            top = attributes.getBoolean(R.styleable.DiscreteLineViewAttrs_top, DEFAULT_TOP)
            segment = attributes.getDimension(R.styleable.DiscreteLineViewAttrs_segment, DEFAULT_SEGMENT)
            deviation = attributes.getDimension(R.styleable.DiscreteLineViewAttrs_deviation, DEFAULT_DEVIATION)
            color = attributes.getColor(R.styleable.DiscreteLineViewAttrs_fill, DEFAULT_COLOR)
            attributes.recycle()
        } else {
            top = DEFAULT_TOP
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
        mPath = Path()
        mPath.moveTo(-DELTA, if (top) -DELTA else (measuredHeight + DELTA))
        mPath.lineTo(-DELTA, measuredHeight / 2f)
        mPath.lineTo(measuredWidth + DELTA, measuredHeight / 2f)
        mPath.lineTo(measuredWidth + DELTA, if (top) -DELTA else (measuredHeight + DELTA))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mPath, mPaint)
    }

    companion object {
        val DELTA = 50f.dp
        val DEFAULT_SEGMENT = 2f.dp
        val DEFAULT_DEVIATION = 2f.dp
        val DEFAULT_TOP = true
        val DEFAULT_COLOR = Color.WHITE
    }
}
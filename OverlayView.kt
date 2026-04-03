import android.content.Context
import android.graphics.*
import android.view.View

class OverlayView(context: Context) : View(context) {

    private var target: Box? = null
    private var tracking = true

    private val circlePaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val boxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    fun setTarget(t: Box?, isTracking: Boolean) {
        target = t
        tracking = isTracking
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = 200f

        // 중앙 에임보정 반경 표시 원
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        if (!tracking) return

        // 타겟 히트박스
        target?.let {
            val left = it.x - 50
            val top = it.y - 50
            val right = it.x + 50
            val bottom = it.y + 50

            canvas.drawRect(left, top, right, bottom, boxPaint)

            // 중앙->타겟 라인
            canvas.drawLine(centerX, centerY, it.x.toFloat(), it.y.toFloat(), boxPaint)
        }
    }
}
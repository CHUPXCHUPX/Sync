import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.WindowManager

class OverlayService : Service() {

    companion object {
        var instance: OverlayService? = null
    }

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: OverlayView

    override fun onCreate() {
        super.onCreate()

        // static instance 연결
        instance = this

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = OverlayView(this)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 메모리 누수 차단 코드
        instance = null
        windowManager.removeView(overlayView)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // 외부호출 업데이트 
    fun update(target: Box?, tracking: Boolean) {
        overlayView.setTarget(target, tracking)
    }
}
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mediaProjectionHelper: MediaProjectionHelper
    private lateinit var capture: CaptureService

    private val REQUEST_CODE = 1000

    // 에임 트래킹 로직
    private val detector = Detector()
    private val targetManager = TargetManager()
    private val motionDetector = MotionDetector()
    private val tracker = SmoothTracker()

    private var trackingEnabled = true
    private var lastDetectionTime = 0L
    private val detectionInterval = 300L
    private var cachedObjects: List<Box> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 오버레이 권한 요청
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        // 미디아프로젝션헬퍼 캡쳐서비스 초기화
        mediaProjectionHelper = MediaProjectionHelper(this)
        capture = CaptureService(mediaProjectionHelper)

        // 미디어프로젝션 권한 요청
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as android.media.projection.MediaProjectionManager
        startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mediaProjectionHelper.startProjection(resultCode, data)

            // 캡처 후 앱 시작
            startApp()
        }
    }

    private fun startApp() {

        Thread {
            while (true) {

                val startTime = System.currentTimeMillis()

                val frame = capture.getFrame() ?: continue

                val motion = motionDetector.calculate(frame)
                trackingEnabled = motion < 50

                val currentTime = System.currentTimeMillis()

                if (trackingEnabled && currentTime - lastDetectionTime > detectionInterval) {
                    cachedObjects = detector.detect(frame)
                    lastDetectionTime = currentTime
                }

                val rawTarget = targetManager.select(cachedObjects, trackingEnabled)
                val smoothTarget = tracker.update(rawTarget)

                // 오버레이서비스로 에임 업데이트
                OverlayService.instance?.update(smoothTarget, trackingEnabled)

                val elapsed = System.currentTimeMillis() - startTime
                val sleepTime = 70 - elapsed

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime)
                }
            }
        }.start()
    }
}

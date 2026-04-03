import android.graphics.Bitmap

class CaptureService(private val helper: MediaProjectionHelper) {

    fun getFrame(): Bitmap? {
        return helper.captureFrame()
    }
}

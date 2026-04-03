import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import java.nio.ByteBuffer

class MediaProjectionHelper(private val context: Context) {

    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null

    private var reusableBitmap: Bitmap? = null

    private val width = 320
    private val height = 180

    fun startProjection(resultCode: Int, data: Intent) {

        val manager =
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        mediaProjection = manager.getMediaProjection(resultCode, data)

        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            2
        )

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            1,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )
    }

    fun captureFrame(): Bitmap? {

        val image = imageReader?.acquireLatestImage() ?: return null

        val plane = image.planes[0]
        val buffer: ByteBuffer = plane.buffer

        val pixelStride = plane.pixelStride
        val rowStride = plane.rowStride
        val rowPadding = rowStride - pixelStride * width

        if (reusableBitmap == null) {
            reusableBitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
        }

        reusableBitmap!!.copyPixelsFromBuffer(buffer)

        image.close() 
        // 메모리 최적화->업데이트 버전에서는 방식 바꿔보기

        return Bitmap.createBitmap(reusableBitmap!!, 0, 0, width, height)
    }

    fun stop() {
        virtualDisplay?.release()
        mediaProjection?.stop()
    }
}
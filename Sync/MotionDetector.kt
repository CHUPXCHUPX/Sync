class MotionDetector {

    private var lastFrame: Any? = null

    fun calculate(currentFrame: Any): Int {

        if (lastFrame == null) {
            lastFrame = currentFrame
            return 0
        }

        val motionValue = (0..100).random() // 테스트용 (추후변경)

        lastFrame = currentFrame

        return motionValue
    }
}

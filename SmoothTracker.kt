data class Point(var x: Double, var y: Double)

class SmoothTracker {

    private var current = Point(0.0, 0.0)
    private var velocity = Point(0.0, 0.0)
    private var initialized = false

    fun update(newTarget: Box?): Box? {

        if (newTarget == null) return null

        val target = Point(newTarget.x.toDouble(), newTarget.y.toDouble())

        // 기본 프리셋 설정
        if (!initialized) {
            current = target
            initialized = true
            return Box(current.x.toInt(), current.y.toInt())
        }

        // 속도 계산
        velocity.x = target.x - current.x
        velocity.y = target.y - current.y

        // 예측 에임
        current.x += velocity.x * 0.3
        current.y += velocity.y * 0.3

        return Box(current.x.toInt(), current.y.toInt())
    }
}
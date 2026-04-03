class TargetManager {

    private var currentTarget: Box? = null

    private val centerX = 540
    private val centerY = 960
    private val radius = 200 // 중앙 원 범위:배포기기따라 바꾸기

    fun select(objects: List<Box>, trackingEnabled: Boolean): Box? {

        if (!trackingEnabled) {
            return currentTarget
        }

        //기존 타겟 유지
        currentTarget?.let { target ->

            for (obj in objects) {
                val dist = distance(target, obj)

                if (dist < 100) {
                    currentTarget = obj
                    return currentTarget
                }
            }
        }

        //중앙 원 안에 있는 개채만 트래킹
        val candidates = objects.filter {
            distance(it, Box(centerX, centerY)) < radius
        }

        currentTarget = findClosest(candidates)

        return currentTarget
    }

    private fun findClosest(objects: List<Box>): Box? {

        var closest: Box? = null
        var minDist = Double.MAX_VALUE

        for (obj in objects) {
            val dist = distance(obj, Box(centerX, centerY))

            if (dist < minDist) {
                minDist = dist
                closest = obj
            }
        }

        return closest
    }

    private fun distance(a: Box, b: Box): Double {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return Math.sqrt((dx * dx + dy * dy).toDouble())
    }
}
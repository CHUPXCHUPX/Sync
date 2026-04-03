data class Box(val x: Int, val y: Int)

class Detector {

    fun detect(frame: Any): List<Box> {

        return listOf(
            Box(500, 500),
            Box(600, 800),
            Box(300, 1000)
        )
    }
}

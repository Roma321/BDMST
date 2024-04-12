import kotlin.math.abs

data class Point(var x: Int, var y: Int, var vNum: Int) {
    fun distance(other: Point): Int {
        return abs(this.x - other.x) + abs(this.y - other.y)
    }

    constructor(str: String, vNum: Int) : this(0, 0, 0) {
        val parts = str.split(Regex("\\s+")).map { it.toInt() }
        if (parts.size >= 2) {
            this.x = parts[0]
            this.y = parts[1]
            this.vNum = vNum
        }
    }

    constructor(x: Int, y: Int) : this(x, y, -1)

    fun belongs(inR: Rectangle, notInR: Rectangle? = null): Boolean {
        val isInsideInR = this.x >= inR.fromX && this.x <= inR.toX && this.y >= inR.fromY && this.y <= inR.toY
        if (notInR == null) {
            return isInsideInR
        }
        val isOutsideNotInR =
            this.x > notInR.toX || this.x < notInR.fromX || this.y > notInR.toY || this.y < notInR.fromY
//        println(listOf(this, inR, notInR))
//        println(this.x < notInR.fromX)
//        println(this.x > notInR.toX)
//        println(this.y < notInR.fromY)
//        println(this.y > notInR.toY)
        return isInsideInR && isOutsideNotInR
    }
}
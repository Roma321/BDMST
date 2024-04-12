data class Rectangle(val fromX: Int, val toX: Int, val fromY: Int, val toY: Int) {
    fun extendTo(n: Int): Rectangle {
        return Rectangle(fromX - n, toX + n, fromY - n, toY + n)
    }
}

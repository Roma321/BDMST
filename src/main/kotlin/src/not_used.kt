fun interestingShuffle(points: List<Point>): List<Point> {
    val squareNumberInRow = points.size / 16
    val squareSize = vertex_number / squareNumberInRow
    val rectangles = (0..<squareNumberInRow * squareNumberInRow).map {
        Rectangle(
            squareSize * (it % squareNumberInRow),
            squareSize * (it % squareNumberInRow) + squareSize - 1,
            it / squareNumberInRow,
            it / squareNumberInRow + squareSize - 1,
        )
    }
    return points.groupBy { point -> rectangles.indexOfFirst { point.belongs(it) } }.mapValues { it.value.shuffled() }
        .map { it.value }.shuffled().flatten()
}

fun <T> List<T>.shuffledWithinLimits(): List<T> {
    val limit = this.size / 5
    val shuffledList = this.toMutableList()
    for (i in shuffledList.indices) {
        val randomIndex = (i..Math.min(i + limit, shuffledList.lastIndex)).random()
        shuffledList[i] = shuffledList[randomIndex].also { shuffledList[randomIndex] = shuffledList[i] }
    }
    return shuffledList
}

private fun pointsToMatrix(points: List<Point>): Array<Array<Int>> {
    val matrix = Array(points.size) { Array(points.size) { 0 } }
    for (rowIndex in points.indices) {
        for (colIndex in points.indices) {
            matrix[rowIndex][colIndex] = points[rowIndex].distance(points[colIndex])
        }
    }
    return matrix
}

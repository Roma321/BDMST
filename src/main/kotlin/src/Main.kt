import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.math.min
import kotlinx.coroutines.*
import java.util.concurrent.Executors

typealias Edge = Pair<Int, Int>

// [206170, Point(x=3124, y=259, vNum=2413)]
private const val limitFullShuffle = 760
const val vertex_number = 4096
const val checkAll = true
var aaa = Int.MAX_VALUE
var count = 0L
val bestStartPoint = Point(
    x = 2064,
    y = 1981,
    vNum = 2640
) // для прямоугольников 4096 и для сортировки по центру 4096 !!!206978, не забыть обновить
//val bestStartPoint = Point(x = 42, y = 45, vNum = 36) // для прямоугольников 64

// [209163, Point(x=2064, y=1981, vNum=2640)]
fun realMain() {
    val fileName = "Benchmark/Taxicab_${vertex_number}.txt" // Replace with the actual file name or path
    var points = readFromFile(fileName)
//    points = points.sortedBy {
//        points.sumOf { listPoint -> listPoint.distance(it) } / points.size + it.distance(
//            Point(
//                1028,
//                1028
//            )
//        ) + it.distance(Point(3050, 3050))
//    }
//    println(points)
//    tryGoFromCenter(points)


//    val anchor1 = points.minBy { it.distance(Point(2048, 2048)) }
//    val anchor2 = points.minBy { it.distance(Point(1024, 1024)) }
//    val anchor3 = points.minBy { it.distance(Point(3076, 3076)) }
//    val anchor4 = points.minBy { it.distance(Point(1024, 3076)) }
//    val anchor5 = points.minBy { it.distance(Point(3076, 1024)) }

//    val lst = listOf(anchor1, anchor2, anchor3, anchor4, anchor5)
//    println(generateSubsets(lst))
//    for (anchors in generateSubsets(lst).filter { it.isNotEmpty() }){
//        points = points.sortedBy { point -> anchors.minOf { anchor -> anchor.distance(point) } }
//        val tree = TreeVertex(anchors[0], 0)
//        for (anchor in anchors.subList(1, anchors.size)){
//            tree.connectPoint(anchor)
//        }
//        points = points.subList(anchors.size, points.size)
//        val a = tryGoFromCenter(points)
//        println("RESULT!!!!!!!!!!!!!!!!!!!")
//        println(a)
//    }
//    points = points.sortedBy { point -> lst.minOf { anchor -> anchor.distance(point) } }
//    println(points)
//    val tree = TreeVertex(anchor1, 0)
//    tree.connectPoint(anchor2)
//    tree.connectPoint(anchor3)
//    tree.connectPoint(anchor4)
//    tree.connectPoint(anchor5)
//    points = points.subList(5, points.size)
//    tryGoFromCenter(points)
//    points = points.sortedBy { }
//    val a =
//        goFromThisAsCenterSorted(points, Point(x = 3124, y = 259, vNum = 2413), Point(x = 3124, y = 259, vNum = 2413))
//    println(a)
//    tryGoFromCenter(points)
//    println(points)

//    val points = listOf(Point(1, 1), Point(2, 4), Point(3, 3), Point(6, 22), Point(5, 2), Point(9, 9))
//    val points = listOf(Point(1, 1), Point(2, 4), Point(3, 3))

//    val bp = points.minBy { goFromThisA
//    sCenterSorted(points, it, getFieldCenter(points)) }
//    val u = Point(x = 45, y = 19, vNum = 14)
//    val a = goFromThisAsCenterByRectangles(points, bestStartPoint, getFieldCenter(points))
//    val a = goFromThisAsCenterSorted(points, bestStartPoint, getFieldCenter(points))
//    println(a)
//    solveSearchTree(points)
//    while (true) {
//        val r = goFromThisAsCenterSorted(points, bestStartPoint, getFieldCenter(points))
//        if (r < aaa) {
//            aaa = r
//            println(aaa)
//        }
//    }
}

fun tryGoFromCenter(points: List<Point>): Int {
    val center = getFieldCenter(points)
    val centerPoint = points.minBy { it.distance(center) }
    var bestPoint = Point(1, 0)
    if (checkAll) {
        var bestSum = 99999999
        for ((idx, point) in points.withIndex()) {
            println(idx)
//            val a = goFromThisAsCenterByRectangles(points, point, center)
            val a = goFromThisAsCenterSorted(points, point, centerPoint)
            if (a < bestSum) {
                bestSum = a
                bestPoint = point
            }
        }
        goFromThisAsCenterSorted(points, bestPoint, centerPoint)
        println(listOf(bestSum, bestPoint))
        return bestSum
    } else {
        val a = goFromThisAsCenterByRectangles(points, bestStartPoint, center)
        println(a)
        return a
    }
}

fun getFieldCenter(points: List<Point>): Point {
    val minX = points.minOf { it.x }
    val minY = points.minOf { it.y }
    val maxX = points.maxOf { it.x }
    val maxY = points.maxOf { it.y }
    val center = Point((maxX - minX) / 2, (maxY - minY) / 2, -1)
    return center
}

fun goFromThisAsCenterSorted(points: List<Point>, startPoint: Point, realCenter: Point): Int {
    val treeCenter = TreeVertex(startPoint, 0)
    val maxD = points.size / 16 / 2 // Расстояние от центра
    val edgesList = mutableListOf<Edge>()
    val pointsWithoutCenter =
        points.filter { it.vNum != startPoint.vNum }//.sortedBy { it.distance(startPoint) }
//        points.filter { it.vNum != startPoint.vNum }.sortedBy { it.distance(realCenter) }

    for (point in pointsWithoutCenter) {
        val closestVertexFromTree = treeCenter.closestVertex(point, maxD)
        closestVertexFromTree.connectPoint(point)
        edgesList.add(Edge(closestVertexFromTree.point.vNum, point.vNum))
    }

//    treeCenter.saveToFile()
    return treeCenter.treeWeight()
}

private fun goFromThisAsCenterByRectangles(points: List<Point>, centerPoint: Point, realCenter: Point): Int {
    val treeCenter = TreeVertex(centerPoint, 0)

    var prevRectangle = Rectangle(realCenter.x, realCenter.x, realCenter.y, realCenter.y)
    val maxD = points.size / 16 / 2 // Расстояние от центра
    val rectanglesNumber = maxD * 2
    val rectangleStep = points.size / rectanglesNumber
    val pointsInTree = mutableListOf<Point>()
    pointsInTree.add(treeCenter.point)
    val pointsWithoutCenter = points.filter { it.vNum != centerPoint.vNum }
    for (i in 1..rectanglesNumber) {
        val thisStepRectangle = prevRectangle.extendTo(rectangleStep)

        for (point in pointsWithoutCenter) {
            if (point.belongs(thisStepRectangle, prevRectangle)) {

                val closestVertexFromTree =
                    treeCenter.closestVertex(point, maxD)//или можно попробовать i для небьольших графов
                closestVertexFromTree.connectPoint(point)
            }
        }

        prevRectangle = thisStepRectangle
    }
//    val vertexesToRearrange = treeCenter.vertexesToRearrange(maxD)
//    vertexesToRearrange.forEach { vertexToRearrange ->
//        edgesList.removeIf { it.second == vertexToRearrange.point.vNum || it.first == vertexToRearrange.point.vNum }
//    }
    val sum = treeCenter.treeWeight()

//    val edgesList = treeCenter.edges()
//    if (!checkAll) {
//        val bufferedWriter = BufferedWriter(FileWriter(File("output/Mamaev_${vertex_number}_1.txt")))
//        bufferedWriter.write("c Вес дерева = ${sum}, диаметр = ${maxD * 2}\np ${vertex_number} ${vertex_number - 1}\n")
//        for (edge in edgesList) {
//            bufferedWriter.write("e ${edge.first} ${edge.second}\n")
//        }
//        bufferedWriter.flush()
//        bufferedWriter.close()
//    }

//    treeCenter.getD()
//    val toRearrange = treeCenter.vertexesToRearrange(maxD)
//    println(toRearrange[0])
//    println(toRearrange[0].parent)
//    treeCenter.detachByVnum(toRearrange[0].point.vNum)
//    println(treeCenter.closestVertex(toRearrange[0].point, maxD))
    return sum
}

fun readFromFile(fileName: String): List<Point> {
    val points = mutableListOf<Point>()
    val file = File(fileName)
    if (!file.exists()) {
        println("File not found.")
        throw IOException()
    }

    val lines = file.readLines() // Read all lines from the file

    for ((idx, line) in lines.withIndex()) {
        if (idx == 0) continue
        points.add(Point(line, idx))
    }


    return points
}


fun main() {
//    realMain()
    bruteForceParallel()
}


fun bruteForceParallel() {
    val fileName = "Benchmark/Taxicab_${vertex_number}.txt" // Replace with the actual file name or path
    var points = readFromFile(fileName).sortedBy { it.distance(Point(x=3124, y=259, vNum=2413)) }
//    val p = points.find { it.vNum == 2008 }!!
//    points = points.sortedBy { it.distance(p) }

    val executor = Executors.newFixedThreadPool(16).asCoroutineDispatcher()

    runBlocking {
        repeat(16) {
            launch(executor) {

                while (true) {

//                    val shuffledPoints = interestingShuffle(points)
//                    val shuffledPoints = if (aaa > limitFullShuffle) points.shuffled() else points.shuffledWithinLimits()
                    val shuffledPoints = points.shuffledWithinLimits()

                    val treeCenter = TreeVertex(shuffledPoints[0], 0)
                    val maxD = shuffledPoints.size / 16 / 2 // Distance from the center
//                    val edgesList = mutableListOf<Edge>()
                    val pointsWithoutCenter = shuffledPoints.subList(1, shuffledPoints.size)
                    for (point in pointsWithoutCenter) {
                        val closestVertexFromTree = treeCenter.closestVertex(point, maxD)
                        closestVertexFromTree.connectPoint(point)
//                        edgesList.add(Edge(closestVertexFromTree.point.vNum, point.vNum))
                    }
                    count++
                    if (count % 1_000 == 0L) {
                        println(count)
                    }

                    val r = treeCenter.treeWeight()

                    // Synchronize access to aaa variable
                    if (r < aaa) {
//                        if (r < limitFullShuffle) {
//                            points = shuffledPoints
//                        }
                        println("count: ${count}")
                        aaa = r
                        println(treeCenter.edges())
                        println(shuffledPoints)
                        println(aaa)

                    }
                }

            }
        }
    }
    executor.close()
}

fun <T> generateSubsets(list: List<T>): List<List<T>> {
    if (list.isEmpty()) {
        return listOf(emptyList())
    }

    val firstElement = list[0]
    val remainingElements = list.drop(1)

    val subsetsWithoutFirst = generateSubsets(remainingElements)
    val subsetsWithFirst = subsetsWithoutFirst.map { it + firstElement }

    return subsetsWithoutFirst + subsetsWithFirst
}

fun <T> List<T>.shuffledWithinLimits(): List<T> {
    val limit = this.size / 2100
    val shuffledList = this.toMutableList()
    for (i in shuffledList.indices) {
        val randomIndex = (i..Math.min(i + limit, shuffledList.lastIndex)).random()
        shuffledList[i] = shuffledList[randomIndex].also { shuffledList[randomIndex] = shuffledList[i] }
    }
    return shuffledList
}
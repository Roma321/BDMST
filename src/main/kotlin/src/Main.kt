import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.math.min
import kotlinx.coroutines.*
import java.util.concurrent.Executors

typealias Edge = Pair<Int, Int>

private const val limitFullShuffle = 760
val vertex_number = 4096
val checkAll = true
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
    val points = readFromFile(fileName)
    val a =
        goFromThisAsCenterSorted(points, Point(x = 3124, y = 259, vNum = 2413), Point(x = 3124, y = 259, vNum = 2413))
    println(a)
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

fun tryGoFromCenter(points: List<Point>) {
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
    } else {
        val a = goFromThisAsCenterByRectangles(points, bestStartPoint, center)
        println(a)
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

private fun goFromThisAsCenterSorted(points: List<Point>, startPoint: Point, realCenter: Point): Int {
    val treeCenter = TreeVertex(startPoint, 0)
    val maxD = points.size / 16 / 2 // Расстояние от центра
    val edgesList = mutableListOf<Edge>()
    val pointsWithoutCenter =
        points.filter { it.vNum != startPoint.vNum }.sortedBy { it.distance(startPoint) }
//        points.filter { it.vNum != startPoint.vNum }.sortedBy { it.distance(realCenter) }

    for (point in pointsWithoutCenter) {
        val closestVertexFromTree = treeCenter.closestVertex(point, maxD)
        closestVertexFromTree.connectPoint(point)
        edgesList.add(Edge(closestVertexFromTree.point.vNum, point.vNum))
    }
    treeCenter.saveToFile()
    return treeCenter.treeWeight()
}

private fun goFromThisAsCenterByRectangles(points: List<Point>, centerPoint: Point, realCenter: Point): Int {
    val treeCenter = TreeVertex(centerPoint, 0)

    var prevRectangle = Rectangle(realCenter.x, realCenter.x, realCenter.y, realCenter.y)
    val maxD = points.size / 16 / 2 // Расстояние от центра
    val rectanglesNumber = maxD * 2
//    println(rectanglesNumber)
    val rectangleStep = points.size / rectanglesNumber
//    var sum = 0
//    var count = 0
    val pointsInTree = mutableListOf<Point>()
    pointsInTree.add(treeCenter.point)
//    val edgesList = mutableListOf<Edge>()
    val pointsWithoutCenter = points.filter { it.vNum != centerPoint.vNum }
    for (i in 1..rectanglesNumber) {
        val thisStepRectangle = prevRectangle.extendTo(rectangleStep)

        for (point in pointsWithoutCenter) {
            if (point.belongs(thisStepRectangle, prevRectangle)) {

                val closestVertexFromTree = treeCenter.closestVertex(point, maxD)//или можно попробовать i для небьольших графов
//                sum += closestVertexFromTree.point.distance(point)
//                count += 1
                closestVertexFromTree.connectPoint(point)
//                edgesList.add(Edge(closestVertexFromTree.point.vNum, point.vNum))
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
    realMain()
//    bruteForceParallel()
}



fun bruteForceParallel() {
    val fileName = "Benchmark/Taxicab_${vertex_number}.txt" // Replace with the actual file name or path
    var points = readFromFile(fileName).sortedBy { it.distance(Point(2048, 2048)) }

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
                    var breakFlag = false
                    for ((i, point) in pointsWithoutCenter.withIndex()) {
                        val closestVertexFromTree = treeCenter.closestVertex(point, maxD)
                        closestVertexFromTree.connectPoint(point)
                        if (treeCenter.treeWeight() > 11876) {
                            breakFlag = true
                            break
                        }
//                        edgesList.add(Edge(closestVertexFromTree.point.vNum, point.vNum))
                    }
                    count++
                    if (count % 10_000 == 0L) {
                        println(count)
                    }
                    if (breakFlag) {
                        continue
                    }
                    val r = treeCenter.treeWeight()

                    // Synchronize access to aaa variable
                    if (r < aaa) {
//                        if (r < limitFullShuffle) {
//                            points = shuffledPoints
//                        }
                        println("count: ${count}")
                        aaa = r
                        println(aaa)
                        println(treeCenter.edges())
                        println(shuffledPoints)
                    }
                }

            }
        }
    }
    executor.close()
}

val save753_64 =
    "[Point(x=42, y=45, vNum=36), Point(x=13, y=16, vNum=16), Point(x=4, y=5, vNum=56), Point(x=4, y=51, vNum=41), Point(x=42, y=5, vNum=13), Point(x=33, y=37, vNum=11), Point(x=51, y=30, vNum=19), Point(x=34, y=40, vNum=60), Point(x=47, y=52, vNum=43), Point(x=41, y=51, vNum=39), Point(x=0, y=55, vNum=51), Point(x=53, y=54, vNum=25), Point(x=50, y=49, vNum=3), Point(x=3, y=37, vNum=35), Point(x=51, y=20, vNum=44), Point(x=38, y=18, vNum=38), Point(x=19, y=14, vNum=8), Point(x=63, y=60, vNum=4), Point(x=15, y=13, vNum=37), Point(x=21, y=1, vNum=55), Point(x=53, y=1, vNum=31), Point(x=34, y=55, vNum=7), Point(x=50, y=39, vNum=15), Point(x=56, y=7, vNum=18), Point(x=12, y=22, vNum=52), Point(x=15, y=17, vNum=1), Point(x=44, y=48, vNum=45), Point(x=24, y=38, vNum=10), Point(x=32, y=12, vNum=57), Point(x=36, y=32, vNum=17), Point(x=54, y=1, vNum=63), Point(x=9, y=12, vNum=29), Point(x=32, y=40, vNum=30), Point(x=49, y=52, vNum=9), Point(x=25, y=14, vNum=47), Point(x=42, y=60, vNum=24), Point(x=53, y=50, vNum=21), Point(x=21, y=7, vNum=27), Point(x=23, y=22, vNum=33), Point(x=0, y=21, vNum=61), Point(x=4, y=26, vNum=64), Point(x=39, y=44, vNum=2), Point(x=51, y=45, vNum=46), Point(x=13, y=20, vNum=42), Point(x=34, y=53, vNum=48), Point(x=8, y=61, vNum=54), Point(x=14, y=53, vNum=58), Point(x=56, y=6, vNum=53), Point(x=61, y=34, vNum=50), Point(x=14, y=18, vNum=20), Point(x=52, y=47, vNum=32), Point(x=38, y=51, vNum=28), Point(x=58, y=63, vNum=5), Point(x=45, y=19, vNum=14), Point(x=25, y=36, vNum=62), Point(x=51, y=53, vNum=34), Point(x=39, y=51, vNum=6), Point(x=48, y=54, vNum=12), Point(x=53, y=30, vNum=26), Point(x=19, y=12, vNum=40), Point(x=41, y=2, vNum=59), Point(x=50, y=47, vNum=22), Point(x=45, y=48, vNum=49), Point(x=44, y=49, vNum=23)]\n"
val a1583_128 =
    "[Point(x=39, y=27, vNum=87), Point(x=95, y=101, vNum=58), Point(x=32, y=75, vNum=125), Point(x=3, y=26, vNum=110), Point(x=30, y=109, vNum=104), Point(x=74, y=97, vNum=54), Point(x=39, y=106, vNum=60), Point(x=36, y=37, vNum=88), Point(x=103, y=102, vNum=121), Point(x=30, y=24, vNum=66), Point(x=37, y=77, vNum=85), Point(x=30, y=16, vNum=120), Point(x=97, y=85, vNum=127), Point(x=2, y=44, vNum=47), Point(x=99, y=100, vNum=65), Point(x=97, y=97, vNum=14), Point(x=31, y=117, vNum=122), Point(x=100, y=95, vNum=100), Point(x=72, y=31, vNum=43), Point(x=35, y=31, vNum=15), Point(x=100, y=96, vNum=28), Point(x=8, y=85, vNum=128), Point(x=121, y=105, vNum=49), Point(x=70, y=15, vNum=21), Point(x=32, y=32, vNum=33), Point(x=27, y=65, vNum=92), Point(x=104, y=94, vNum=103), Point(x=37, y=48, vNum=56), Point(x=103, y=91, vNum=64), Point(x=16, y=110, vNum=35), Point(x=22, y=40, vNum=81), Point(x=2, y=84, vNum=94), Point(x=55, y=29, vNum=98), Point(x=33, y=69, vNum=53), Point(x=92, y=86, vNum=51), Point(x=1, y=122, vNum=67), Point(x=114, y=33, vNum=105), Point(x=39, y=44, vNum=37), Point(x=71, y=31, vNum=27), Point(x=110, y=14, vNum=71), Point(x=102, y=109, vNum=102), Point(x=16, y=96, vNum=31), Point(x=84, y=99, vNum=32), Point(x=116, y=59, vNum=108), Point(x=38, y=15, vNum=41), Point(x=51, y=97, vNum=38), Point(x=77, y=24, vNum=16), Point(x=1, y=76, vNum=40), Point(x=98, y=74, vNum=79), Point(x=92, y=89, vNum=2), Point(x=99, y=90, vNum=9), Point(x=47, y=34, vNum=90), Point(x=25, y=27, vNum=96), Point(x=110, y=100, vNum=80), Point(x=115, y=56, vNum=45), Point(x=76, y=11, vNum=20), Point(x=79, y=84, vNum=5), Point(x=110, y=27, vNum=1), Point(x=92, y=99, vNum=109), Point(x=96, y=98, vNum=106), Point(x=100, y=92, vNum=74), Point(x=21, y=62, vNum=117), Point(x=83, y=108, vNum=114), Point(x=28, y=35, vNum=8), Point(x=39, y=30, vNum=112), Point(x=26, y=29, vNum=83), Point(x=37, y=89, vNum=101), Point(x=0, y=82, vNum=78), Point(x=64, y=85, vNum=7), Point(x=122, y=104, vNum=70), Point(x=36, y=29, vNum=50), Point(x=101, y=102, vNum=29), Point(x=112, y=31, vNum=22), Point(x=5, y=47, vNum=111), Point(x=88, y=80, vNum=84), Point(x=90, y=97, vNum=118), Point(x=1, y=60, vNum=89), Point(x=78, y=22, vNum=95), Point(x=102, y=104, vNum=30), Point(x=78, y=36, vNum=34), Point(x=63, y=102, vNum=99), Point(x=28, y=102, vNum=24), Point(x=10, y=113, vNum=73), Point(x=13, y=66, vNum=62), Point(x=95, y=98, vNum=17), Point(x=59, y=38, vNum=23), Point(x=92, y=0, vNum=86), Point(x=36, y=3, vNum=44), Point(x=19, y=87, vNum=91), Point(x=8, y=20, vNum=25), Point(x=94, y=94, vNum=75), Point(x=123, y=40, vNum=107), Point(x=5, y=92, vNum=48), Point(x=6, y=14, vNum=3), Point(x=24, y=26, vNum=69), Point(x=100, y=101, vNum=76), Point(x=92, y=88, vNum=126), Point(x=107, y=28, vNum=113), Point(x=70, y=26, vNum=6), Point(x=122, y=89, vNum=116), Point(x=103, y=100, vNum=59), Point(x=76, y=53, vNum=115), Point(x=112, y=11, vNum=46), Point(x=40, y=39, vNum=68), Point(x=27, y=59, vNum=124), Point(x=89, y=65, vNum=123), Point(x=74, y=2, vNum=72), Point(x=97, y=99, vNum=12), Point(x=53, y=75, vNum=52), Point(x=45, y=60, vNum=26), Point(x=8, y=78, vNum=42), Point(x=72, y=99, vNum=93), Point(x=59, y=97, vNum=13), Point(x=86, y=72, vNum=57), Point(x=42, y=11, vNum=61), Point(x=53, y=49, vNum=18), Point(x=120, y=126, vNum=82), Point(x=14, y=127, vNum=63), Point(x=4, y=5, vNum=10), Point(x=100, y=40, vNum=77), Point(x=74, y=50, vNum=19), Point(x=72, y=82, vNum=97), Point(x=65, y=81, vNum=36), Point(x=60, y=70, vNum=4), Point(x=91, y=104, vNum=39), Point(x=43, y=76, vNum=11), Point(x=26, y=22, vNum=55), Point(x=122, y=108, vNum=119)]\n"
val a741_64_2 =
    "[Point(x=42, y=45, vNum=36), Point(x=13, y=16, vNum=16), Point(x=15, y=17, vNum=1), Point(x=15, y=13, vNum=37), Point(x=53, y=54, vNum=25), Point(x=23, y=22, vNum=33), Point(x=4, y=51, vNum=41), Point(x=39, y=51, vNum=6), Point(x=53, y=50, vNum=21), Point(x=42, y=5, vNum=13), Point(x=45, y=19, vNum=14), Point(x=32, y=40, vNum=30), Point(x=24, y=38, vNum=10), Point(x=56, y=6, vNum=53), Point(x=34, y=53, vNum=48), Point(x=8, y=61, vNum=54), Point(x=54, y=1, vNum=63), Point(x=51, y=30, vNum=19), Point(x=36, y=32, vNum=17), Point(x=51, y=45, vNum=46), Point(x=12, y=22, vNum=52), Point(x=34, y=40, vNum=60), Point(x=3, y=37, vNum=35), Point(x=58, y=63, vNum=5), Point(x=21, y=1, vNum=55), Point(x=44, y=49, vNum=23), Point(x=56, y=7, vNum=18), Point(x=63, y=60, vNum=4), Point(x=44, y=48, vNum=45), Point(x=19, y=12, vNum=40), Point(x=38, y=18, vNum=38), Point(x=47, y=52, vNum=43), Point(x=50, y=49, vNum=3), Point(x=49, y=52, vNum=9), Point(x=41, y=51, vNum=39), Point(x=53, y=1, vNum=31), Point(x=32, y=12, vNum=57), Point(x=42, y=60, vNum=24), Point(x=13, y=20, vNum=42), Point(x=52, y=47, vNum=32), Point(x=19, y=14, vNum=8), Point(x=14, y=18, vNum=20), Point(x=41, y=2, vNum=59), Point(x=45, y=48, vNum=49), Point(x=38, y=51, vNum=28), Point(x=0, y=55, vNum=51), Point(x=50, y=39, vNum=15), Point(x=48, y=54, vNum=12), Point(x=14, y=53, vNum=58), Point(x=34, y=55, vNum=7), Point(x=9, y=12, vNum=29), Point(x=25, y=36, vNum=62), Point(x=61, y=34, vNum=50), Point(x=39, y=44, vNum=2), Point(x=51, y=20, vNum=44), Point(x=25, y=14, vNum=47), Point(x=53, y=30, vNum=26), Point(x=4, y=5, vNum=56), Point(x=51, y=53, vNum=34), Point(x=0, y=21, vNum=61), Point(x=21, y=7, vNum=27), Point(x=4, y=26, vNum=64), Point(x=50, y=47, vNum=22), Point(x=33, y=37, vNum=11)]\n"
val a741_64 =
    "[Point(x=45, y=48, vNum=49), Point(x=13, y=16, vNum=16), Point(x=15, y=13, vNum=37), Point(x=4, y=51, vNum=41), Point(x=53, y=1, vNum=31), Point(x=53, y=54, vNum=25), Point(x=56, y=7, vNum=18), Point(x=19, y=14, vNum=8), Point(x=4, y=26, vNum=64), Point(x=33, y=37, vNum=11), Point(x=51, y=30, vNum=19), Point(x=56, y=6, vNum=53), Point(x=50, y=47, vNum=22), Point(x=13, y=20, vNum=42), Point(x=25, y=36, vNum=62), Point(x=14, y=18, vNum=20), Point(x=63, y=60, vNum=4), Point(x=41, y=2, vNum=59), Point(x=34, y=53, vNum=48), Point(x=14, y=53, vNum=58), Point(x=12, y=22, vNum=52), Point(x=41, y=51, vNum=39), Point(x=8, y=61, vNum=54), Point(x=32, y=12, vNum=57), Point(x=23, y=22, vNum=33), Point(x=42, y=60, vNum=24), Point(x=45, y=19, vNum=14), Point(x=21, y=7, vNum=27), Point(x=38, y=51, vNum=28), Point(x=47, y=52, vNum=43), Point(x=49, y=52, vNum=9), Point(x=36, y=32, vNum=17), Point(x=42, y=45, vNum=36), Point(x=50, y=39, vNum=15), Point(x=34, y=55, vNum=7), Point(x=38, y=18, vNum=38), Point(x=3, y=37, vNum=35), Point(x=53, y=50, vNum=21), Point(x=34, y=40, vNum=60), Point(x=24, y=38, vNum=10), Point(x=39, y=44, vNum=2), Point(x=58, y=63, vNum=5), Point(x=32, y=40, vNum=30), Point(x=51, y=20, vNum=44), Point(x=50, y=49, vNum=3), Point(x=44, y=49, vNum=23), Point(x=51, y=53, vNum=34), Point(x=53, y=30, vNum=26), Point(x=21, y=1, vNum=55), Point(x=44, y=48, vNum=45), Point(x=61, y=34, vNum=50), Point(x=42, y=5, vNum=13), Point(x=4, y=5, vNum=56), Point(x=15, y=17, vNum=1), Point(x=52, y=47, vNum=32), Point(x=19, y=12, vNum=40), Point(x=25, y=14, vNum=47), Point(x=54, y=1, vNum=63), Point(x=48, y=54, vNum=12), Point(x=9, y=12, vNum=29), Point(x=0, y=21, vNum=61), Point(x=0, y=55, vNum=51), Point(x=39, y=51, vNum=6), Point(x=51, y=45, vNum=46)]\n"
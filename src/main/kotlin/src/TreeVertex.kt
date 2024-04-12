import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

data class TreeVertex(val point: Point, val dLevel: Int, val parent: TreeVertex? = null) {
    val linksTo: MutableList<LinkType> = mutableListOf()
    fun closestVertex(point: Point, maxD: Int): TreeVertex {
        var closest: TreeVertex = this
        var closestDistance = this.point.distance(point)
        for (vertex in linksTo) {
            if (vertex.to.dLevel == maxD) continue
            val distance = point.distance(vertex.to.point)
            if (distance < closestDistance) {
                closest = vertex.to
                closestDistance = distance
            }
            val recursiveClosest =
                vertex.to.closestVertex(point, maxD)
            val recursiveDistance = point.distance(recursiveClosest.point)
            if (recursiveDistance < closestDistance) {
                closest = recursiveClosest
                closestDistance = recursiveDistance
            }
        }
        return closest
    }

    fun connectPoint(point: Point) {
        this.linksTo.add(LinkType(TreeVertex(point, dLevel + 1, this), this.point.distance(point)))
    }

    fun vertexesToRearrange(maxDLevel: Int): List<TreeVertex> {
        val res = mutableListOf<TreeVertex>()
        if (this.linksTo.isEmpty() && dLevel < maxDLevel) {
            res.add(this)
        }
        for (child in linksTo) {
            res.addAll(child.to.vertexesToRearrange(maxDLevel))
        }
        return res
    }

    fun detachByVnum(vnumToDetach: Int): Boolean {
        if (this.linksTo.removeIf { it.to.point.vNum == vnumToDetach }) {
            return true
        }

        for (vertex in this.linksTo) {
            if (vertex.to.detachByVnum(vnumToDetach = vnumToDetach)) {
                return true
            }
        }

        return false
    }

    fun treeWeight(): Int {
        return this.linksTo.sumOf { it.to.treeWeight() + it.weight }
    }

    fun edges(): List<Edge> {
        val res = mutableListOf<Edge>()
        res.addAll(this.linksTo.map { Edge(this.point.vNum, it.to.point.vNum) })
        for (child in linksTo) {
            res.addAll(child.to.edges())
        }
        return res
    }


    override fun toString(): String {
        return "TreeVertex(Point=$point, dLevel=$dLevel, linksTo=$linksTo)"
    }

    /** Работает только для корня дерева*/
    fun getD(): Int {
        val branchLengths = this.linksTo.map { it.to.maxBranch(1) }.sorted().reversed()
        if (branchLengths.isEmpty()) return 0
        if (branchLengths.size == 1) return branchLengths[0]
        return branchLengths[0] + branchLengths[1]
    }

    private fun maxBranch(level: Int): Int {
        return if (this.linksTo.isEmpty()) level
        else linksTo.maxOf { it.to.maxBranch(level + 1) }
    }

    fun saveToFile() {
        val sum = this.treeWeight()
        val edgesList = this.edges()
        val bufferedWriter = BufferedWriter(FileWriter(File("output/Mamaev_${vertex_number}_1.txt")))
        bufferedWriter.write("c Вес дерева = ${sum}, диаметр = ${this.getD()}\np edge ${vertex_number} ${vertex_number - 1}\n")
        for (edge in edgesList) {
            bufferedWriter.write("e ${edge.first} ${edge.second}\n")
        }
        bufferedWriter.flush()
        bufferedWriter.close()
    }

}

data class LinkType(val to: TreeVertex, val weight: Int)
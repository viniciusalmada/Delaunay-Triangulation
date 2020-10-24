import geom.Point
import geom.Triangulation
import utils.plus
import utils.writePointsAutoCAD
import utils.writeTrianglesAutoCAD

class Delaunay(points: List<Point>) {
    private val mPoints: List<Point>
    private val mTriangulation: Triangulation

    init {
        val extraPoints = calculateExtraPoints(points)
        val pointsShuffled = points.shuffled()
        mPoints = List(points.size + 3) { i ->
            if (i in 0..2)
                Point(extraPoints[i], i)
            else
                Point(pointsShuffled[i - 3], i)
        }
        mPoints.writePointsAutoCAD()
        mTriangulation = Triangulation(mPoints, 0, 1, 2)
    }

    fun make() {
        mTriangulation.addTriangle(3)
        mTriangulation.writeTrianglesAutoCAD()
    }

    private fun calculateExtraPoints(points: List<Point>): List<Point> {
        val maxX = points.maxOf { it.x }
        val minX = points.minOf { it.x }
        val maxY = points.maxOf { it.y }
        val minY = points.minOf { it.y }
        val m = if (maxX - minX > maxY - minY)
            (maxX - minX) / 2.0
        else
            (maxY - minY) / 2.0
        val centerX = minX + m
        val centerY = minY + m
        val center = Point(centerX, centerY)
        val p1 = Point(-3.0 * m, -3.0 * m) + center
        val p2 = Point(3.0 * m, 0.0) + center
        val p3 = Point(0.0, 3.0 * m) + center
        return listOf(p1, p2, p3)
    }
}
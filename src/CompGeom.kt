import kotlin.math.abs
import kotlin.math.sqrt

object CompGeom {

    enum class Orientation { LEFT, RIGHT, COLLINEAR }

    data class Point(val x: Double, val y: Double) {
        operator fun plus(pt: Point): Point {
            return Point(x + pt.x, y + pt.y)
        }

        operator fun minus(pt: Point): Point {
            return Point(x - pt.x, y - pt.y)
        }

        operator fun div(num: Double): Point {
            return Point(x / num, y / num)
        }

        infix fun dist(pt: Point): Double {
            val x = this.x - pt.x
            val y = this.y - pt.y
            return sqrt(x * x + y * y)
        }
    }

    data class Circle(val center: Point, val radius: Double) {
        fun contains(pt: Point): Boolean {
            return center.dist(pt) <= radius
        }
    }

    private fun orientation(a: Point, b: Point, c: Point): Orientation {
        val det = (a.x * b.y + b.x * c.y + c.x * a.y) - (a.x * c.y + b.x * a.y + c.x * b.y)
        return when {
            det > 0.0 -> Orientation.LEFT
            det < 0.0 -> Orientation.RIGHT
            else -> Orientation.COLLINEAR
        }
    }

    private fun checkLinesCrossing(a: Point, b: Point, c: Point, d: Point): Boolean {
        val abc = orientation(a, b, c)
        val abd = orientation(a, b, d)
        val cda = orientation(c, d, a)
        val cdb = orientation(c, d, b)

        return abc != abd && cda != cdb
    }

    private fun linesCrossingPoint(p1: Point, p2: Point, q1: Point, q2: Point): Point {
        val a1 = p1.y - p2.y
        val b1 = p2.x - p1.x
        val c1 = p1.x * p2.y - p1.y * p2.x

        val a2 = q1.y - q2.y
        val b2 = q2.x - q1.x
        val c2 = q1.x * q2.y - q1.y * q2.x

        val xp = (c2 * b1 - c1 * b2) / (a1 * b2 - a2 * b1)
        val yp = (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1)

        return Point(xp, yp)
    }

    private fun midPoint(p0: Point, p1: Point): Point {
        return (p0 + p1) / 2.0
    }

    private fun perpendToLine(a: Point, b: Point): Point {
        val ba = a - b
        val baPerpend = Point(ba.y, -ba.x)
        val mid = midPoint(a, b)
        return mid + baPerpend
    }

    fun isConvexPolygon(p0: Point, p1: Point, p2: Point, p3: Point): Boolean {
        val verifyP0 = orientation(p3, p0, p1)
        val verifyP1 = orientation(p0, p1, p2)
        val verifyP2 = orientation(p1, p2, p3)
        val verifyP3 = orientation(p2, p3, p0)

        return verifyP0 == Orientation.LEFT &&
                verifyP1 == Orientation.LEFT &&
                verifyP2 == Orientation.LEFT &&
                verifyP3 == Orientation.LEFT
    }

    fun circle3Points(p0: Point, p1: Point, p2: Point): Circle {
        val mid01 = midPoint(p0, p1)
        val perpend01 = perpendToLine(p0, p1)
        val mid12 = midPoint(p1, p2)
        val perpend12 = perpendToLine(p1, p2)
        val mid20 = midPoint(p2, p0)
        val perpend20 = perpendToLine(p2, p0)

        val cross0 = linesCrossingPoint(mid01, perpend01, mid12, perpend12)
        val cross1 = linesCrossingPoint(mid12, perpend12, mid20, perpend20)
        val cross2 = linesCrossingPoint(mid20, perpend20, mid01, perpend01)

        val center = (cross0 + cross1 + cross2) / 3.0
        val radius = center.dist(p0)

        return Circle(center, radius)
    }

    fun triangleContains(p0: Point, p1: Point, p2: Point, pt: Point): Boolean {
        val ptInf = Point(abs(pt.x) * 10, pt.y)

        val ab = checkLinesCrossing(p0, p1, pt, ptInf)
        val bc = checkLinesCrossing(p1, p2, pt, ptInf)
        val ca = checkLinesCrossing(p2, p0, pt, ptInf)

        var count = 0
        if (ab) count++
        if (bc) count++
        if (ca) count++

        return count % 2 != 0
    }

    fun edgeContains(p0: Point, p1: Point, pt: Point): Boolean {
        return orientation(p0, pt, p1) == Orientation.COLLINEAR
    }

    fun triangleBox(pts: List<Point>): List<Point> {
        val maxX = pts.maxOf { it.x }
        val minX = pts.minOf { it.x }
        val maxY = pts.maxOf { it.y }
        val minY = pts.minOf { it.y }

        return if (maxX - minX > maxY - minY) {
            val m = (maxX - minX) / 2.0
            val centerX = minX + m
            val centerY = minY + (maxY - minY) / 2.0
            val center = Point(centerX, centerY)
            val p0 = Point(-3.0 * m, -3.0 * m) + center
            val p1 = Point(3.0 * m, 0.0) + center
            val p2 = Point(0.0, 3.0 * m) + center

            listOf(p0, p1, p2)
        } else {
            val m = (maxY - minY) / 2.0
            val centerX = minX + (maxX - minX) / 2.0
            val centerY = minY + m
            val center = Point(centerX, centerY)
            val p0 = Point(-3.0 * m, -3.0 * m) + center
            val p1 = Point(3.0 * m, 0.0) + center
            val p2 = Point(0.0, 3.0 * m) + center

            listOf(p0, p1, p2)
        }
    }
}

package utils

import geom.Edge
import geom.Point
import geom.Triangle
import geom.Triangulation
import java.io.File

enum class Orientation { LEFT, RIGHT, COLLINEAR }

fun List<Point>.writePointsAutoCAD() {
    val file = File("points.scr").writer()
    file.write("point\n")
    this.onEach {
        file.write("${it.x},${it.y}\n\n")
    }
    file.close()
}

fun Triangulation.writeTrianglesAutoCAD() {
    val file = File("triangles.scr").writer()
    val pointsBuffer = StringBuffer()
    pointsBuffer.append("point\n")
    this.mTriangles.onEach {
        pointsBuffer.append("${it.point0.x},${it.point0.y}\n\n")
        pointsBuffer.append("${it.point1.x},${it.point1.y}\n\n")
        pointsBuffer.append("${it.point2.x},${it.point2.y}\n\n")
    }

    val trianglesBuffer = StringBuffer()
    this.mTriangles.onEach {
        trianglesBuffer.append("pline\n")
        trianglesBuffer.append("${it.point0.x},${it.point0.y}\n")
        trianglesBuffer.append("${it.point1.x},${it.point1.y}\n")
        trianglesBuffer.append("${it.point2.x},${it.point2.y}\n")
        trianglesBuffer.append("${it.point0.x},${it.point0.y}\n\n")
    }
    file.write(pointsBuffer.toString().dropLast(1))
    file.write(trianglesBuffer.toString())
    file.close()
}

operator fun Point.plus(pt: Point): Point {
    return Point(x + pt.x, y + pt.y)
}

/**
 *     | 1 ax ay |
 * d = | 1 bx by |
 *     | 1 cx cy |
 */
fun orientation(ed: Edge, c: Point): Orientation {
    val a = ed.p1
    val b = ed.p2
    val d = (a.x * b.y + b.x * c.y + c.x * a.y) - (a.x * c.y + b.x * a.y + c.x * b.y)
    return when {
        d > 0.0 -> Orientation.LEFT
        d < 0.0 -> Orientation.RIGHT
        else -> Orientation.COLLINEAR
    }
}

fun ArrayList<Triangle>.faceThatContainsVtx(pt: Point): Triangle? {
    forEach { tri ->
        if (tri.contains(pt))
            return tri
    }

    return null
}
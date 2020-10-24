package geom

import utils.Orientation
import utils.orientation


data class Triangle(
    val point0: Point,
    val point1: Point,
    val point2: Point,
) {
    fun contains(pt: Point): Boolean {
        return (orientation(edge0, pt) == Orientation.LEFT &&
                orientation(edge1, pt) == Orientation.LEFT &&
                orientation(edge2, pt) == Orientation.LEFT
                )
    }

    private val edge0 = Edge(point0, point1)
    private val edge1 = Edge(point1, point2)
    private val edge2 = Edge(point2, point0)
}
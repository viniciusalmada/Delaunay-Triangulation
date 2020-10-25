package geom

import utils.faceThatContainsVtx


class Triangulation(private val mPoints: List<Point>, point1: Int, point2: Int, point3: Int) {
    val mTriangles: ArrayList<Triangle> = ArrayList()
    val mEdges: ArrayList<Edge> = ArrayList()

    init {
        val triangle = Triangle(
            mPoints[point1],
            mPoints[point2],
            mPoints[point3],
        )
        mTriangles.add(triangle)
    }

    fun addTriangle(pointIndex: Int) {
        val point = mPoints[pointIndex]

        val oldTriangle = mTriangles.faceThatContainsVtx(point) ?: return

        val tri0 = Triangle(point, oldTriangle.point0, oldTriangle.point1)
        val tri1 = Triangle(point, oldTriangle.point1, oldTriangle.point2)
        val tri2 = Triangle(point, oldTriangle.point2, oldTriangle.point0)

        mTriangles.remove(oldTriangle)
        mTriangles.add(tri0)
        mTriangles.add(tri1)
        mTriangles.add(tri2)
    }
}



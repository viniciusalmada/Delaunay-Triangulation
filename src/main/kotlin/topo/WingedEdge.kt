/*
package topo

import geom.Point
import geom.Triangle
import utils.Orientation
import utils.orientation

object WingedEdge {
    private val verticesList: HashMap<Int, WEVertex> = hashMapOf()
    private val facesList: HashMap<Int, WEFace> = hashMapOf()
    private val edgesList: HashMap<Int, WEEdge> = hashMapOf()

    fun init(triangle: Triangle) {
        val vtx0 = WEVertex(triangle.pointA.x, triangle.pointA.y, 0)
        val vtx1 = WEVertex(triangle.pointB.x, triangle.pointB.y, 1)
        val vtx2 = WEVertex(triangle.pointC.x, triangle.pointC.y, 2)

        val face = WEFace(0, 1, 2)

        val edge0 = WEEdge(0, 1, 0, -1, 2, 1, -1, -1)
        val edge1 = WEEdge(1, 2, 0, -1, 0, 2, -1, -1)
        val edge2 = WEEdge(2, 0, 0, -1, 1, 0, -1, -1)

        verticesList[0] = vtx0
        verticesList[1] = vtx1
        verticesList[2] = vtx2
        facesList[0] = face
        edgesList[0] = edge0
        edgesList[1] = edge1
        edgesList[2] = edge2
    }

    fun faceThatContainsVtx(pt: Point): Int {
        facesList.forEach { (i, face) ->
            val edge0 = edgesList[face.edge0] ?: return@forEach
            val edge1 = edgesList[face.edge1] ?: return@forEach
            val edge2 = edgesList[face.edge2] ?: return@forEach

            if (orientationPointToEdge(edge0, pt) == Orientation.LEFT &&
                orientationPointToEdge(edge1, pt) == Orientation.LEFT &&
                orientationPointToEdge(edge2, pt) == Orientation.LEFT
            ) {
                return i
            }
        }
        return -1
    }

    private fun orientationPointToEdge(edge0: WEEdge, pt: Point): Orientation {
        val a = verticesList[edge0.vtxInit].toPoint()
        val b = verticesList[edge0.vtxFinal].toPoint()

        return orientation(a, b, pt)
    }

    data class WEVertex(val x: Double, val y: Double, val elem: Int)

    data class WEFace(val edge0: Int, val edge1: Int, val edge2: Int)

    data class WEEdge(
        val vtxInit: Int,
        val vtxFinal: Int,
        val faceLeft: Int,
        val faceRight: Int,
        val edgeLeftPrev: Int,
        val edgeLeftNext: Int,
        val edgeRightPrev: Int,
        val edgeRightNext: Int,
    )

}
*/

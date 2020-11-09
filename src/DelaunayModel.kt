import HED.Edge
import HED.HalfEdge
import HED.Triangle
import HED.Triangle.TriVtx.*
import HED.Vertex
import HED.isInvalid
import HED.isValid
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * Class that represents a model for Delaunay Triangulation construction from
 * a given set of 2D points.
 *
 * This class use Half-Edge [HED] topological data structure to provide better queries on
 * editing and adding triangles, edges and vertices to model.
 *
 * The first step is create a triangle (three vertices) that contains all given points.
 * This triangle is only a helper to create the Delaunay Triangulation, after all points
 * are processed, it must be excluded.
 *
 * It is done with maps created including only those vertices and triangles of the
 * original set of given points.
 *
 * @param points list of three 2D base points for Delaunay Triangulation
 * @property mVertices set of HED vertices including first base points
 * @property mHalfEdges set of HED half-edges
 * @property mEdges set of HED edges
 * @property mTriangles set of HED triangles including those with first base points
 * @property mVerticesFinal map of HED vertices without the first base points
 * @property mTrianglesFinal map of HED triangles excluding those with first base points
 * @constructor Creates the first triangle formed by three vertices calculated to include all
 * points inside itself.
 */
class DelaunayModel(points: List<CompGeom.Point>) {
    private val mVertices: Stack<Vertex> = Stack()
    private val mHalfEdges: Stack<HalfEdge> = Stack()
    private val mEdges: Stack<Edge> = Stack()
    private val mTriangles: Stack<Triangle> = Stack()

    private val mVerticesFinal: HashMap<Int, Vertex> = HashMap()
    private val mTrianglesFinal: HashMap<Int, Triangle> = HashMap()

    init {
        val box: List<CompGeom.Point> = CompGeom.triangleBox(points)

        val v0: Int = newVertex(box[0])
        val v1: Int = newVertex(box[1])
        val v2: Int = newVertex(box[2])

        val h1e0: Int = newHalfEdge(v0)
        val h2e0: Int = newHalfEdge(v1)
        val h1e1: Int = newHalfEdge(v1)
        val h2e1: Int = newHalfEdge(v2)
        val h1e2: Int = newHalfEdge(v2)
        val h2e2: Int = newHalfEdge(v0)

        newEdge(h1e0, h2e0)
        newEdge(h1e1, h2e1)
        newEdge(h1e2, h2e2)

        newTriangle(h1e0, h1e1, h1e2)

        points.onEach { addTriangle(it) }
        finish()
    }

    /**
     * This function creates a new topological HED Triangle and return its index.
     * Also, it set, for each half-edge provided, a next half-edge that forms the
     * triangle loop.
     *
     * @param h0 first HED half-edge
     * @param h1 second HED half-edge
     * @param h2 third HED half-edge
     * @return the index of the new triangle created.
     */
    private fun newTriangle(h0: Int, h1: Int, h2: Int): Int {
        mTriangles.push(Triangle(h0, h1, h2))
        mHalfEdges[h0].tri = mTriangles.lastIndex
        mHalfEdges[h1].tri = mTriangles.lastIndex
        mHalfEdges[h2].tri = mTriangles.lastIndex

        mHalfEdges[h0].next = h1
        mHalfEdges[h1].next = h2
        mHalfEdges[h2].next = h0
        return mTriangles.lastIndex
    }

    /**
     * This function creates a new topological HED Edge and return its index.
     * Also, it set, for each half-edge provided, the edge property
     *
     * @param h1Id HED half-edge from initial vertex
     * @param h2Id HED half-edge from final vertex
     * @return the index of the new edge created.
     */
    private fun newEdge(h1Id: Int, h2Id: Int): Int {
        mEdges.push(Edge(h1Id, h2Id))
        mHalfEdges[h1Id].edge = mEdges.lastIndex
        mHalfEdges[h2Id].edge = mEdges.lastIndex
        return mEdges.lastIndex
    }

    /**
     * This function creates a new topological HED Half-edge and return its index.
     * Also, it set, for the vertex provided, the half-edge property
     *
     * @param vertexId HED vertex reference
     * @return the index of the new half-edge created.
     */
    private fun newHalfEdge(vertexId: Int): Int {
        mHalfEdges.push(HalfEdge(vertexId))
        mVertices[vertexId].hed = mHalfEdges.lastIndex
        return mHalfEdges.lastIndex
    }

    /**
     * This function creates a new topological HED Vertex and return its index.
     *
     * @param pt 2D geometrical point
     * @return the index of the new vertex created.
     */
    private fun newVertex(pt: CompGeom.Point): Int {
        mVertices.push(Vertex(pt))
        return mVertices.lastIndex
    }

    /**
     * This function update the half-edge property to a given vertex.
     *
     * @param vtx vertex index to be updated
     * @param hed new half-edge reference index
     */
    private fun updateHalfEdgeOfVertex(vtx: Int, hed: Int) {
        mVertices[vtx].hed = hed
    }

    /**
     * This function update the half-edges properties to a given triangle.
     *
     * @param tri triangle index to be updated
     * @param hed0 new first half-edge reference index
     * @param hed1 new second half-edge reference index
     * @param hed2 new third half-edge reference index
     */
    private fun updateHalfEdgesOfTriangle(tri: Int, hed0: Int, hed1: Int, hed2: Int) {
        mTriangles[tri].hed0 = hed0
        mTriangles[tri].hed1 = hed1
        mTriangles[tri].hed2 = hed2

        mHalfEdges[hed0].tri = tri
        mHalfEdges[hed1].tri = tri
        mHalfEdges[hed2].tri = tri

        mHalfEdges[hed0].next = hed1
        mHalfEdges[hed1].next = hed2
        mHalfEdges[hed2].next = hed0
    }

    /**
     * This function update the vertex property to a given half-edge.
     *
     * @param hed half-edge index to be updated
     * @param vtx new vertex reference index
     */
    private fun updateVertexOfHalfEdge(hed: Int, vtx: Int) {
        mHalfEdges[hed].vtx = vtx
    }

    // GETTERS

    private fun getHalfEdgeFromTriangle(triId: Int, i: Triangle.TriVtx): Int {
        val tri = mTriangles[triId]
        return when (i) {
            V0 -> tri.hed0
            V1 -> tri.hed1
            V2 -> tri.hed2
        }
    }

    private fun getVertexOfTriangle(triId: Int, i: Triangle.TriVtx): Int {
        return mHalfEdges[getHalfEdgeFromTriangle(triId, i)].vtx
    }

    private fun getVerticesOfTriangle(triId: Int): IntArray {
        val v0 = getVertexOfTriangle(triId, V0)
        val v1 = getVertexOfTriangle(triId, V1)
        val v2 = getVertexOfTriangle(triId, V2)

        return intArrayOf(v0, v1, v2)
    }

    private fun getHalfEdgeFromVertex(vtxId: Int): Int {
        return mVertices[vtxId].hed
    }

    private fun getEdgeFromHalfEdge(hed: Int): Int {
        return mHalfEdges[hed].edge
    }

    private fun getNearEdges(vararg vertices: Int): Stack<Int> {
        val edges = Stack<Int>()
        vertices@ for (vtxId in vertices) {
            val firstHed = getHalfEdgeFromVertex(vtxId)
            val firstEdge = getEdgeFromHalfEdge(firstHed)
            edges.push(firstEdge)

            var currentHalfEdge = getNextOfHalfEdge(firstHed)
            currentHalfEdge = getNextOfHalfEdge(currentHalfEdge)

            while (currentHalfEdge.isValid()) {
                val edge = getEdgeFromHalfEdge(currentHalfEdge)
                if (edge == firstEdge)
                    continue@vertices
                edges.push(edge)
                currentHalfEdge = getMateOfHalfEdge(currentHalfEdge)
                if (getTriangleOfHalfEdge(currentHalfEdge).isInvalid())
                    break

                currentHalfEdge = getNextOfHalfEdge(currentHalfEdge)
                currentHalfEdge = getNextOfHalfEdge(currentHalfEdge)
            }

            val firstHalfEdgeMate = getMateOfHalfEdge(firstHed)
            if (getTriangleOfHalfEdge(firstHalfEdgeMate).isInvalid())
                continue@vertices

            currentHalfEdge = getNextOfHalfEdge(firstHalfEdgeMate)
            while (currentHalfEdge.isValid()) {
                val edge = getEdgeFromHalfEdge(currentHalfEdge)
                edges.push(edge)
                currentHalfEdge = getMateOfHalfEdge(currentHalfEdge)
                if (getTriangleOfHalfEdge(currentHalfEdge).isInvalid())
                    break
                currentHalfEdge = getNextOfHalfEdge(currentHalfEdge)
            }
        }
        return edges
    }

    private fun getMateOfHalfEdge(hed: Int): Int {
        val edge = mHalfEdges[hed].edge
        val hed1 = mEdges[edge].hed1
        val hed2 = mEdges[edge].hed2

        return if (hed == hed1) hed2 else hed1
    }

    private fun getTriangleOfHalfEdge(hed: Int): Int {
        return mHalfEdges[hed].tri
    }

    private fun getNextOfHalfEdge(hed: Int): Int {
        return mHalfEdges[hed].next
    }

    private fun getPointOfHalfEdge(hed: Int): CompGeom.Point {
        val vtx = getVertexOfHalfEdge(hed)
        return mVertices[vtx].pt
    }

    private fun getVertexOfHalfEdge(hed: Int): Int {
        return mHalfEdges[hed].vtx
    }

    private fun getPointOfVertex(vtx: Int): CompGeom.Point {
        return mVertices[vtx].pt
    }

    // UTILS

    private fun verifyVertices(vararg vertices: Int) {
        for (vtxId in vertices) {
            val edges = getNearEdges(vtxId)
            for (e in edges) {
                if (!isEdgeLegal(e)) {
                    makeFlip(e)
                }
            }
        }
    }

    private fun isEdgeLegal(e: Int): Boolean {
        if (!isEdgeNearTwoFaces(e)) return true

        val edge = mEdges[e]
        val hed1 = edge.hed1
        val hed1next = getNextOfHalfEdge(hed1)
        val hed1next2 = getNextOfHalfEdge(hed1next)
        val hed2 = edge.hed2
        val hed2next = getNextOfHalfEdge(hed2)
        val hed2next2 = getNextOfHalfEdge(hed2next)

        val p0 = getPointOfHalfEdge(hed1)
        val p1 = getPointOfHalfEdge(hed2next2)
        val p2 = getPointOfHalfEdge(hed2)
        val p3 = getPointOfHalfEdge(hed1next2)

        if (!CompGeom.isConvexPolygon(p0, p1, p2, p3))
            return true

        val circle012 = CompGeom.circle3Points(p0, p1, p2)
        if (!circle012.contains(p3))
            return true

        val circle023 = CompGeom.circle3Points(p0, p2, p3)
        if (!circle023.contains(p1))
            return true

        return false
    }

    private fun makeFlip(e: Int) {
        val edge = mEdges[e]
        val tri1 = getTriangleOfHalfEdge(edge.hed1)
        val tri2 = getTriangleOfHalfEdge(edge.hed2)

        val nextHed1 = getNextOfHalfEdge(edge.hed1)
        val nextNextHed1 = getNextOfHalfEdge(nextHed1)

        val nextHed2 = getNextOfHalfEdge(edge.hed2)
        val nextNextHed2 = getNextOfHalfEdge(nextHed2)

        val vtx1 = getVertexOfHalfEdge(edge.hed1)
        val hedVtx1 = getHalfEdgeFromVertex(vtx1)
        if (hedVtx1 == edge.hed1)
            updateHalfEdgeOfVertex(vtx1, nextHed2)

        val vtx2 = getVertexOfHalfEdge(edge.hed2)
        val hedVtx2 = getHalfEdgeFromVertex(vtx2)
        if (hedVtx2 == edge.hed2)
            updateHalfEdgeOfVertex(vtx2, nextHed1)

        val newVtxOfHed1 = getVertexOfHalfEdge(nextNextHed1)
        val newVtxOfHed2 = getVertexOfHalfEdge(nextNextHed2)

        updateVertexOfHalfEdge(edge.hed1, newVtxOfHed1)
        updateVertexOfHalfEdge(edge.hed2, newVtxOfHed2)

        updateHalfEdgesOfTriangle(tri1, edge.hed1, nextNextHed2, nextHed1)
        updateHalfEdgesOfTriangle(tri2, edge.hed2, nextNextHed1, nextHed2)

        verifyVertices(newVtxOfHed1, newVtxOfHed2)
    }

    private fun isEdgeNearTwoFaces(e: Int): Boolean {
        val edge = mEdges[e]
        val hed1 = mHalfEdges[edge.hed1]
        val hed2 = mHalfEdges[edge.hed2]

        return hed1.tri.isValid() && hed2.tri.isValid()
    }

    private fun findEdgeThatContainsPoint(pt: CompGeom.Point): Int {
        for (i in mEdges.indices) {
            val edge = mEdges[i]
            val p0 = getPointOfHalfEdge(edge.hed1)
            val p1 = getPointOfHalfEdge(edge.hed2)

            if (CompGeom.edgeContains(p0, p1, pt))
                return i
        }
        return -1
    }

    private fun findTriangleThatContainsPoint(pt: CompGeom.Point): Int {
        for (i in mTriangles.indices) {
            val tri = mTriangles[i]
            val p0 = getPointOfHalfEdge(tri.hed0)
            val p1 = getPointOfHalfEdge(tri.hed1)
            val p2 = getPointOfHalfEdge(tri.hed2)

            if (CompGeom.triangleContains(p0, p1, p2, pt))
                return i
        }
        return -1
    }

    private fun addTriangle(pt: CompGeom.Point) {

        val edge = findEdgeThatContainsPoint(pt)

        if (edge.isValid()) {
            splitEdge(edge, pt)
            return
        }

        val tri0 = findTriangleThatContainsPoint(pt)
        splitTriangle(tri0, pt)
    }

    private fun splitEdge(edgeId: Int, pt: CompGeom.Point) {
        val vtx = newVertex(pt)
        val edge = mEdges[edgeId]

        val hed1 = edge.hed1
        val hed2 = edge.hed2
        val tri1 = getTriangleOfHalfEdge(hed1)
        val tri2 = getTriangleOfHalfEdge(hed2)
        val hed3 = newHalfEdge(vtx)
        val hed4 = newHalfEdge(vtx)
        val hed5 = newHalfEdge(vtx)
        val hed6 = newHalfEdge(vtx)

        val nextHed1 = getNextOfHalfEdge(hed1)
        val nextNextHed1 = getNextOfHalfEdge(nextHed1)
        val vtxNextNextHed1 = getVertexOfHalfEdge(nextNextHed1)
        val hed7 = newHalfEdge(vtxNextNextHed1)

        val nextHed2 = getNextOfHalfEdge(hed2)
        val nextNextHed2 = getNextOfHalfEdge(nextHed2)
        val vtxNextNextHed2 = getVertexOfHalfEdge(nextNextHed2)
        val hed8 = newHalfEdge(vtxNextNextHed2)

        setHalfEdgesOfEdge(edgeId, hed3, hed2)
        newEdge(hed6, hed1)
        newEdge(hed4, hed7)
        newEdge(hed5, hed8)

        newTriangle(hed4, nextNextHed1, hed1)
        newTriangle(hed6, nextHed2, hed8)

        updateHalfEdgesOfTriangle(tri1, hed3, nextHed1, hed7)
        updateHalfEdgesOfTriangle(tri2, hed5, nextNextHed2, hed2)
    }

    private fun setHalfEdgesOfEdge(edgeId: Int, hed1: Int, hed2: Int) {
        mEdges[edgeId].hed1 = hed1
        mEdges[edgeId].hed2 = hed2
        mHalfEdges[hed1].edge = edgeId
        mHalfEdges[hed2].edge = edgeId
    }

    private fun splitTriangle(tri: Int, pt: CompGeom.Point) {
        val verticesToCheck = getVerticesOfTriangle(tri)

        val newV = newVertex(pt)

        val h1e0 = newHalfEdge(newV)
        val h1e1 = newHalfEdge(newV)
        val h1e2 = newHalfEdge(newV)
        val h2e0 = newHalfEdge(getVertexOfTriangle(tri, V0))
        val h2e1 = newHalfEdge(getVertexOfTriangle(tri, V1))
        val h2e2 = newHalfEdge(getVertexOfTriangle(tri, V2))

        newEdge(h1e0, h2e0)
        newEdge(h1e1, h2e1)
        newEdge(h1e2, h2e2)

        val h0Tri0 = getHalfEdgeFromTriangle(tri, V0)
        val h1Tri0 = getHalfEdgeFromTriangle(tri, V1)
        val h2Tri0 = getHalfEdgeFromTriangle(tri, V2)

        newTriangle(h1e1, h1Tri0, h2e2)

        newTriangle(h1e2, h2Tri0, h2e0)

        updateHalfEdgesOfTriangle(tri, h0Tri0, h2e1, h1e0)

        verifyVertices(verticesToCheck[0], verticesToCheck[1], verticesToCheck[2])
    }

    private fun finish() {
        val edges = getNearEdges(0, 1, 2)

        val verticesToIgnore = setOf(0, 1, 2)
        val trianglesToIgnore = hashSetOf<Int>()

        for (e in edges) {
            val edge = mEdges[e]
            val hed1 = mHalfEdges[edge.hed1]
            val hed2 = mHalfEdges[edge.hed2]

            trianglesToIgnore.add(hed1.tri)
            trianglesToIgnore.add(hed2.tri)
        }

        for (i in mVertices.indices) {
            if (verticesToIgnore.contains(i))
                continue
            mVerticesFinal[i] = mVertices[i]
        }

        for (i in mTriangles.indices) {
            if (trianglesToIgnore.contains(i))
                continue
            mTrianglesFinal[i] = mTriangles[i]
        }

    }

    // PUBLIC

    fun printMATLAB(): String {
        val buffer = StringBuilder()
        buffer.append("% Generated ${mTrianglesFinal.size} triangles\n")
        for (tri in mTrianglesFinal.keys) {
            val vertices = getVerticesOfTriangle(tri)
            val p0 = getPointOfVertex(vertices[0])
            val p1 = getPointOfVertex(vertices[1])
            val p2 = getPointOfVertex(vertices[2])
            buffer.append("line([")
            buffer.append("${p0.x},${p1.x},${p2.x},${p0.x}")
            buffer.append("],[")
            buffer.append("${p0.y},${p1.y},${p2.y},${p0.y}")
            buffer.append("])\n")
        }
        buffer.append("axis equal\n\n")
        buffer.append("x=[")
        for (vtx in mVerticesFinal.values) {
            buffer.append("${vtx.pt.x},")
        }
        buffer.append("];\n")
        buffer.append("y=[")
        for (vtx in mVerticesFinal.values) {
            buffer.append("${vtx.pt.y},")
        }
        buffer.append("];\n")

        val file = File("../triangles.m").writer()
        file.write(buffer.toString())

        file.close()

        return buffer.toString()
    }
}
object HED {

    data class Vertex(
        val pt: CompGeom.Point,
        var hed: Int = -1
    )

    data class HalfEdge(
        var vtx: Int = -1,
        var edge: Int = -1,
        var tri: Int = -1,
        var next: Int = -1,
    )

    data class Triangle(
        var hed0: Int = -1,
        var hed1: Int = -1,
        var hed2: Int = -1,
    ) {
        enum class TriVtx { V0, V1, V2 }
    }

    data class Edge(
        var hed1: Int,
        var hed2: Int,
    )

    fun Int.isInvalid(): Boolean {
        return this == -1
    }

    fun Int.isValid(): Boolean {
        return this != -1
    }
}
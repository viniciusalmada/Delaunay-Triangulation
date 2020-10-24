import geom.Point

fun main(args: Array<String>) {
    val pts = listOf(
        Point(10.0, 20.0),
        Point(5.0, 25.0),
        Point(-5.0, 10.0),
        Point(4.5, 13.5),
        Point(0.0, 16.0),
        Point(-2.0, 20.0),
    )

    val delaunay = Delaunay(pts)
    delaunay.make()
}
package geom

data class Point(val x: Double, val y: Double, var index: Int = -1) {
    constructor(point: Point, i: Int) : this(point.x, point.y, i)
}
package com.xxxsarutahikoxxx.kotlin.FingerMotionDetector

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.floor


fun FingerDetector.createImage(wSlice : Float, hSlice : Float ) : BufferedImage {
    val xMin = 0f
    val xMax = cDouble.map { it[1].second.x }.max()!!
    val yMin = cDouble.map { it[1].second.y }.min()!!
    val yMax = cDouble.map { it[1].second.y }.max()!!

    val height = yMax - yMin
    val width = xMax - xMin

    val wSize : Int = floor(width / wSlice).toInt() + 1
    val hSize : Int = floor(height / wSlice).toInt() + 1

    val ret = BufferedImage(wSize, hSize, BufferedImage.TYPE_INT_RGB)



    fun evaluate(point : dPoint) : Color? {
        val colors : Map<fCombination, Color> = cDouble.map { it.fComb }.zip(listOf(
            Color.red, Color.cyan, Color.green, Color.blue, Color.magenta,
            Color.gray, Color.white, Color.pink, Color.yellow, Color.orange
        )).toMap()

        val polar =
            Polar.of(point.first, point.second)

        return evaluate2(polar)?.run { colors[this] }
    }


    // 領域を塗り分ける
    0.until(wSize).forEach{
        val xIndex = it
        0.until(hSize).forEach {
            val yIndex = it

            val point = (xMin + xIndex * wSlice) to (yMax - yIndex * hSlice)
            val color = evaluate(point)

            ret.setRGB( xIndex, yIndex, color?.rgb ?: Color.white.rgb)
        }
    }

    // 基準点を塗る
    cDouble.map { it[1].second }.forEach {
        val xIndex = floor( (it.x - xMin) / wSlice )
        val yIndex = floor( (yMax - it.y) / hSlice )

        ret.setRGB(xIndex.toInt(), yIndex.toInt(), Color.black.rgb)
    }

    // 左系の場合は画像を左右反転する
    if( hPosition.dir is fLeft){
        0.until(hSize).forEach{
            val bytes = IntArray(wSize)
            ret.getRGB(0, it, wSize, 1, bytes, 0, wSize)
            ret.setRGB(0, it, wSize, 1, bytes.reversed().toIntArray(), 0, wSize)
        }
    }

    return ret
}
package com.xxxsarutahikoxxx.kotlin.FingerMotionDetector

import kotlin.math.atan2
import kotlin.math.sqrt


interface fDirection {
    /**
     * 座標店のリストを右手か左手かに従ってソートする
     *
     * 返り値は親指->小指の順でソートされた結果となる
     * */
    fun sort( points : List<dPoint> ) : List<dPoint> {
        val isRight = this is fRight

        return points.sortedBy { it.first }.let { if( isRight ) it else it.reversed() }
    }

    /**
     * 元となる点列から基準点とそれに対する極座標のマップへと変換する
     *
     * 右手の場合は左端の点を基準として極座標を設定する<br>
     * 左手の場合は右端の点を基準として左右反転した極座標を設定する
     *
     * [yReverse] = true の場合は極形式に変換する際にy座標を反転する、これはディスプレイからの入力を想定した処理である
     * */
    fun toPolar(points : List<dPoint>, yReverse : Boolean ) : Map<dPoint, Polar> {
        val points = sort(points)

        // 基準となる原点座標
        val (x0, y0) = points[0]

        val isRight = this is fRight
        return points.associateWith {
            if( it != points[0] ){
                ((it.first - x0) to (it.second - y0)).run {
                    if( isRight ){ first }else{ - first } to if( yReverse ){ - second } else{ second }
                }.run {
                    Polar(
                        sqrt(first * first + second * second),
                        atan2(second, first)
                    )
                }
            }else{
                Polar(0f, 0f)
            }
        }
    }
}
interface fRight : fDirection
interface fLeft : fDirection

interface Hand : fDirection {
    val thumb : Finger.Thumb
    val index : Finger.Index
    val middle : Finger.Middle
    val ring : Finger.Ring
    val little : Finger.Little

    val fingers get() = listOf(thumb, index, middle, ring, little)
}

sealed class Finger : Comparable<Finger> {
    open class Thumb : Finger()
    open class Index : Finger()
    open class Middle : Finger()
    open class Ring : Finger()
    open class Little : Finger()

    object Unknown : Finger()


    //
    override fun compareTo(other: Finger): Int {
        return when( this ){
            is Thumb -> 1
            is Index -> 2
            is Middle -> 3
            is Ring -> 4
            is Little -> 5
            is Unknown -> 6
        } +
        when( this ){
            is fRight -> 10
            is fLeft -> 20
            else -> 30
        }
    }



    companion object {
        val thumb = Thumb()
        val index = Index()
        val middle = Middle()
        val ring = Ring()
        val little = Little()


        val RHand : Hand = object : Hand,
            fRight {
            override val thumb: Thumb = object : fRight, Thumb(){}
            override val index: Index = object : fRight, Index(){}
            override val middle: Middle = object : fRight, Middle(){}
            override val ring: Ring = object : fRight, Ring(){}
            override val little: Little = object : fRight, Little(){}
        }
        val LHand : Hand = object : Hand,
            fLeft {
            override val thumb: Thumb = object : fLeft, Thumb(){}
            override val index: Index = object : fLeft, Index(){}
            override val middle: Middle = object : fLeft, Middle(){}
            override val ring: Ring = object : fLeft, Ring(){}
            override val little: Little = object : fLeft, Little(){}
        }
    }
}

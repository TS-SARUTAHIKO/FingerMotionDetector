package com.xxxsarutahikoxxx.kotlin.FingerMotionDetector

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


/** (x,y)座標 */
typealias dPoint = Pair<Float, Float>

/** 十字方向 */
enum class CrossDirection {
    Up, Down, Left, Right, Center
}

/**
 * 座標の極形式
 * */
class Polar(val radius : Float, val argument : Float){
    val x : Float by lazy { cos(argument) * radius }
    val y : Float by lazy {  sin(argument) * radius }

    val xy : dPoint by lazy { x to y }

    override fun toString(): String {
        return "極座標 (r,θ)=($radius, $argument) 直交座標 (x, y)=($x, $y)"
    }

    /** 角度[radius]を[r]だけずらした極座標の値を返す */
    fun dRadius(r : Float) : Polar =
        Polar(radius + r, argument)
    /** 角度[argument]を[arg]だけずらした極座標の値を返す */
    fun dArgument(arg : Float) : Polar =
        Polar(radius, argument + arg)

    companion object {
        /** (x,y)座標から作成するためのファクトリ関数 */
        fun of(x : Float, y : Float) : Polar {
            return Polar(sqrt(x * x + y * y), atan2(y, x))
        }
    }
}

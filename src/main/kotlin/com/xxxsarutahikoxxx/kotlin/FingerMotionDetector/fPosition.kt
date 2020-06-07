package com.xxxsarutahikoxxx.kotlin.FingerMotionDetector

import java.lang.RuntimeException


/**
 * 方向性[dir]に従って順序と極座標変換を行った点列データ
 *
 * 1. 与えられた方向性と点列データから原点を決定する（Rightならば原点は最も左の点、Leftならば原点は最も右の点）
 *
 * 2. 各点を原点からの極座標に変換する。ただし方向性がLeftならば極座標は左右反転した座標として計算する。
 *
 * @param dir : 点列データの方向性
 * @param points : 方向性に従って順序付けられた点列データ
 * @param polars : 方向性と原点に従って(x,y)座標から(r,θ)座標に変換した点列データ
 * */
open class fPosition(points : List<dPoint>, val dir : fDirection) {
    val polars : Map<dPoint, Polar> = dir.toPolar(points,
        yReverse
    )
    val points : List<dPoint> by lazy { polars.keys.toList() }

    /** [dPoint]に対応した極座標を取得する  */
    operator fun get(point : dPoint) : Pair<dPoint, Polar> = point to polars[point]!!
    /** [index]番目の点に対応した極座標を取得する */
    operator fun get(index : Int) : Pair<dPoint, Polar> = get(points[index])


    companion object {
        /** 座標を極座標に変換する際にY座標を反転するかどうか（ディスプレイ座標は上がら下にY座標が設定されているため） */
        var yReverse = true
    }
}

/**
 * [fCombination] に対応した [fPosition]
 * */
open class fCombPosition protected constructor(val fComb : fCombination, points : List<dPoint>, dir : fDirection) : fPosition(points, dir){
    constructor(hPosition : fHandPosition, fComb: fCombination) : this(fComb, hPosition[fComb].map { it.first }, hPosition.dir)

    val thumb : Pair<dPoint, Polar> get() = this[ fComb.indexOf { it is Finger.Thumb } ]
    val index : Pair<dPoint, Polar> get() = this[ fComb.indexOf { it is Finger.Index } ]
    val middle : Pair<dPoint, Polar> get() = this[ fComb.indexOf { it is Finger.Middle } ]
    val ring : Pair<dPoint, Polar> get() = this[ fComb.indexOf { it is Finger.Ring } ]
    val little : Pair<dPoint, Polar> get() = this[ fComb.indexOf { it is Finger.Little } ]

    operator fun get(fComb : fCombination) : List<Pair<dPoint, Polar>> {
        return listOfNotNull(
            if( fComb.contains { it is Finger.Thumb } ) thumb else null,
            if( fComb.contains { it is Finger.Index } ) index else null,
            if( fComb.contains { it is Finger.Middle } ) middle else null,
            if( fComb.contains { it is Finger.Ring } ) ring else null,
            if( fComb.contains { it is Finger.Little } ) little else null
        )
    }
}

/**
 * [Hand] に対応した [fPosition]
 * */
class fHandPosition(points : List<dPoint>, hand : Hand) : fCombPosition(
    fCombination.F5_T_I_M_R_L, points, hand) {
    val hand : Hand get() = dir as Hand

    init {
        if( points.size != 5 ) throw RuntimeException("ハンド・ポジションの構築には5つの点が初期値として必要です")
    }

    companion object {
        fun RHand(points : List<dPoint>) =
            fHandPosition(
                points,
                Finger.RHand
            )
        fun LHand(points : List<dPoint>) =
            fHandPosition(
                points,
                Finger.LHand
            )
    }
}
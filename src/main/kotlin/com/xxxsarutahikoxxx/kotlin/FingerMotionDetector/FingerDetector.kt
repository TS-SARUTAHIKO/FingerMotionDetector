package com.xxxsarutahikoxxx.kotlin.FingerMotionDetector

import java.lang.RuntimeException
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

class FingerDetector(
    val hPosition : fHandPosition
){
    /**
     * [fHandPosition] を元に組み合わせパターンごとの極座標を構築する
     *
     * ただし１つ指、５つ指は評価対象外とする
     * */
    val cPositions : List<fCombPosition> = fCombination.values().filter { it.size in 2..4 }.map {
        fCombPosition(
            hPosition,
            it
        )
    }

    val cDouble : List<fCombPosition> get() = cPositions.filter { it.fComb.isDouble }
    val cTriple : List<fCombPosition> get() = cPositions.filter { it.fComb.isTriple }
    val cQuadruple : List<fCombPosition> get() = cPositions.filter { it.fComb.isQuadruple }


    var evaluator2 : (Polar, fCombPosition)->(Float?) = { polar, comb ->
        val point = polar.xy

        if( abs( comb[1].second.argument - polar.argument ) <= PI*20/180 ){
            comb[1].second
                .run { (x-point.first) to (y-point.second) }
                .run { sqrt(first*first + second*second) }

        }else{
            null
        }
    }
    fun evaluate2(polar : Polar, fComb: fCombPosition) : Float? = evaluator2(polar, fComb)
    fun evaluate2(polar : Polar) : fCombination? {
        var list = cDouble.sortedBy { evaluate2(polar, it) ?: Float.MAX_VALUE }

        return list.firstOrNull()?.fComb
    }
    fun evaluate2(point1 : dPoint, point2 : dPoint) : fCombination? {
        val pos = fPosition(
            listOf(point1, point2),
            hPosition.dir
        )
        return evaluate2(pos[1].second)
    }

    var evaluator3 : (Polar, Polar, fCombPosition)->(Float?) = { polar1, polar2, comb ->
        if( abs( (comb[1].second.argument + comb[2].second.argument)/2 - (polar1.argument + polar2.argument)/2 ) <= PI*30/180 ){
            ((-20)..(+20) step 4).map { PI * it / 180 }
                .map {
                    comb[1].second
                        .dArgument(it.toFloat())
                        .run { (x-polar1.x) to (y-polar1.y) }
                        .run { sqrt(first*first + second*second) } +
                    comb[2].second
                        .dArgument(it.toFloat())
                        .run { (x-polar2.x) to (y-polar2.y) }
                        .run { sqrt(first*first + second*second) }
                }
                .min()
        }else{
            null
        }
    }
    fun evaluate3(polar1 : Polar, polar2 : Polar, fComb: fCombPosition) : Float? = evaluator3(polar1, polar2, fComb)
    fun evaluate3(polar1 : Polar, polar2 : Polar) : fCombination? {
        var list = cTriple.sortedBy { evaluate3(polar1, polar2, it) ?: Float.MAX_VALUE }

        return list.firstOrNull()?.fComb
    }
    fun evaluate3(point1 : dPoint, point2 : dPoint, point3 : dPoint) : fCombination? {
        val pos = fPosition(
            listOf(point1, point2, point3),
            hPosition.dir
        )
        return evaluate3(pos[1].second, pos[2].second)
    }

    var evaluator4 : (Polar, Polar, Polar, fCombPosition)->(Float?) = { polar1, polar2, polar3, comb ->
        if( abs( (comb[1].second.argument + comb[2].second.argument + comb[3].second.argument)/3 - (polar1.argument + polar2.argument + polar3.argument)/3 ) <= PI*30/180 ){
            ((-20)..(+20) step 4).map { PI * it / 180 }
                .map {
                    comb[1].second
                        .dArgument(it.toFloat())
                        .run { (x-polar1.x) to (y-polar1.y) }
                        .run { sqrt(first*first + second*second) } +
                    comb[2].second
                        .dArgument(it.toFloat())
                        .run { (x-polar2.x) to (y-polar2.y) }
                        .run { sqrt(first*first + second*second) } +
                    comb[3].second
                        .dArgument(it.toFloat())
                        .run { (x-polar3.x) to (y-polar3.y) }
                        .run { sqrt(first*first + second*second) }                }
                .min()
        }else{
            null
        }
    }
    fun evaluate4(polar1 : Polar, polar2 : Polar, polar3 : Polar, fComb: fCombPosition) : Float? = evaluator4(polar1, polar2, polar3, fComb)
    fun evaluate4(polar1 : Polar, polar2 : Polar, polar3 : Polar) : fCombination? {
        var list = cQuadruple.sortedBy { evaluate4(polar1, polar2, polar3, it) ?: Float.MAX_VALUE }

        return list.firstOrNull()?.fComb
    }
    fun evaluate4(point1 : dPoint, point2 : dPoint, point3 : dPoint, point4 : dPoint) : fCombination? {
        val pos = fPosition(
            listOf(
                point1,
                point2,
                point3,
                point4
            ), hPosition.dir
        )
        return evaluate4(pos[1].second, pos[2].second, pos[3].second)
    }


    operator fun invoke(vararg points : dPoint) : fCombination? {
        return when(points.size){
            1 -> fCombination.F1_U
            2 -> evaluate2( points[0], points[1] )
            3 -> evaluate3( points[0], points[1], points[2] )
            4 -> evaluate4( points[0], points[1], points[2], points[3] )
            5 -> fCombination.F5_T_I_M_R_L
            else -> throw RuntimeException("評価できる点列は個数が2～4である必要があります : $points")
        }
    }


    companion object {
        fun RHand(points : List<dPoint>) : FingerDetector =
            FingerDetector(
                fHandPosition.RHand(points)
            )
        fun LHand(points : List<dPoint>) : FingerDetector =
            FingerDetector(
                fHandPosition.LHand(points)
            )
    }
}
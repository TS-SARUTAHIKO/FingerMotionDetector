package com.xxxsarutahikoxxx.kotlin.FingerMotionDetector


/**
 * 指の組み合わせの全リスト
 * */
enum class fCombination(val code : String, val fingers : List<Finger>){
    // 指数 = 0
    F0_Non("Non"),

    // 指数 = 1
    F1_U("Single", Finger.Unknown),

    // 指数 = 2
    F2_T_I("2-Neighbor T-I",
        Finger.thumb,
        Finger.index
    ),
    F2_I_M("2-Neighbor I-M",
        Finger.index,
        Finger.middle
    ),
    F2_M_R("2-Neighbor M-R",
        Finger.middle,
        Finger.ring
    ),
    F2_R_L("2-Neighbor R-L",
        Finger.ring,
        Finger.little
    ),
    F2_L_T("2-Neighbor L-T",
        Finger.little,
        Finger.thumb
    ),

    F2_T_M("2-Distant T-M",
        Finger.thumb,
        Finger.middle
    ),
    F2_I_R("2-Distant I-R",
        Finger.index,
        Finger.ring
    ),
    F2_M_L("2-Distant M-L",
        Finger.middle,
        Finger.little
    ),
    F2_R_T("2-Distant R-T",
        Finger.ring,
        Finger.thumb
    ),
    F2_L_I("2-Distant L-I",
        Finger.little,
        Finger.index
    ),

    // 指数 = 3
    F3_T_I_M("3-Neighbor T-I-M",
        Finger.thumb,
        Finger.index,
        Finger.middle
    ),
    F3_I_M_R("3-Neighbor I-M-R",
        Finger.index,
        Finger.middle,
        Finger.ring
    ),
    F3_M_R_L("3-Neighbor M-R-L",
        Finger.middle,
        Finger.ring,
        Finger.little
    ),
    F3_R_L_T("3-Neighbor R-L-T",
        Finger.ring,
        Finger.little,
        Finger.thumb
    ),
    F3_L_T_I("3-Neighbor L-T-I",
        Finger.little,
        Finger.thumb,
        Finger.index
    ),

    F3_T_M_R("3-Distant T-M-R",
        Finger.thumb,
        Finger.middle,
        Finger.ring
    ),
    F3_I_R_L("3-Distant I-R-L",
        Finger.index,
        Finger.ring,
        Finger.little
    ),
    F3_M_L_T("3-Distant M-L-T",
        Finger.middle,
        Finger.little,
        Finger.thumb
    ),
    F3_R_T_I("3-Distant R-T-I",
        Finger.ring,
        Finger.thumb,
        Finger.index
    ),
    F3_L_I_M("3-Distant L-I-M",
        Finger.little,
        Finger.index,
        Finger.middle
    ),

    // 指数 = 5
    F4_T_I_M_R("Except-little",
        Finger.thumb,
        Finger.index,
        Finger.middle,
        Finger.ring
    ),
    F4_I_M_R_L("Except-thumb",
        Finger.index,
        Finger.middle,
        Finger.ring,
        Finger.little
    ),
    F4_M_R_L_T("Except-index",
        Finger.middle,
        Finger.ring,
        Finger.little,
        Finger.thumb
    ),
    F4_R_L_T_I("Except-middle",
        Finger.ring,
        Finger.little,
        Finger.thumb,
        Finger.index
    ),
    F4_L_T_I_M("Except-ring",
        Finger.little,
        Finger.thumb,
        Finger.index,
        Finger.middle
    ),

    // 指数 = 5
    F5_T_I_M_R_L("Quintuple",
        Finger.thumb,
        Finger.index,
        Finger.middle,
        Finger.ring,
        Finger.little
    )
    ;

    constructor(code : String, vararg fingers : Finger) : this(code, fingers.toList().sorted())

    /** [condition] を満たす指が存在するか */
    fun contains( condition : (Finger)->(Boolean) ) : Boolean {
        return fingers.filter(condition).isNotEmpty()
    }
    /** [condition] を満たす最初の指の番号 */
    fun indexOf( condition : (Finger)->(Boolean) ) : Int {
        return fingers.indexOfFirst(condition)
    }

    val size : Int get() = fingers.size
    val isSingle : Boolean get() = size == 1
    val isDouble : Boolean get() = size == 2
    val isTriple : Boolean get() = size == 3
    val isQuadruple : Boolean get() = size == 4
    val isQuintuple : Boolean get() = size == 5

    companion object {
        val Singles get() = values().filter { it.isSingle }
        val Doubles get() = values().filter { it.isDouble }
        val Triples get() = values().filter { it.isTriple }
        val Quadruples get() = values().filter { it.isQuadruple }
        val Quintuples get() = values().filter { it.isQuintuple }

        fun of( fingers : List<Finger> ) : fCombination {
            val fingers = fingers.sorted()
            return values().first { it.fingers == fingers }
        }
    }
}
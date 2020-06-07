package com.xxxsarutahikoxxx.kotlin.FingerMotionDetector


open class MotionDetector(
    var stableOffset : Float,
    var gestureOffset : Float,
    var rCondition : MotionDetector.(CrossDirection, List<Pair<dPoint, Long>>) -> (Boolean) = { _, _ -> false },
    val yReverse : Boolean = true
){
    val history : MutableList<Triple<dPoint, CrossDirection, Long >> = mutableListOf()

    val anchor : dPoint? get() = history.lastOrNull()?.first
    val rootTime : Long get() = history.first().third

    val redundant : List<Pair<dPoint, Long>> get(){
        val dir = history.last().second
        val index = history.indexOfLast { it.second != dir }

        return history.subList(index+1, history.size).map { it.first to it.third }
    }

    var stable : Boolean = true
    val directions : MutableList<CrossDirection> = mutableListOf()

    var isLongPressed : Boolean = false
    var LongPressTime : Long = 800



    fun detect( point : dPoint){
        val time = System.currentTimeMillis()

        if( anchor == null ){
            history.add(Triple( point,
                CrossDirection.Center, time ))
            return
        }

        val dx = point.first - anchor!!.first
        val dy = (point.second - anchor!!.second).run { if(yReverse) - this else this }

        // アンカーからの位置が ([anchor] ± [stableOffset], [anchor] ± [stableOffset]) の外ならば [stable] 状態を false (不安定)に変化させる
        if( stable && !( (dx in (-stableOffset)..(stableOffset)) && (dy in (-stableOffset)..(stableOffset)) ) ){
            stable = false
            onUnstable(point)
        }

        // 不安定状態での処理
        if( stable ){
            // Long Press 関係の処理
            if( (! isLongPressed) && (time - rootTime > LongPressTime) ){
                isLongPressed = true
                onLongPressed(point, false)
            }
        }else{
            // Dragging 関係の処理
            onDragging(point)

            // Gesture 関係の処理
            when {
                dx in (-gestureOffset)..(gestureOffset) && dy in (-gestureOffset)..(gestureOffset) -> CrossDirection.Center

                dx > dy && - dx < dy -> CrossDirection.Right
                dx > dy && - dx > dy -> CrossDirection.Down
                dx < dy && - dx < dy -> CrossDirection.Up
                dx < dy && - dx > dy -> CrossDirection.Left

                else -> CrossDirection.Center
            }.apply {
                if( this != CrossDirection.Center){
                    val last = history.lastOrNull()?.second

                    history.add(Triple(point, this, System.currentTimeMillis()) )

                    if( (last != this) || rCondition(this, redundant) ){
                        directions.add(this)
                        onGestured(directions.toList(), false)
                    }
                }
            }
        }
    }
    fun cleanup( point : dPoint){
        when {
            stable && ! isLongPressed -> onClicked(point) // Click
            stable &&   isLongPressed -> onLongPressed(point, true)
            ! stable -> onGestured(directions.toList(), true)
        }

        onFinished(point)

        stable = true
        isLongPressed = false
        history.clear()
        directions.clear()
    }

    /** 状態が不安定状態に変化したときのコールバック */
    open fun onUnstable(point : dPoint){}
    /** ドラッグが行われたときのコールバック */
    open fun onDragging( point : dPoint){}
    /** クリックが行われたときのコールバック */
    open fun onClicked(point : dPoint){}
    /** ロングプレスが行われたときのコールバック */
    open fun onLongPressed(point : dPoint, finished : Boolean){}
    /** ジェスチャーが行われたときのコールバック */
    open fun onGestured(directions : List<CrossDirection>, finished : Boolean){}
    /** 検出が終了したときのコールバック */
    open fun onFinished( point : dPoint){}

}

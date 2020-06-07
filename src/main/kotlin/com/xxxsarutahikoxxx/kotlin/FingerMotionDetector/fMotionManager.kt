package com.xxxsarutahikoxxx.kotlin.FingerMotionDetector


class fMotionManager(
    /** 指の基準位置 */
    hPosition: fHandPosition,
    var stableOffset : Float,
    var gestureOffset : Float,
    var rCondition : MotionDetector.(CrossDirection, List<Pair<dPoint, Long>>) -> (Boolean) = { _, _ -> false },
    val yReverse : Boolean = true
){
    /** 指判定に用いる検出器 */
    val fDetector : FingerDetector =
        FingerDetector(hPosition)

    /** 現在押されている指の組み合わせ */
    var fComb : fCombination =
        fCombination.F0_Non
    /** 指のモーションを処理するため[fMotionDetector]のリスト */
    val detectors : MutableList<fMotionDetector> = mutableListOf()

    /** [id]に対応した[fMotionDetector]を取得する */
    fun detector(id : Any) = detectors.first { it.id == id }


    fun detect( points : Map<Any, dPoint> ){
        points.forEach { detector(it.key).detect(it.value) }
    }
    fun detectPress(points : Map<Any, dPoint>, pressID : Any){
        val point = points[pressID]!!

        fComb = fDetector.invoke(*points.values.toTypedArray()) !!

        // 新規[fMotionDetector]の作成と検出
        fMotionDetector(
            this,
            pressID,
            stableOffset,
            gestureOffset,
            rCondition,
            yReverse
        ).apply {
            detectors.add(this)
        }

        val map = fDetector.hPosition.dir.sort(points.values.toList())
            .zip(fComb.fingers).toMap().run {
                points.keys.associateWith { this[points[it]] }
            }
        detectors.forEach {
            it.finger = map[it.id]!!
        }

        // TODO : 既存の指の情報を用いた指判定を実行する必要あり
        // TODO : 新たな指が検出されたことで元からのDetectorにも変更を与える必要がある場合

        //
        points.forEach { detector(it.key).detect(it.value) }
    }
    fun detectRelease(points : Map<Any, dPoint>, releaseID : Any){
        val point = points[releaseID]!!
        val detector = detector(releaseID)

        detector.cleanup(point)
        detectors.remove(detector)

        fComb =
            fCombination.of(fComb.fingers - detector.finger)
    }



    var onClicked : fMotionManager.(detector : fMotionDetector, point: dPoint)->(Unit) = { _, _->}
    var onGestured : fMotionManager.(detector : fMotionDetector, directions : List<CrossDirection>, finished: Boolean)->(Unit) = { _, _, _->}
}

class fMotionDetector(
    /** 親となる[fMotionManager] */
    val fManager: fMotionManager,
    /** 親の[fMotionManager]から割り振られたID */
    val id : Any,

    stableOffset : Float,
    gestureOffset : Float,
    rCondition : MotionDetector.(CrossDirection, List<Pair<dPoint, Long>>) -> (Boolean) = { _, _ -> false },
    yReverse : Boolean = true
) : MotionDetector(stableOffset, gestureOffset, rCondition, yReverse) {

    /** 親の[fMotionManager]割り当てられている指の種類 */
    lateinit var finger : Finger

    override fun onClicked(point: dPoint) = fManager.onClicked(fManager, this, point)
    override fun onGestured(directions: List<CrossDirection>, finished: Boolean) = fManager.onGestured(fManager, this, directions, finished)
}

// TODO : Gesture の指判定について、複数指のジェスチャーを行うとその本数以下の指でのジェスチャーが付随して発生する
// e.g. press(M) + press(R) -> 左フリック -> release(M) + release(R) というような2本指フリックを行う場合
// onGesture, Finger=M, fComb=M_R が呼び出された後に onGesture, Finger=R, fComb=R が呼び出される
//
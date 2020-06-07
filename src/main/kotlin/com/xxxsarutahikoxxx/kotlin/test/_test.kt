package com.xxxsarutahikoxxx.kotlin.test

import com.xxxsarutahikoxxx.kotlin.FingerMotionDetector.*
import java.io.File
import java.lang.RuntimeException
import javax.imageio.ImageIO


internal var out : Any?
    get() = throw RuntimeException("")
    set(value) { println(value) }


internal fun main(args: Array<String>) {

    val list = listOf(
        3f to 10f,
        6f to 4f,
        8f to 2.5f,
        10f to 3f,
        13f to 5f
    ).shuffled()

    fPosition.yReverse = true

    val detector = FingerDetector.RHand(list)
    ImageIO.write(detector.createImage(0.05f, 0.05f), "png", File("sample.png"))




    fMotionManager(
        fHandPosition.RHand(
            list
        ), 20f, 40f
    ).apply {
        onClicked = { detector, point ->

        }
        onGestured = { detector, directions, finished ->

        }
    }

}

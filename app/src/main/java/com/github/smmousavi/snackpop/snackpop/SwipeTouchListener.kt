package com.github.smmousavi.snackpop.snackpop

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import kotlin.math.abs
import kotlin.math.atan2

abstract class SwipeTouchListener(context: Context) :
    View.OnTouchListener {

    abstract fun rootView(): FrameLayout

    abstract fun onSingleTap()

    abstract fun onSwipeRightStart()

    abstract fun onSwipeLeftStart()

    abstract fun onSwipeRightEnd()

    abstract fun onSwipeLeftEnd()

    private val gestureDetector = GestureDetector(context, GestureListener())

    private var x1: Float = 0f
    private var x2: Float = 0f
    private var y1: Float = 0f
    private var y2: Float = 0f
    private var e1Init = false
    private var e2Init = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!e1Init) {
                    initFirstEvent(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!e2Init) {
                    if (!swipedHorizontally(x1, event.x)) {
                        // both motion events are initialized
                        initSecondEvent(event)
                        val dir = getDirection(x1, y1, x2, y2)
                        swipe(dir)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (Direction.isCurrentInitialized())
                    when (Direction.currentDir) {
                        Direction.LEFT -> {
                            if (e1Init && e2Init)
                                onSwipeLeftEnd()
                            invalidateEvents()
                        }
                        Direction.RIGHT -> {
                            if (e1Init && e2Init)
                                onSwipeRightEnd()
                            invalidateEvents()
                        }
                        else -> {
                            invalidateEvents()
                        }
                    }
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    private fun initFirstEvent(event: MotionEvent) {
        x1 = event.x
        y1 = event.y
        e1Init = true
    }

    private fun initSecondEvent(event: MotionEvent) {
        x2 = event.x
        y2 = event.y
        e2Init = true
    }

    private fun swipe(direction: Direction?): Boolean {
        return when (direction) {
            Direction.UP -> {
                false
            }
            Direction.DOWN -> {
                false
            }
            Direction.LEFT -> {
                onSwipeLeftStart()
                true
            }
            Direction.RIGHT -> {
                onSwipeRightStart()
                true
            }
            else -> false
        }
    }



    /**
     * Given two points in the plane p1=(x1, x2) and p2=(y1, y1), this method
     * returns the direction that an arrow pointing from p1 to p2 would have.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the direction
     */
    private fun getDirection(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Direction {
        val angle = getAngle(x1, y1, x2, y2)
        return Direction.fromAngle(angle)
    }

    /**
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    private fun getAngle(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Double {
        val rad =
            atan2(y1 - y2.toDouble(), x2 - x1.toDouble()) + Math.PI
        return (rad * 180 / Math.PI + 180) % 360
    }

    private fun swipedHorizontally(x1: Float, x2: Float): Boolean {
        return abs(x1 - x2) < 25
    }

    private fun invalidateEvents() {
        x1 = 0f
        y1 = 0f
        x2 = 0f
        y2 = 0f
        e1Init = false
        e2Init = false
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT;

        companion object {
            lateinit var currentDir: Direction

            fun isCurrentInitialized() = this::currentDir.isInitialized

            /**
             * Returns a direction given an angle.
             * Directions are defined as follows:
             *
             * @param Double an angle from 0 to 360 - e
             * @return the direction of an angle
             */
            fun fromAngle(angle: Double): Direction {
                currentDir = if (inRange(angle, 45f, 135f)) {
                    UP
                } else if (inRange(
                        angle,
                        0f,
                        45f
                    ) || inRange(angle, 315f, 360f)
                ) {
                    RIGHT
                } else if (inRange(angle, 225f, 315f)) {
                    DOWN
                } else {
                    LEFT
                }
                return currentDir
            }

            /**
             * @param angle an angle
             * @param init the initial bound
             * @param end the final bound
             * @return returns true if the given angle is in the interval [init, end).
             */
            private fun inRange(
                angle: Double,
                init: Float,
                end: Float
            ): Boolean {
                return angle >= init && angle < end
            }
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onSingleTap()
            return true
        }
    }
}
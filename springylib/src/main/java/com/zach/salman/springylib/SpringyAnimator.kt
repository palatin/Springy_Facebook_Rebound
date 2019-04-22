package com.zach.salman.springylib

import android.view.View

import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringSystem
import com.facebook.rebound.SpringUtil


class SpringyAnimator {
    private var startValue: Float = 0.toFloat()
    private var endValue: Float = 0.toFloat()
    private var tension: Double = 0.toDouble()
    private var fraction: Double = 0.toDouble()
    private var springSystem: SpringSystem? = null
    private var animationType: SpringAnimationType? = null
    private var springAnimatorListener: SpringyListener? = null
    private var delay = 0


    /**
     * Constructor for with Animation Type + Spring config + animation Values
     * * @param springConfig config class for the spring
     * @param  type SpringyAnimationType instance for animation type
     * @param  tension Spring tension for animation type
     * @param  fraction Spring fraction value for animation
     * @param  startValue where animation start from
     * @param  endValue where animation ends to
     */


    constructor(type: SpringAnimationType, tension: Double,
                fraction: Double, startValue: Float, endValue: Float) {
        this.tension = tension
        this.fraction = fraction
        this.startValue = startValue
        this.endValue = endValue
        springSystem = SpringSystem.create()
        animationType = type

    }


    /**
     * Constructor for with Animation Type + default config for spring + animation Values
     * * @param springConfig config class for the spring
     * @param  type SpringyAnimationType instance for animation type
     * @param  startValue where animation start from
     * @param  endValue where animation ends to
     */
    constructor(type: SpringAnimationType, startValue: Float,
                endValue: Float) {
        this.tension = DEFAULT_TENSION.toDouble()
        this.fraction = DEFAULT_FRACTION.toDouble()
        this.startValue = startValue
        this.endValue = endValue
        springSystem = SpringSystem.create()
        animationType = type

    }


    /**
     * @param  delay   int value  for SpringyAnimation delay each item if we have multiple items in
     * animation  raw.
     */
    fun setDelay(delay: Int) {
        this.delay = delay
    }

    fun startSpring(view: View) {
        setInitValue(view)
        val startAnimation = Runnable {
            val spring = springSystem!!.createSpring()
            spring.springConfig = SpringConfig.fromOrigamiTensionAndFriction(tension, fraction)
            spring.addListener(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring?) {
                    view.visibility = View.VISIBLE
                    val value = SpringUtil.mapValueFromRangeToRange(spring!!.currentValue, 0.0, 1.0, startValue.toDouble(), endValue.toDouble()).toFloat()
                    when (animationType) {
                        SpringAnimationType.TRANSLATEY -> view.translationY = value
                        SpringAnimationType.TRANSLATEX -> view.translationX = value
                        SpringAnimationType.ALPHA -> view.alpha = value
                        SpringAnimationType.SCALEY -> view.scaleY = value
                        SpringAnimationType.SCALEX -> view.scaleX = value
                        SpringAnimationType.SCALEXY -> {
                            view.scaleY = value
                            view.scaleX = value
                        }
                        SpringAnimationType.ROTATEY -> view.rotationY = value
                        SpringAnimationType.ROTATEX -> view.rotationX = value
                        SpringAnimationType.ROTATION -> view.rotation = value
                    }
                }

                override fun onSpringAtRest(spring: Spring?) {
                    if (springAnimatorListener != null) {
                        springAnimatorListener!!.onSpringStop()
                    }
                }

                override fun onSpringActivate(spring: Spring?) {
                    if (springAnimatorListener != null) {
                        springAnimatorListener!!.onSpringStart()
                    }
                }

            })
            spring.endValue = 1.0
        }
        view.postDelayed(startAnimation, delay.toLong())

    }

    /**
     * @param  view  instance for  set pre animation value
     */
    private fun setInitValue(view: View) {
        when (animationType) {
            SpringAnimationType.TRANSLATEY -> view.translationY = startValue
            SpringAnimationType.TRANSLATEX -> view.translationX = startValue
            SpringAnimationType.ALPHA -> view.alpha = startValue
            SpringAnimationType.SCALEY -> view.scaleY = startValue
            SpringAnimationType.SCALEX -> view.scaleX = startValue
            SpringAnimationType.SCALEXY -> {
                view.scaleY = startValue
                view.scaleX = startValue
            }
            SpringAnimationType.ROTATEY -> view.rotationY = startValue
            SpringAnimationType.ROTATEX -> view.rotationX = startValue
            SpringAnimationType.ROTATION -> view.rotation = startValue
        }
    }

    /*
* Springy Listener to track the Spring
* */
    fun setSpringyListener(springyListener: SpringyListener) {
        this.springAnimatorListener = springyListener
    }

    companion object {
        private val DEFAULT_TENSION = 40f
        private val DEFAULT_FRACTION = 7f
    }


}

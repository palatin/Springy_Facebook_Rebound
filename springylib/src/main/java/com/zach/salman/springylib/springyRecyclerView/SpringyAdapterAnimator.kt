package com.zach.salman.springylib.springyRecyclerView


import android.view.View
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.RecyclerView

import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringSystem
import com.facebook.rebound.SpringUtil



class SpringyAdapterAnimator(private val parent: RecyclerView) {

    private var tension: Int = 0
    private var fraction: Int = 0

    private val parentHeight: Int
    private val parentWidth: Int
    private val mSpringSystem: SpringSystem = SpringSystem.create()
    private var animationType: SpringyAdapterAnimationType? = null
    private var mFirstViewInit = true
    private var mLastPosition = -1
    private var mStartDelay: Long = 0

    init {
        animationType = SpringyAdapterAnimationType.SLIDE_FROM_BOTTOM
        parentHeight = parent.getResources().getDisplayMetrics().heightPixels
        parentWidth = parent.getResources().getDisplayMetrics().widthPixels
        mStartDelay = INIT_DELAY
        tension = INIT_TENSION
        fraction = INIT_FRICTION
    }

    /*
* setInitDelay @param initDelay for set delay at screen creation
* */
    fun setInitDelay(initDelay: Long) {
        mStartDelay = initDelay
    }

    fun setSpringAnimationType(type: SpringyAdapterAnimationType) {
        animationType = type
    }


    fun addConfig(tension: Int, fraction: Int) {
        this.tension = tension
        this.fraction = fraction
    }

    /**
     * onSpringyItemCreate call in Adapter's Constructor method
     * @param item itemView instance from RecyclerView's OnCreateView method
     */
    fun onSpringItemCreate(item: View) {

        if (mFirstViewInit) {
            setAnimation(item, mStartDelay, tension, fraction)
            mStartDelay += PER_ITEM_GAPE
        }
    }


    /**
     * * onSpringyItemBind call in RecyclerView's onBind for scroll effects
     * @param  item itemView instance from RecyclerView's onBind method
     * @param  position from RecyclerView's onBind method
     */
    fun onSpringItemBind(item: View, position: Int) {

        if (!mFirstViewInit && position > mLastPosition) {
            setAnimation(item, 0, tension - tension / 4, fraction)
            mLastPosition = position
        }
    }


    private fun setAnimation(item: View, delay: Long,
                             tension: Int, friction: Int) {
        setInitValue(item)
        val startAnimation = Runnable {
            val config = SpringConfig(tension.toDouble(), friction.toDouble())
            val spring = mSpringSystem.createSpring()
            spring.springConfig = config
            spring.addListener(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring?) {
                    when (animationType) {
                        SpringyAdapterAnimationType.SLIDE_FROM_BOTTOM -> item.translationY = getMappedValue(spring)
                        SpringyAdapterAnimationType.SLIDE_FROM_LEFT -> item.translationX = getMappedValue(spring)
                        SpringyAdapterAnimationType.SLIDE_FROM_RIGHT -> item.translationX = getMappedValue(spring)
                        SpringyAdapterAnimationType.SCALE -> {
                            item.scaleX = getMappedValue(spring)
                            item.scaleY = getMappedValue(spring)
                        }
                    }

                }

                override fun onSpringEndStateChange(spring: Spring?) {
                    mFirstViewInit = false
                }
            })
            spring.endValue = 1.0
        }

        parent.postDelayed(startAnimation, delay)
    }

    private fun setInitValue(item: View) {

        when (animationType) {
            SpringyAdapterAnimationType.SLIDE_FROM_BOTTOM -> item.translationY = parentHeight.toFloat()
            SpringyAdapterAnimationType.SLIDE_FROM_LEFT -> item.translationX = (-parentWidth).toFloat()
            SpringyAdapterAnimationType.SLIDE_FROM_RIGHT -> item.translationX = parentWidth.toFloat()
            SpringyAdapterAnimationType.SCALE -> {
                item.scaleX = 0f
                item.scaleY = 0f
            }
            else -> item.translationY = parentHeight.toFloat()
        }
    }

    private fun getMappedValue(spring: Spring?): Float {

        val value: Float
        when (animationType) {
            SpringyAdapterAnimationType.SLIDE_FROM_BOTTOM -> value = SpringUtil.mapValueFromRangeToRange(spring!!.currentValue, 0.0, 1.0, parentHeight.toDouble(), 0.0).toFloat()
            SpringyAdapterAnimationType.SLIDE_FROM_LEFT -> value = SpringUtil.mapValueFromRangeToRange(spring!!.currentValue, 0.0, 1.0, (-parentWidth).toDouble(), 0.0).toFloat()
            SpringyAdapterAnimationType.SLIDE_FROM_RIGHT -> value = SpringUtil.mapValueFromRangeToRange(spring!!.currentValue, 0.0, 1.0, parentWidth.toDouble(), 0.0).toFloat()
            SpringyAdapterAnimationType.SCALE -> value = SpringUtil.mapValueFromRangeToRange(spring!!.currentValue, 0.0, 1.0, 0.0, 1.0).toFloat()
            else -> value = SpringUtil.mapValueFromRangeToRange(spring!!.currentValue, 0.0, 1.0, parentHeight.toDouble(), 0.0).toFloat()
        }
        return value
    }

    companion object {

        private const val INIT_DELAY = 100L

        private const val INIT_TENSION = 200
        private const val INIT_FRICTION = 20


        private const val PER_ITEM_GAPE = 100
    }

}

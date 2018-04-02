package com.canvastools.canvastools.util.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View

class BatteryCanvas(context : Context , attrSet : AttributeSet) : View(context, attrSet){

    private var redPaint  =  Paint() // Paint that will be used to Handle colors and style of the Path
    private var path  = Path()
    private var rectOuterBattery = RectF() /// RechF used to create the Rectangle and for support from API 19
    private var rectInnerBatteryBar =  RectF() /// RechF used to create the Rectangle and for support from API 19
    private var radius : Float = 50f

    private var strokeWidth : Float = 0f
    private var minValue : Int = 0
    private var maxValue : Int = 0
    private var intervals : Int = 0
    private lateinit var colorList : IntArray
    private var shouldMaintainAspectRatio : Boolean = false

    private fun initInnerBar() {
        rectInnerBatteryBar.left = 0f
        rectInnerBatteryBar.right = strokeWidth * 2
        rectInnerBatteryBar.top = 0f
        rectInnerBatteryBar.bottom = 0f
    }

    /**
     * @Method initialize , will be called for the initialization of the BatteryCanvas
     * @param strokeWidth : Width of the Stroke that is the outer skin of the Battery
     * @param minValue : Minimum value of the battery to be set for example it can start with 0
     * @param maxValue : Maximum value that can be set, for example 100 or 200
     * @param intervals : Intervals at which the check is needed to be made for the change of the Style and color of the Battery
     * @param colorList : Color list to be used, if color list is less then
     * (maxValue/intervals) then last color from list will be used as default
     * @param shouldMaintainAspectRatio : Whether the View should maintain the aspectRatio depending width and height
     *
     * Provided the default value for anyone who just want to try the Library
     *
     */
    fun initialize(strokeWidth : Float = 5f, minValue : Int = 0
                   , maxValue : Int = 100, intervals : Int = 30
                   , colorList : IntArray = intArrayOf(android.R.color.holo_red_dark, android.R.color.holo_green_dark, android.R.color.holo_blue_dark)
                   , shouldMaintainAspectRatio : Boolean = false){

        this.strokeWidth = strokeWidth
        this.minValue = minValue
        this.maxValue = maxValue
        this.intervals = intervals
        this.colorList = colorList
        this.shouldMaintainAspectRatio = shouldMaintainAspectRatio
        radius = strokeWidth *2
        initializePaints()
    }

    fun initializePaints(){
        redPaint = Paint()
        redPaint.style = Paint.Style.STROKE
        redPaint.color = ContextCompat.getColor(context ,android.R.color.holo_red_dark)
        redPaint.strokeWidth = strokeWidth
    }

    fun intitializeBatterySkeleton(){
        rectOuterBattery.left = strokeWidth
        rectOuterBattery.top = strokeWidth
        rectOuterBattery.right = measuredWidth.toFloat() - (strokeWidth *2)
        rectOuterBattery.bottom = measuredHeight.toFloat() - (strokeWidth*2)

    }

    /**
     * @method createInnerBatteryBar creates the Battery Status bar inside the
     */
    fun createInnerBatteryBar() : RectF{

        val eachWidthOfbar = (rectOuterBattery.right - rectOuterBattery.left - (strokeWidth * (intervals +3))) /  intervals

        rectInnerBatteryBar.top =  rectOuterBattery.top + (strokeWidth * 2)
        rectInnerBatteryBar.bottom =  rectOuterBattery.bottom - (strokeWidth * 2)
        rectInnerBatteryBar.left = rectInnerBatteryBar.right + strokeWidth
        rectInnerBatteryBar.right = rectInnerBatteryBar.left + eachWidthOfbar

        return rectInnerBatteryBar
    }

    /**
     * call invalidate() method to redraw the complete view
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        intitializeBatterySkeleton()
        initInnerBar()
        redPaint.style = Paint.Style.STROKE
        with(path) {
            addRoundRect(rectOuterBattery ,radius, radius,Path.Direction.CCW )
        }
        canvas?.drawPath(path, redPaint)

        redPaint.style = Paint.Style.FILL

        for(x in 1..(intervals)){
            canvas?.drawRoundRect(createInnerBatteryBar() , radius, radius, redPaint)
        }
        canvas?.drawRoundRect(updateTopBatteryPoint() , 50f,0f,redPaint)

    }

    /**
     * The Top elevated section of the battery
     */
    private fun updateTopBatteryPoint(): RectF{
        rectOuterBattery.left = rectOuterBattery.right-(strokeWidth/2)
        rectOuterBattery.top = (rectOuterBattery.bottom / 8f) * 3f
        rectOuterBattery.bottom = (rectOuterBattery.bottom / 8f) * 5f
        rectOuterBattery.right = rectOuterBattery.right + strokeWidth*2

        return rectOuterBattery
    }

}
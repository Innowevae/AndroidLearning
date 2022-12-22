package sahu.ritvik.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.util.*

class DrawingView(context: Context, attrs: AttributeSet) : View(context,attrs) {

    private var mDrawPath:Custompath? = null
    private var mCanvasbitmap: Bitmap?=null
    private var mDrawPaint: Paint?=null
    private var mCanvasPaint: Paint?=null
    private var mBrushSize :Float = 0.toFloat()
    private var color= Color.BLACK
    private var canvas: Canvas?=null
    private val mPaths = ArrayList<Custompath>()
    private val mUndoPaths = ArrayList<Custompath>()


    init{
        setUpDrawing()
    }

    fun onClickUndo(){
        if(mPaths.size>0){
            mUndoPaths.add(mPaths.removeAt((mPaths.size-1)))
            invalidate()
        }
    }

    private fun setUpDrawing(){
        mDrawPaint=Paint()
        mDrawPath=Custompath(color,mBrushSize)
        mDrawPaint!!.color=color
        mDrawPaint!!.style=Paint.Style.STROKE
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize=20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasbitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas =Canvas(mCanvasbitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasbitmap!!,0f,0f,mCanvasPaint)
        for(path in mPaths){
            mDrawPaint!!.strokeWidth=path.brushThickness
            mDrawPaint!!.color=path.Color
            canvas.drawPath(path,mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness
            mDrawPaint!!.color=mDrawPath!!.Color //check thissssssssssssssssssssssssssssssss
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.Color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!, touchY!!)
            }

            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(touchX!!, touchY!!)
            }

            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!)
                mDrawPath = Custompath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true


    }
        fun setSizeForBrush(newSize: Float) {
            mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize,
                resources.displayMetrics)
            mDrawPaint?.strokeWidth = mBrushSize
        }



    fun setColor(newColor:String) {
        color = Color.parseColor(newColor)
        mDrawPaint!!.color=color
    }

    internal inner class Custompath(var Color :Int,
                                    var brushThickness:Float): Path()



}

package com.example.myrollingball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),SensorEventListener,SurfaceHolder.Callback {
    private var surfaceWidth:Int = 0
    private var surfaceHeight:Int = 0
    private val radius = 50.0f
    private val coef = 1000.0f

    private var ballX:Float = 400f
    private var ballY:Float = 100f
    private var vx:Float = 0f
    private var vy:Float = 0f
    private var time:Long = 0L
    private var flag = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val holder = surfaceView.holder
        holder.addCallback(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        button.setOnClickListener{reset()}
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(event==null){
            return
        }
        if(time==0L){
            time = System.currentTimeMillis()
        }
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            val x = event.values[0]*-1
            val y = event.values[1]
            var t = (System.currentTimeMillis() - time).toFloat()
            time = System.currentTimeMillis()
            t /= 1000.0f
            if(flag){
                val dx = (vx*t) + (x*t*t)/2.0f
                val dy = (vy*t) + (y*t*t)/2.0f
                ballX += (dx*coef)
                ballY += (dy*coef)
                vx +=(x*t)
                vy +=(y*t)
                if( (ballX -radius)<0 && vx<0 ){
                    // 左にぶつかった時
                    vx = -vx /1.5f;
                    ballX = radius;
                }else if( (ballX+radius)>surfaceWidth && vx>0){
                    // 右にぶつかった時
                    vx = -vx/1.5f;
                    ballX = (surfaceWidth-radius);
                }
                // 上下について
                if( (ballY -radius)<0 && vy<0 ){
                    // 下にぶつかった時
                    vy = -vy /1.5f;
                    ballY = radius;
                }else if( (ballY+radius)>surfaceHeight && vy>0 ){
                    // 上にぶつかった時
                    vy = -vy/1.5f;
                    ballY = surfaceHeight -radius;
                }
                if(ballY==ballX&&vx>0&&vy>0){
                    vx=-vx/1.5f
                    vy=-vy/1.5f
                }
            }
            // キャンバスに描画
            this.drawCanvas();
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
            this,
            accSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        val accSensor = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor.unregisterListener(this)
    }


    private fun drawCanvas(){
        val canvas = surfaceView.holder.lockCanvas()
        canvas.drawColor(Color.DKGRAY)
        canvas.drawRect(200f,500f,600f,600f,Paint().apply { color = Color.BLACK })
        fall(200f,500f,600f,600f)
        canvas.drawRect(500f,900f,600f,1200f,Paint().apply { color = Color.BLACK })
        fall(500f,900f,600f,1200f)
        canvas.drawRect(0f,1000f,300f,1400f,Paint().apply { color = Color.BLACK })
        fall(0f,1000f,300f,1400f)
        canvas.drawRect(400f,600f,500f,700f,Paint().apply { color = Color.YELLOW })
        clear(400f,600f,500f,700f)
        if(flag){
            canvas.drawCircle(
                ballX,
                ballY,
                radius,
                Paint().apply{
                    color = Color.RED
                }
            )
        }
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }
    private fun reset(){
        ballX = 400f
        ballY = 100f
        vx = 0f
        vy = 0f
        time = 0L
        flag = true
        image.setImageResource(R.drawable.pose_ganbarou_man)
        text.setText(R.string.ganbare)
    }
    private fun fall(left:Float,top:Float,right:Float,bottom:Float){
        if(left<ballX&&ballX<right&&top<ballY&&ballY<bottom){
            flag = false
            image.setImageResource(R.drawable.businessman4_cry)
            text.setText(R.string.zannnenn)
        }
    }
    private fun clear(left:Float,top:Float,right:Float,bottom:Float){
        if(left<ballX&&ballX<right&&top<ballY&&ballY<bottom){
            image.setImageResource(R.drawable.clear)
            flag=false
            text.setText(R.string.omedetou)
        }
    }

}

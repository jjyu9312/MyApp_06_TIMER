package com.kkuber.myapp_06_timer

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // by lazy : View에 접근하기 위해 사용
    private val remianMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)

    }

    private var currentCountDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
    }

    // 각각의 뷰에 있는 리스너와 실제 로직을 연결
    fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    remianMinutesTextView.text = "%02d".format(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                    currentCountDownTimer?.cancel() // 현재 카운트다운 타이머 멈춤
                    currentCountDownTimer = null
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                    seekBar ?: return

                    currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
                    currentCountDownTimer?.start()
                }
            }
        )
    }

    private fun createCountDownTimer(initialMillis: Long) =

        object: CountDownTimer(initialMillis, 1000L) {
            override fun onTick(p0: Long) {

                updateRemainingTimes(p0) // 1초 마다 한번씩 텍스트 뷰 갱신
                updateSeekBar(p0) // 1초 마다 Seek Bar 갱신

            }

            override fun onFinish() {

                updateRemainingTimes(0)
                updateSeekBar(0)

            }
        }

    @SuppressLint("SetTextI18n")
    private fun updateRemainingTimes(remainMillis: Long) {

        val remainSeconds = remainMillis / 1000

        remianMinutesTextView.text = "%02d".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {

        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}
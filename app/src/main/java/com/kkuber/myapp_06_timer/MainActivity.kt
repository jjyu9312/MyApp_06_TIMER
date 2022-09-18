package com.kkuber.myapp_06_timer

import android.annotation.SuppressLint
import android.media.SoundPool
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

    private val soundPool = SoundPool.Builder().build()

    private var tickingSoundId: Int? = null

    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()

        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    // 각각의 뷰에 있는 리스너와 실제 로직을 연결
    fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {

                        updateRemainingTimes(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                    seekBar ?: return

                    if(seekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown()
                    }
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
                completeCountDown()
            }


        }



    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun startCountDown() {

        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun completeCountDown() {

        updateRemainingTimes(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let { bellSoundId ->
            soundPool.play(bellSoundId, 1F, 1F, 0, 0, 1F)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainingTimes(remainMillis: Long) {

        val remainSeconds = remainMillis / 1000

        remianMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {

        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

    private fun initSounds() {

        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }
}
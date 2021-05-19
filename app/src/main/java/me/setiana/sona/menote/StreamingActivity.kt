package me.setiana.sona.menote

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_streaming.*
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import me.setiana.sona.menote.databinding.ActivityStreamingBinding
import org.videolan.libvlc.interfaces.IMedia

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class StreamingActivity : AppCompatActivity() {


    companion object {
        private const val TESTING_LINK = "https://live.cnnindonesia.com/livecnn/smil:cnntv.smil/playlist.m3u8"
    }

    private lateinit var binding: ActivityStreamingBinding

    private var mLibVLC: LibVLC? = null
    private var mMediaPlayer: MediaPlayer? = null
    private val checker = Handler(Looper.getMainLooper())

    inner class Runner : Runnable{
        override fun run() {
            mMediaPlayer?.let {
                Log.d("STREAMING_TV", "MediaPlayer: ${it.time}")
            }
            checker.post(this)
        }
    }

    private val handlerVisible = Handler(Looper.getMainLooper())

    private val visibleViewHandler = Runnable {
        with(binding){
            toolbar.visibility = View.GONE
            btnPlay.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamingBinding.inflate(layoutInflater)
        hideSystemUI()
        setContentView(binding.root)
        initPlayerView()
        with(binding){
            toolbar.setNavigationOnClickListener { onBackPressed() }
            frame.setOnClickListener {
                toolbar.visibility = View.VISIBLE
                if(!progressBar.isShown){
                    btnPlay.visibility = View.VISIBLE
                }
                handlerVisible.postDelayed(visibleViewHandler, 5000L)
            }
            btnPlay.setOnClickListener{
                mMediaPlayer?.let {
                    if(it.isPlaying){
                        it.setEventListener(null)
                        it.stop()
                        it.detachViews()
                        btnPlay.setImageDrawable(ContextCompat.getDrawable(this@StreamingActivity, R.drawable.ic_play))
                    }else{
                        initPlayerView()
                        btnPlay.setImageDrawable(ContextCompat.getDrawable(this@StreamingActivity, R.drawable.ic_pause))
                    }
                }

            }
        }
        //checker.post(Runner())
    }

    private fun initPlayerView() {
        mLibVLC = LibVLC(this, ArrayList<String>().apply {
            add("--no-drop-late-frames")
            add("--no-skip-frames")
            add("--rtsp-tcp")
            add("-vvv")
        })
        mMediaPlayer = MediaPlayer(mLibVLC)
        mMediaPlayer?.attachViews(binding.viewVlcLayout, null, true, false)
        try {
            Media(mLibVLC, Uri.parse(TESTING_LINK)).apply {
                setHWDecoderEnabled(true, false)
                addOption(":network-caching=150")
                addOption(":clock-jitter=0")
                addOption(":clock-synchro=0")
                addOption(":fullscreen")
                mMediaPlayer?.let {
                    it.media = this
                    it.aspectRatio = "16:9"
                    it.scale = 1.8f
                }
            }.release()
            mMediaPlayer?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMediaPlayer?.setEventListener {
            when(it.type){
                MediaPlayer.Event.TimeChanged -> {
                    binding.progressBar.hide()
                }
                MediaPlayer.Event.Buffering -> {
                    if(it.buffering == 100f){
                        binding.progressBar.hide()
                    }else{
                        binding.progressBar.show()
                    }
                }
            }
        }
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer?.stop()
        mMediaPlayer?.detachViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        mLibVLC?.release()
    }
}
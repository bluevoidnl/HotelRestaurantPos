package com.spuyt.assignment

import androidx.appcompat.app.AppCompatActivity
//import androidx.activity.viewModels
import android.os.Bundle
import android.widget.ImageView
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

//    private val viewModel: PixelateViewModel by viewModels(){
//        PixelateViewModel()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val image=findViewById<ImageView>(R.id.pix)
        thread{
            val bitmap=PixelateViewModel.pixelateTestImage(this)
            image.post { image.setImageBitmap(bitmap) }
        }
    }
}
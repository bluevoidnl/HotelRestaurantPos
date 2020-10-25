package com.spuyt.assignment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import java.io.InputStream

class PixelateViewModel : ViewModel() {

    val pixelationProgress = MutableLiveData<Int>()
    val pixelatedImage = MutableLiveData<Resource<Bitmap>>()
    var pixelateJob: Job? = null
    private var lastPhotoBitmap: Bitmap? = null
    private var nrBlocks = 20
    fun setPhotoBitmap(bitmap: Bitmap) {
        lastPhotoBitmap = bitmap
        pixelateImage()
    }

    fun setNrBLocks(blockNr: Int) {
        this.nrBlocks = blockNr
        pixelateImage()
    }

    private fun pixelateImage() {
        lastPhotoBitmap?.let {
            viewModelScope.launch(Dispatchers.Default) {
                pixelateJob?.cancelAndJoin()
                // start pixelation job
                pixelateJob = viewModelScope.launch(Dispatchers.Default) {
                    val pixBitmap = pixelate(it, nrBlocks)
                    pixelatedImage.postValue(Resource.Success(pixBitmap))
                }
            }
        }
    }

    private fun pixelate(image: Bitmap, nrBlocks: Int): Bitmap {
        val w = image.width
        val h = image.height

        Log.i("xxx", "$w $h")

        val blockSize = w / nrBlocks
        val targetW = (w / blockSize)
        val targetH = (h / blockSize)
        val targetImage = image.copy(Bitmap.Config.ARGB_8888, true)
        for (wIndex in 0 until targetW) {
            var endX = 0
            for (hIndex in 0 until targetH + 1) {
                endX = createPixel(blockSize, wIndex, hIndex, image, targetImage)
            }
            pixelatedImage.postValue(Resource.Loading(targetImage))
            pixelationProgress.postValue(endX)
        }
        return targetImage
    }

    fun pixelateTestImage(context: Context) {
        val bitmap: InputStream = context.assets.open("funny_car.jpg")
        lastPhotoBitmap = BitmapFactory.decodeStream(bitmap)
        pixelateImage()
    }

    private fun createPixel(blockSize: Int, wIndex: Int, hIndex: Int, image: Bitmap, targetImage: Bitmap): Int {
        var redSum = 0
        var blueSum = 0
        var greenSum = 0
        var pixelsInBlock = 0
        val startX = wIndex * blockSize
        val endX = blockSize + startX - 1
        //println("$startX $endX ${image.width}")
        for (x: Int in startX..endX) {
            val startY = hIndex * blockSize
            val endY = blockSize + startY - 1
            //println("$startY $endY ${image.height}")
            for (y: Int in startY..endY) {
                if (y < image.height) {
                    val intColor = image[x, y]
                    redSum += Color.red(intColor)
                    blueSum += Color.blue(intColor)
                    greenSum += Color.green(intColor)
                    pixelsInBlock++
                }
            }
        }
        if (pixelsInBlock > 0) {
            val blockColor = Color.argb(255, redSum / pixelsInBlock, greenSum / pixelsInBlock, blueSum / pixelsInBlock)
            for (x: Int in startX..endX) {
                val startY = hIndex * blockSize
                val endY = blockSize + startY - 1
                //println("$startY $endY ${image.height}")
                for (y: Int in startY..endY) {
                    if (y < targetImage.height) {
                        targetImage[x, y] = blockColor
                    }
                }
            }
        }
        return endX
    }
}
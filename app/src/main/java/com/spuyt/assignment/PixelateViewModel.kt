package com.spuyt.assignment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.InputStream
import kotlin.concurrent.thread

class PixelateViewModel : ViewModel() {

    val pixelatedImage = MutableLiveData<Bitmap>()
    //val pixelatedImage = MutableLiveData<Resource<Bitmap>>()

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
            thread {
                // todo: make lifecycle aware
                // todo: cancel previous calculation when called again
                val pixBitmap = pixelate(it, nrBlocks)
                //pixelatedImage.postValue(Resource.Success(pixBitmap))
                pixelatedImage.postValue(pixBitmap)
            }
        }
    }

   private fun pixelate(image: Bitmap, nrBlocks: Int): Bitmap {
        //BitmapFactory.decodeFile("assets/)
        val w = image.width
        val h = image.height

        // todo: fix rounding error
        val blockSize = w / nrBlocks
        val targetW = (w / blockSize)
        val targetH = (h / blockSize)
        //println("$W $H $blockSize target $targetW $targetH")
        val targetImage = image.copy(Bitmap.Config.ARGB_8888, true)
        // val targetImage = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        for (wIndex in 0 until targetW) {
            for (hIndex in 0 until targetH+1) {
                createPixel(blockSize, wIndex, hIndex, image, targetImage)
            }

            pixelatedImage.postValue(targetImage)
        }
        return targetImage
    }

    fun pixelateTestImage(context: Context) {
        val bitmap: InputStream = context.assets.open("funny_car.jpg")
        lastPhotoBitmap = BitmapFactory.decodeStream(bitmap)
        pixelateImage()
    }

    private fun createPixel(blockSize: Int, wIndex: Int, hIndex: Int, image: Bitmap, targetImage: Bitmap) {
        var redSum = 0
        var blueSum = 0
        var greenSum = 0
        var pixelsInBlock=0
        val startX = wIndex * blockSize
        val endX = blockSize + startX - 1
        //println("$startX $endX ${image.width}")
        for (x: Int in startX..endX) {
            val startY = hIndex * blockSize
            val endY = blockSize + startY - 1
            //println("$startY $endY ${image.height}")
            for (y: Int in startY..endY) {
                if(y<image.height) {
                    val intColor = image[x, y]
                    redSum += Color.red(intColor)
                    blueSum += Color.blue(intColor)
                    greenSum += Color.green(intColor)
                    pixelsInBlock++
                }
            }
        }
        val blockColor = Color.argb(255, redSum / pixelsInBlock, greenSum / pixelsInBlock, blueSum / pixelsInBlock)
        for (x: Int in startX..endX) {
            val startY = hIndex * blockSize
            val endY = blockSize + startY - 1
            //println("$startY $endY ${image.height}")
            for (y: Int in startY..endY) {
                if(y<targetImage.height) {
                    targetImage[x, y] = blockColor
                }
            }
        }
    }
}
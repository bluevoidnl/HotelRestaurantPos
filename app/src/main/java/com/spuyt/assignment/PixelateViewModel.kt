package com.spuyt.assignment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import java.io.InputStream

class PixelateViewModel : ViewModel() {

    companion object {
        fun pixelateTestImage(context: Context): Bitmap {
            val bitmap: InputStream = context.assets.open("funny_car.jpg")
            val image = BitmapFactory.decodeStream(bitmap)
            return pixelate(image)
        }

        fun pixelate(image: Bitmap): Bitmap {
            //BitmapFactory.decodeFile("assets/)
            val W = image.width
            val H = image.height

            val div = 30
            val blockSize = W / div
            val targetW = (W / blockSize)
            val targetH = (H / blockSize)
            //println("$W $H $blockSize target $targetW $targetH")

            val targetImage = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
            for (wIndex in 0 until targetW) {
                for (hIndex in 0 until targetH) {
                    createPixel(blockSize, wIndex, hIndex, image, targetImage)
                }
            }
            return targetImage
        }

        private fun createPixel(
            blockSize: Int,
            wIndex: Int,
            hIndex: Int,
            image: Bitmap,
            targetImage: Bitmap
        ) {
            var redSum = 0
            var blueSum = 0
            var greenSum = 0
            val startX = wIndex * blockSize
            val endX = blockSize + startX - 1
            //println("$startX $endX ${image.width}")
            for (x: Int in startX..endX) {
                val startY = hIndex * blockSize
                val endY = blockSize + startY - 1
                //println("$startY $endY ${image.height}")
                for (y: Int in startY..endY) {
                    val intColor = image[x, y]
                    redSum += Color.red(intColor)
                    blueSum += Color.blue(intColor)
                    greenSum += Color.green(intColor)
                }
            }
            val pixelsInBlock = blockSize * blockSize
            val blockColor = Color.argb(
                255,
                redSum / pixelsInBlock,
                greenSum / pixelsInBlock,
                blueSum / pixelsInBlock
            )
            for (x: Int in startX..endX) {
                val startY = hIndex * blockSize
                val endY = blockSize + startY - 1
                //println("$startY $endY ${image.height}")
                for (y: Int in startY..endY) {
                    targetImage.set(x, y, blockColor)
                }
            }
        }


    }
}
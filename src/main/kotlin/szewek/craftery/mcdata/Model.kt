package szewek.craftery.mcdata

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect

/**
 * Abstract class for drawing Minecraft items and blocks in a slot.
 */
abstract class Model {

    abstract fun draw(scope: DrawScope)

    object Empty: Model() {
        override fun draw(scope: DrawScope) {
            scope.drawCircle(Color.Yellow, 4f)
        }
    }

    object Failed: Model() {
        override fun draw(scope: DrawScope) {
            scope.drawCircle(Color.Red, 6f)
        }

    }

    class Custom(private val img: Image): Model() {
        override fun draw(scope: DrawScope) {
            val size = scope.size
            scope.drawIntoCanvas { it.nativeCanvas.drawImageRect(img, Rect.makeWH(size.width, size.height)) }
        }
    }

    class Item(tex: String): Model() {
        private val imgTex by lazy { Models.getImageOf(tex) }

        override fun draw(scope: DrawScope) {
            val img = imgTex
            if (img != null) {
                val size = scope.size
                scope.drawIntoCanvas { it.nativeCanvas.drawImageRect(img, Rect.makeWH(size.width, size.height)) }
            }
        }
    }

    class Block(up: String, north: String, west: String): Model() {
        private val upTex by Models.lazyImageOf(up)
        private val northTex by Models.lazyImageOf(north)
        private val westTex by Models.lazyImageOf(west)

        private val matrix = Matrix().apply {
            rotateX(120f)
            rotateZ(45f)
            scale(0.625f, 0.625f, 0.625f)
        }

        override fun draw(scope: DrawScope) {
            val size = scope.size
            scope.withTransform({
                transform(matrix)
                translate(size.width * -0.5f, size.height * -1.625f)
            }) {
                val r = Rect.makeWH(size.width, size.height)
                val p = Paint()
                p.color = 0xFF000000.toInt()
                val north = northTex
                if (north != null) withTransform(::transformX) {
                    p.setAlphaf(0.2f)
                    drawIntoCanvas { it.nativeCanvas.apply {
                        drawImageRect(north, r)
                        drawRect(r, p)
                    } }
                }

                val up = upTex
                if (up != null) withTransform(::transformY) {
                    drawIntoCanvas { it.nativeCanvas.drawImageRect(up, r) }
                }

                val west = westTex
                if (west != null) withTransform(::transformZ) {
                    p.setAlphaf(0.4f)
                    drawIntoCanvas { it.nativeCanvas.apply {
                        drawImageRect(west, r)
                        drawRect(r, p)
                    } }
                }
            }
        }

        private fun transformX(dt: DrawTransform) {
            val m = Matrix()
            m.rotateY(-90f)
            m.rotateX(-90f)
            dt.transform(m)
        }
        @Suppress("UNUSED_PARAMETER")
        private fun transformY(dt: DrawTransform) {}
        private fun transformZ(dt: DrawTransform) {
            val m = Matrix()
            m.rotateX(-90f)
            dt.transform(m)
        }
    }
}
package io.github.justinrobertson.playing.memorygame

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Paint

val SYMBOLS = "ABCDEFGH".toCharArray()
val numTiles = SYMBOLS.size * 2

class Model {
    val tiles = ArrayList<Int>()
    var currentlySelected = -1
    var matchesFound = 0
    /*
     * Model the tiles as integers from 0 to N-1 each appearing twice in the arraylist.
     * Shuffle the array. When a pair is matched, set them both to -1.
     *
     */

    init {
        // Add each tile number twice
        for (i in 0 until numTiles) {
            tiles.add(i)
            tiles.add(i)
        }
        // println(tiles)

        // Shuffle the tiles
        tiles.shuffle()

        // println(tiles)
    }

    fun select(newlySelected: Int) {
        println("Tile selected")
        if (currentlySelected == -1) {
            // No tiles are currently selected, so this is the first tile flipped
            println("First tile flipped. Result: ${tiles[newlySelected]}")
            currentlySelected = newlySelected
        } else if (tiles[newlySelected] == -1) {
            // They've clicked on a tile that they've already found
            println("That tile has already been matched!")
        } else if (currentlySelected == newlySelected) {
            // They have selected the same tile twice.
            // The UI could prevent this but if it happens
            // do nothing.
        } else {
            println("Second tile flipped. Result: ${tiles[newlySelected]}")
            if (tiles[newlySelected] == tiles[currentlySelected]) {
                // match
                println("Match")
                matchesFound += 1
                tiles[newlySelected] = -1
                tiles[currentlySelected] = -1
            } else {
                // no match
                println("No match")
                currentlySelected = -1
            }
        }
    }

    fun gameOver(): Boolean {
        return matchesFound == SYMBOLS.size
    }
}

class View() : Application() {
    val model = Model()
    val canvasSideLength = 500.0
    val canvas = Canvas(canvasSideLength, canvasSideLength)
    val ctx = canvas.graphicsContext2D
    /*
     * Limit options to nxn grid where n is even. If border width b =
     * 1/n * tile side length then we need (b + tile side length) n times
     * then one extra border. This gives (b + n * b) * n + b
     * which is equal to ((n + 1) * b) * n + b which is
     * n(n + 1) * b + b which is n(n + 1) + 1 lots of b.
     * Therefore b = canvasSideLength / ((n(n + 1) + 1).
     */
    val n = 6 // where n^2 is the number of tiles and the border width = 1/n * tile width
    val b = canvasSideLength / (n * (n + 1) + 1) // b is the border width
    val t = b * n // t is the tile width

    override fun start(stage: Stage) {
        val root = StackPane()
        ctx.fill = Paint.valueOf("Black")
        ctx.fillRect(0.0, 0.0, canvas.height, canvas.width)
        stage.title = "Memory"

        // Add mouse listener to canvas??
        canvas.onMouseClicked = EventHandler { click(it) }

        // Add everything and paint
        root.getChildren().add(canvas)
        val scene = Scene(root, canvasSideLength, canvasSideLength)
        stage.setScene(scene)
        stage.show()
        paintCanvas(n) // where n^2 is the number of tiles
    }

    fun click(event: MouseEvent) {
        println(event.x)
    }

    // Canvas accommodates n * n tiles
    fun paintCanvas(n : Int) {
        // Draw the rectangles
        ctx.fill = Paint.valueOf("Blue")
        for (i in 0 until n) {
            for (j in 0 until n) {
                // Eventually here we will check what the model gave us but
                // until then...
                ctx.fillRect((i+1)*b + i*t, (j+1)*b + j*t, t, t)
            }
        }
    }

    fun commandLineGame() {
        while (!model.gameOver()) {
            println("Enter a selection from 1 to ${numTiles}:")
            val selection: String = readLine() ?: ""
            model.select(selection.toInt() - 1)
        }
        println("Game over")

    }
}

class Controller {

}


fun main(args: Array<String>) {
    Application.launch(View::class.java, *args)
}

// Collaborators - Abirami Saravanan, Harish Suresh
package com.example.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

/**
 * Main Activity class for a simple chain-style calculator.
 * - Performs left-to-right "iPhone style" calculations
 * - Handles division by zero gracefully
 * - Prevents multiple decimals in the same number
 */
class MainActivity : AppCompatActivity() {

    // The display TextView for showing input and results
    private lateinit var calcDisplay: TextView

    // Variables for storing the first value, the current operation, and whether a new number is starting
    private var firstValue: Double = 0.0
    private var operation: String? = null
    private var isNewOperation: Boolean = true  // When true, next digit replaces display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calcDisplay = findViewById(R.id.calcDisplay)

        // Number/dot buttons
        val btn0: Button = findViewById(R.id.btn0)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btn9: Button = findViewById(R.id.btn9)
        val btnDot: Button = findViewById(R.id.btnDot)

        // Operation buttons
        val btnClear: Button = findViewById(R.id.btnClear)
        val btnSign: Button = findViewById(R.id.btnSign)
        val btnPercent: Button = findViewById(R.id.btnPercent)
        val btnDivide: Button = findViewById(R.id.btnDivide)
        val btnMultiply: Button = findViewById(R.id.btnMultiply)
        val btnMinus: Button = findViewById(R.id.btnMinus)
        val btnPlus: Button = findViewById(R.id.btnPlus)
        val btnEquals: Button = findViewById(R.id.btnEquals)

        // Listener for digits and dot
        val numberClickListener = { view: android.view.View ->
            val button = view as Button
            numberClicked(button.text.toString())
        }
        // Assign to each numeric/dot button
        btn0.setOnClickListener(numberClickListener)
        btn1.setOnClickListener(numberClickListener)
        btn2.setOnClickListener(numberClickListener)
        btn3.setOnClickListener(numberClickListener)
        btn4.setOnClickListener(numberClickListener)
        btn5.setOnClickListener(numberClickListener)
        btn6.setOnClickListener(numberClickListener)
        btn7.setOnClickListener(numberClickListener)
        btn8.setOnClickListener(numberClickListener)
        btn9.setOnClickListener(numberClickListener)
        btnDot.setOnClickListener(numberClickListener)

        // Operation buttons
        btnPlus.setOnClickListener    { operationClicked("+") }
        btnMinus.setOnClickListener   { operationClicked("-") }
        btnMultiply.setOnClickListener{ operationClicked("*") }
        btnDivide.setOnClickListener  { operationClicked("/") }
        btnEquals.setOnClickListener  { equals() }

        // Function buttons
        btnClear.setOnClickListener   { clearAll() }
        btnSign.setOnClickListener    { toggleSign() }
        btnPercent.setOnClickListener { percent() }
    }

    /**
     * Called when any digit or dot is pressed.
     * - Prevents multiple dots in the same number.
     * - If we're starting fresh or display is "0", we clear the display first.
     */
    private fun numberClicked(digit: String) {
        // If the user tries to type '.' again in the same number, skip if display already has a dot
        val currentText = calcDisplay.text.toString()
        if (digit == "." && currentText.contains(".")) {
            return // Do nothing, ignore second dot
        }

        // If starting a new operation or display is "0", reset the display
        if (isNewOperation || currentText == "0" || currentText == "Error") {
            calcDisplay.text = ""
            isNewOperation = false
        }

        // Append the digit or dot
        calcDisplay.append(digit)
    }

    /**
     * When an operator (+, -, *, /) is pressed:
     * - If not a new operation, do partial calculation first (chain mode).
     * - Store the current operation, remember firstValue, and set isNewOperation true.
     */
    private fun operationClicked(op: String) {
        if (!isNewOperation) {
            equals() // Perform pending calculation
        }
        // If display was "Error", reset before storing any operation
        if (calcDisplay.text.toString() == "Error") {
            calcDisplay.text = "0"
        }

        operation = op
        firstValue = calcDisplay.text.toString().toDoubleOrNull() ?: 0.0
        isNewOperation = true
    }

    /**
     * Perform the pending operation with firstValue and current display.
     * Handles divide-by-zero by showing "Error".
     */
    private fun equals() {
        if (operation != null && calcDisplay.text.toString() != "Error") {
            val secondValue = calcDisplay.text.toString().toDoubleOrNull() ?: 0.0
            val result = when (operation) {
                "+" -> firstValue + secondValue
                "-" -> firstValue - secondValue
                "*" -> firstValue * secondValue
                "/" ->
                    if (secondValue == 0.0) {
                        calcDisplay.text = "Error"
                        0.0
                    } else {
                        firstValue / secondValue
                    }
                else -> secondValue
            }
            // Only update the display if not "Error"
            if (calcDisplay.text.toString() != "Error") {
                calcDisplay.text = result.toString()
                firstValue = result
            }
            isNewOperation = true
            operation = null
        }
    }

    /**
     * Clears the display and internal states to defaults.
     */
    private fun clearAll() {
        calcDisplay.text = "0"
        firstValue = 0.0
        operation = null
        isNewOperation = true
    }

    /**
     * Toggles the sign of the currently displayed number (e.g. 5 -> -5).
     */
    private fun toggleSign() {
        if (calcDisplay.text.toString() == "Error") {
            return // Can't toggle sign of "Error"
        }
        val currentValue = calcDisplay.text.toString().toDoubleOrNull() ?: 0.0
        calcDisplay.text = (currentValue * -1).toString()
    }

    /**
     * Converts the displayed number to a percentage by dividing by 100.
     */
    private fun percent() {
        if (calcDisplay.text.toString() == "Error") {
            return // Can't do percentage of "Error"
        }
        val currentValue = calcDisplay.text.toString().toDoubleOrNull() ?: 0.0
        calcDisplay.text = (currentValue / 100).toString()
    }
}

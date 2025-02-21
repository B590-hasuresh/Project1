package com.example.calculatorapp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.tan

/**
 * Main Activity class for a simple chain-style calculator.
 * - Performs left-to-right "iPhone style" calculations
 * - Handles division by zero gracefully
 * - Prevents multiple decimals in the same number
 * - Supports scientific functions in landscape mode
 */
class MainActivity : AppCompatActivity() {

    /** The display TextView for showing input and results */
    private lateinit var calcDisplay: TextView

    /** Stores the first value in a two-operand calculation */
    private var firstValue: Double = 0.0

    /** Stores the current operation (+, -, *, /) */
    private var operation: String? = null

    /** When true, next digit replaces display instead of appending */
    private var isNewOperation: Boolean = true

    /** Tag for logging */
    private val TAG = "CalculatorApp"

    /**
     * Initializes the activity, sets up view binding and click listeners.
     * Creates listeners for numeric, operation, and function buttons.
     * @param savedInstanceState Bundle containing the saved state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeScientificButtons()
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
     * Handles configuration changes, particularly rotation between portrait and landscape
     * @param newConfig The new device configuration
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // If orientation changes to landscape, initialize scientific buttons
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initializeScientificButtons()
        }
    }

    /**
     * Initializes the scientific calculator buttons that are only available in landscape mode.
     * Sets up click listeners for trigonometric (sin, cos, tan) and logarithmic (log10, ln) functions.
     * Each button is null-checked since they only exist in landscape layout.
     *
     * The function:
     * - Finds scientific operation buttons from layout
     * - Sets up click listeners with null safety checks
     * - Logs button press events for debugging
     *
     * Note: This is called both during onCreate and onConfigurationChanged (landscape)
     */
    private fun initializeScientificButtons() {
        Log.d(TAG, "scientific buttons initialized")
        // Find the scientific buttons which only exist in landscape mode
        val btnSin: Button? = findViewById(R.id.btnSin)
        val btnCos: Button? = findViewById(R.id.btnCos)
        val btnTan: Button? = findViewById(R.id.btnTan)
        val btnLog10: Button? = findViewById(R.id.btnLog10)
        val btnLn: Button? = findViewById(R.id.btnLn)

        // Set up click listeners if buttons exist (null check)
        btnSin?.setOnClickListener {
            Log.d(TAG, "Sin button pressed")
            scientificOperation("sin")
        }

        btnCos?.setOnClickListener {
            Log.d(TAG, "Cos button pressed")
            scientificOperation("cos")
        }

        btnTan?.setOnClickListener {
            Log.d(TAG, "Tan button pressed")
            scientificOperation("tan")
        }

        btnLog10?.setOnClickListener {
            Log.d(TAG, "Log10 button pressed")
            scientificOperation("log10")
        }

        btnLn?.setOnClickListener {
            Log.d(TAG, "Ln button pressed")
            scientificOperation("ln")
        }
    }

    /**
     * Performs scientific calculations on the current display value.
     * Handles domain errors by displaying "Error" for invalid inputs.
     *
     * @param op The operation to perform: "sin", "cos", "tan", "log10", or "ln"
     *
     * Notes:
     * - Trigonometric functions expect input in degrees and convert to radians
     * - Returns "Error" for undefined results (NaN) or infinity
     * - Sets isNewOperation to true after calculation
     * - Skips calculation if display already shows "Error"
     */
    private fun scientificOperation(op: String) {
        if (calcDisplay.text.toString() == "Error") {
            return // Can't perform operations on error state
        }

        val currentValue = calcDisplay.text.toString().toDoubleOrNull() ?: 0.0
        val result = when (op) {
            "sin" -> sin(Math.toRadians(currentValue))
            "cos" -> cos(Math.toRadians(currentValue))
            "tan" -> tan(Math.toRadians(currentValue))
            "log10" -> log10(currentValue)
            "ln" -> ln(currentValue)
            else -> currentValue
        }

        // Handle domain errors
        if (result.isNaN() || result.isInfinite()) {
            calcDisplay.text = "Error"
        } else {
            calcDisplay.text = result.toString()
        }

        isNewOperation = true
    }

    /**
     * Called when any digit or dot is pressed.
     * @param digit The digit or dot that was pressed
     *
     * Notes:
     * - Prevents multiple dots in the same number
     * - If we're starting fresh or display is "0", we clear the display first
     * - Logs each button press for debugging
     */
    private fun numberClicked(digit: String) {
        Log.d(TAG, "Button pressed: $digit")
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
     * Handles operation button clicks (+, -, *, /).
     * @param op The operation symbol that was clicked
     *
     * Notes:
     * - If not a new operation, performs pending calculation first (chain mode)
     * - Stores the operation and first value for next calculation
     * - Sets isNewOperation flag to prepare for next number input
     * - Logs operation for debugging
     */
    private fun operationClicked(op: String) {
        Log.d(TAG, "Operation pressed: $op")
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
     * Performs the pending operation with firstValue and current display.
     *
     * Notes:
     * - Handles divide-by-zero by showing "Error"
     * - Updates display with result if not error
     * - Resets operation after calculation
     * - Logs operation for debugging
     */
    private fun equals() {
        Log.d(TAG, "Equals pressed")
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
     * Clears the display and resets all calculator states to defaults.
     * Sets display to "0" and resets all operation variables.
     */
    private fun clearAll() {
        Log.d(TAG, "Clear pressed")
        calcDisplay.text = "0"
        firstValue = 0.0
        operation = null
        isNewOperation = true
    }

    /**
     * Toggles the sign of the currently displayed number.
     * Multiplies current display value by -1.
     * Skips operation if display shows "Error".
     */
    private fun toggleSign() {
        Log.d(TAG, "Sign toggle pressed")
        if (calcDisplay.text.toString() == "Error") {
            return // Can't toggle sign of "Error"
        }
        val currentValue = calcDisplay.text.toString().toDoubleOrNull() ?: 0.0
        calcDisplay.text = (currentValue * -1).toString()
    }

    /**
     * Converts the displayed number to a percentage by dividing by 100.
     * Skips operation if display shows "Error".
     */
    private fun percent() {
        Log.d(TAG, "Percent pressed")
        if (calcDisplay.text.toString() == "Error") {
            return // Can't do percentage of "Error"
        }
        val currentValue = calcDisplay.text.toString().toDoubleOrNull() ?: 0.0
        calcDisplay.text = (currentValue / 100).toString()
    }

    /**
     * Saves the calculator's state before configuration changes.
     * @param outState Bundle to store the state
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save calculator state before rotation
        outState.putString("DISPLAY_TEXT", calcDisplay.text.toString())
        outState.putDouble("FIRST_VALUE", firstValue)
        outState.putString("OPERATION", operation)
        outState.putBoolean("IS_NEW_OPERATION", isNewOperation)
    }

    /**
     * Restores the calculator's state after configuration changes.
     * @param savedInstanceState Bundle containing the saved state
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Restore state after rotation
        calcDisplay.text = savedInstanceState.getString("DISPLAY_TEXT", "0")
        firstValue = savedInstanceState.getDouble("FIRST_VALUE", 0.0)
        operation = savedInstanceState.getString("OPERATION", null)
        isNewOperation = savedInstanceState.getBoolean("IS_NEW_OPERATION", true)
    }
}
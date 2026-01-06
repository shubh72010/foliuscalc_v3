package com.folius.calc

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btnAdd: Button
    private lateinit var btnSub: Button
    private lateinit var btnMul: Button
    private lateinit var btnDiv: Button
    private lateinit var btnDot: Button
    private lateinit var btnClear: Button
    private lateinit var btnEqual: Button

    // State variables
    private var currentInput = StringBuilder()
    private var firstNumber: BigDecimal? = null
    private var currentOperation: String? = null
    private var isNewCalculation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        display = findViewById(R.id.display)
        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btnAdd = findViewById(R.id.btnAdd)
        btnSub = findViewById(R.id.btnSub)
        btnMul = findViewById(R.id.btnMul)
        btnDiv = findViewById(R.id.btnDiv)
        btnDot = findViewById(R.id.btnDot)
        btnClear = findViewById(R.id.btnClear)
        btnEqual = findViewById(R.id.btnEqual)
        
        updateDisplay()
    }

    private fun setupClickListeners() {
        // Number buttons
        val numberClickListener = { digit: String ->
            if (isNewCalculation) {
                currentInput.clear()
                isNewCalculation = false
            }
            // Prevent multiple decimals
            if (digit == "." && currentInput.contains(".")) {
                return@setOnClickListener
            }
            // Prevent starting with decimal without zero
            if (digit == "." && currentInput.isEmpty()) {
                currentInput.append("0")
            }
            currentInput.append(digit)
            updateDisplay()
        }

        btn0.setOnClickListener { numberClickListener("0") }
        btn1.setOnClickListener { numberClickListener("1") }
        btn2.setOnClickListener { numberClickListener("2") }
        btn3.setOnClickListener { numberClickListener("3") }
        btn4.setOnClickListener { numberClickListener("4") }
        btn5.setOnClickListener { numberClickListener("5") }
        btn6.setOnClickListener { numberClickListener("6") }
        btn7.setOnClickListener { numberClickListener("7") }
        btn8.setOnClickListener { numberClickListener("8") }
        btn9.setOnClickListener { numberClickListener("9") }
        btnDot.setOnClickListener { numberClickListener(".") }

        // Operation buttons
        btnAdd.setOnClickListener { setOperation("+") }
        btnSub.setOnClickListener { setOperation("-") }
        btnMul.setOnClickListener { setOperation("*") }
        btnDiv.setOnClickListener { setOperation("/") }

        // Clear button
        btnClear.setOnClickListener {
            clearAll()
        }

        // Equal button
        btnEqual.setOnClickListener {
            calculateResult()
        }
    }

    private fun setOperation(op: String) {
        if (currentInput.isEmpty()) {
            // If we have a previous result and input is empty, allow changing operation
            if (firstNumber != null) {
                currentOperation = op
            }
            return
        }

        if (firstNumber != null && currentOperation != null && !isNewCalculation) {
            calculateResult()
            currentOperation = op
            isNewCalculation = false // Keep result ready for next operation
        } else {
            firstNumber = try {
                BigDecimal(currentInput.toString())
            } catch (e: NumberFormatException) {
                showError("Invalid Number")
                return
            }
            currentOperation = op
            currentInput.clear()
            updateDisplay()
        }
    }

    private fun calculateResult() {
        if (firstNumber == null || currentOperation == null || currentInput.isEmpty()) {
            return
        }

        val secondNumber = try {
            BigDecimal(currentInput.toString())
        } catch (e: NumberFormatException) {
            showError("Invalid Number")
            return
        }

        if (currentOperation == "/" && secondNumber.compareTo(BigDecimal.ZERO) == 0) {
            showError("Error: Div by 0")
            clearAll()
            return
        }

        val result = try {
            when (currentOperation) {
                "+" -> firstNumber!!.add(secondNumber)
                "-" -> firstNumber!!.subtract(secondNumber)
                "*" -> firstNumber!!.multiply(secondNumber)
                "/" -> firstNumber!!.divide(secondNumber, 10, RoundingMode.HALF_EVEN)
                else -> BigDecimal.ZERO
            }
        } catch (e: Exception) {
            showError("Calculation Error")
            clearAll()
            return
        }

        // Format result: remove trailing zeros and decimal if needed
        val formattedResult = result.stripTrailingZeros().toPlainString()
        
        currentInput = StringBuilder(formattedResult)
        firstNumber = null
        currentOperation = null
        isNewCalculation = true
        
        updateDisplay()
    }

    private fun clearAll() {
        currentInput.clear()
        firstNumber = null
        currentOperation = null
        isNewCalculation = true
        updateDisplay()
    }

    private fun updateDisplay() {
        if (currentInput.isEmpty()) {
            if (firstNumber != null) {
                display.text = firstNumber!!.stripTrailingZeros().toPlainString()
            } else {
                display.text = "0"
            }
        } else {
            display.text = currentInput.toString()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        display.text = "0"
    }
}
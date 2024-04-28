package step.learning.android_spd_111;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Stack;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory;
    private TextView tvResult;
    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvHistory = findViewById(R.id.calc_tv_history);
        tvResult = findViewById(R.id.calc_tv_result);
        if(savedInstanceState == null) {
            tvResult.setText("0");
        }
        /* Задача: циклом перебрати ресурсні кнопки calc_btn_{i} і для кожної
        * з них поставити один обробник onDigitButtonClick */
        for(int i = 0; i < 10; i++) {
            findViewById( // На заміну R.id.calc_btn_0 приходить наступний вираз
                    getResources() // R
                            .getIdentifier(
                                    "calc_btn_" + i, // .calc_btn_0
                                    "id",                  // .id
                                    getPackageName()
                            )
            ).setOnClickListener( this::onDigitButtonClick );
        }
        findViewById(R.id.calc_btn_inverse).setOnClickListener(this::onInverseClick);
        findViewById(R.id.calc_btn_square).setOnClickListener(this::onSquareClick);
        findViewById(R.id.calc_btn_sqrt).setOnClickListener(this::onSqrtClick);
        findViewById(R.id.calc_btn_percent).setOnClickListener(this::onPercentClick);
        findViewById(R.id.calc_btn_plus_minus).setOnClickListener(this::onPlusMinusClick);

        findViewById(R.id.calc_btn_plus).setOnClickListener(this::onPlusClick);
        findViewById(R.id.calc_btn_minus).setOnClickListener(this::onMinusClick);
        findViewById(R.id.calc_btn_multiply).setOnClickListener(this::onMultiplyClick);
        findViewById(R.id.calc_btn_divide).setOnClickListener(this::onDivideClick);

        findViewById(R.id.calc_btn_comma).setOnClickListener(this::onCommaClick);
        findViewById(R.id.calc_btn_c).setOnClickListener(this::onCClick);
        findViewById(R.id.calc_btn_ce).setOnClickListener(this::onCEClick);
        findViewById(R.id.calc_btn_backspace).setOnClickListener(this::onBackspaceClick);

        findViewById(R.id.calc_btn_equals).setOnClickListener(this::onEqualsClick);

        findViewById(R.id.main).setOnTouchListener(new OnSwipeListener(this) {
            @Override
            public void onSwipeLeft() {
                Toast.makeText(CalcActivity.this,
                        "Swipe to the left = Backspace", Toast.LENGTH_SHORT).show();
                onBackspaceClick(findViewById(R.id.main));
            }
        });
    }
    private void onCClick(View view) {
        String result = tvResult.getText().toString();
        String history = tvHistory.getText().toString();
        if(!result.equals("0")) {
            result = "0";
        }
        if(!history.equals("")) {
            history = "";
        }
        tvResult.setText(result);
        tvHistory.setText(history);
    }
    private void onCEClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - 1);
        }
        if (result.isEmpty()) {
            result = "0";
        }
        tvResult.setText(result);
    }
    private void onBackspaceClick(View view) {
        onCEClick(view);
    }
    private void onCommaClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.endsWith("+") && !result.endsWith("*")
                && !result.endsWith("-") && !result.endsWith("/")
                && !result.endsWith(".")) {
            result += ".";
        }
        tvResult.setText(result);
    }
    private void onPlusClick(View view) {
        String result = tvResult.getText().toString();
        if(!result.equals("-") && !result.isEmpty()) {
            if (result.matches("^-?\\d.*")) {
                if (!result.endsWith("+") && !result.endsWith("*") && !result.endsWith("-") && !result.endsWith("/")) {
                    result += "+";
                }
                else if (result.endsWith("-") || result.endsWith("*") || result.endsWith("/")) {
                    result = result.substring(0, result.length() - 1) + "+";
                }
            }
        }
        tvResult.setText(result);
    }
    private void onMinusClick(View view) {
        String result = tvResult.getText().toString();
        if(result.equals("0")) {
            result = "-";
        }
        if (!result.endsWith("+") && !result.endsWith("*")
                && !result.endsWith("-") && !result.endsWith("/")) {
            result += "-";
        }
        if(result.endsWith("+") || result.endsWith("*") || result.endsWith("/")) {
            result = result.substring(0, result.length() - 1) + "-";
        }
        tvResult.setText(result);
    }
    private void onMultiplyClick(View view) {
        String result = tvResult.getText().toString();
        if(!result.equals("-") && !result.isEmpty()) {
            if (result.matches("^-?\\d.*")) {
                if (!result.endsWith("+") && !result.endsWith("*") && !result.endsWith("-") && !result.endsWith("/")) {
                    result += "*";
                }
                else if (result.endsWith("-") || result.endsWith("+") || result.endsWith("/")) {
                    result = result.substring(0, result.length() - 1) + "*";
                }
            }
        }
        tvResult.setText(result);
    }
    private void onDivideClick(View view) {
        String result = tvResult.getText().toString();
        if(!result.equals("-") && !result.isEmpty()) {
            if (result.matches("^-?\\d.*")) {
                if (!result.endsWith("+") && !result.endsWith("*") && !result.endsWith("-") && !result.endsWith("/")) {
                    result += "/";
                }
                else if (result.endsWith("-") || result.endsWith("*") || result.endsWith("+")) {
                    result = result.substring(0, result.length() - 1) + "/";
                }
            }
        }
        tvResult.setText(result);
    }
    private void onSqrtClick(View view) {
        String result = tvResult.getText().toString();
        double x = Double.parseDouble(result);
        if(x <= 0) {
            Toast.makeText(this, R.string.calc_negative_sqrt, Toast.LENGTH_SHORT).show();
            return;
        }
        x = Math.sqrt(x);
        String str = (x == (int)x) ? String.valueOf((int)x) : String.valueOf(x);
        if(str.length() > 13) {
            str = str.substring(0, 13);
        }
        tvResult.setText(str);
    }
    private void onSquareClick(View view) {
        String result = tvResult.getText().toString();
        double x = Double.parseDouble(result);
        x *= x;
        String str = (x == (int)x) ? String.valueOf((int)x) : String.valueOf(x);
        if(str.length() > 13) {
            str = str.substring(0, 13);
        }
        tvResult.setText(str);
    }
    private void onInverseClick(View view) {
        String result = tvResult.getText().toString();
        double x = Double.parseDouble(result);
        if(x == 0) {
            Toast.makeText(this, R.string.calc_zero_division, Toast.LENGTH_SHORT).show();
            return;
        }
        x = 1.0 / x;
        String str = (x == (int)x) ? String.valueOf((int)x) : String.valueOf(x);
        if(str.length() > 13) {
            str = str.substring(0, 13);
        }
        tvResult.setText(str);
    }
    private void onPercentClick(View view) {
        String result = tvResult.getText().toString();
        double x = Double.parseDouble(result);
        x /= 100;
        String str = (x == (int)x) ? String.valueOf((int)x) : String.valueOf(x);
        if(str.length() > 13) {
            str = str.substring(0, 13);
        }
        tvResult.setText(str);
    }
    private void onPlusMinusClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.isEmpty() && !result.equals("0")) {
            if (result.charAt(0) == '-') {
                result = result.substring(1);
            } else {
                result = "-" + result;
            }
        }
        tvResult.setText(result);
    }
    private void onEqualsClick(View view) {
        String result = tvResult.getText().toString();
        try {
            double x = evaluateExpression(result);
            String str = (x == (int)x) ? String.valueOf((int)x) : String.valueOf(x);
            if(str.length() > 13) {
                str = str.substring(0, 13);
            }
            tvHistory.setText(result);
            tvResult.setText(str);
        } catch (ArithmeticException e) {
            Toast.makeText(this, R.string.calc_wrong_expression, Toast.LENGTH_SHORT).show();
        }
    }
    private double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                operands.push(Double.parseDouble(sb.toString()));
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && precedence(c) <= precedence(operators.peek())) {
                    evaluate(operands, operators);
                }
                operators.push(c);
            }
        }
        while (!operators.isEmpty()) {
            evaluate(operands, operators);
        }

        return operands.pop();
    }
    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }
    private void evaluate(Stack<Double> operands, Stack<Character> operators) {
        double operand2 = operands.pop();
        double operand1 = operands.pop();
        char operator = operators.pop();

        double result = 0;
        switch (operator) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                if (operand2 != 0) {
                    result = operand1 / operand2;
                } else {
                    Toast.makeText(this, R.string.calc_zero_division, Toast.LENGTH_SHORT).show();
                }
                break;
        }

        operands.push(result);
    }
    /*
    При зміні конфігурації пристрою (поворотах, змінах налаштувань тощо) відбувається
    перезапуск активності. При цьому подаються події життєвого циклу
    onSaveInstanceState - при виході з активності перед перезапуском
    onRestoreInstanceState - при відновленні активності після перезапуску
    До обробників передається Bundle, що є сховищем, яке дозволяє зберегти та відновити дані.
    Також збережений Bundle передається до onCreate, що дозволяє визначити, чи це перший запуск,
    чи перезапуск через зміну конфігурації
     */

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState); // потрібно
        outState.putCharSequence("tvResult", tvResult.getText());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("tvResult"));
    }

    private void onDigitButtonClick(View view) {
        String result = tvResult.getText().toString();
        if(result.length() >= 10) {
            Toast.makeText(this, R.string.calc_limit_exceeded, Toast.LENGTH_SHORT).show();
            return;
        }
        if(result.equals("0")) {
            result = "";
        }
        result += ((Button) view).getText();
        tvResult.setText( result );
    }
}
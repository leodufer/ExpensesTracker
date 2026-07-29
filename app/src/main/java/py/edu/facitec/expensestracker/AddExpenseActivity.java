package py.edu.facitec.expensestracker;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Date;

public class AddExpenseActivity extends AppCompatActivity {

    ExpenseDao dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_expense);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dao = new ExpenseDao(this);

        EditText editTextAmount = findViewById(R.id.editTextAmount);
        editTextAmount.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().equals(current)) {
                    editTextAmount.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[.]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(new java.util.Locale("es", "PY"));
                            symbols.setGroupingSeparator('.');
                            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0", symbols);
                            String formatted = formatter.format(parsed);

                            current = formatted;
                            editTextAmount.setText(formatted);
                            editTextAmount.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            // Ignored
                        }
                    } else {
                        current = "";
                        editTextAmount.setText("");
                    }

                    editTextAmount.addTextChangedListener(this);
                }
            }
        });
    }

    public void saveExpense(View view) {
        EditText editTextAmount = findViewById(R.id.editTextAmount);
        EditText editTextDescripcion = findViewById(R.id.editTextDescripcion);
        RadioGroup radioGroupType = findViewById(R.id.radioGroupType);
        DatePicker datePickerSelector = findViewById(R.id.datePickerSelector);

        String amountStr = editTextAmount.getText().toString().trim().replaceAll("[.]", "");
        String description = editTextDescripcion.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese una descripción", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isIncome = radioGroupType.getCheckedRadioButtonId() == R.id.radioButtonIncome;

        Calendar calendar = Calendar.getInstance();
        calendar.set(datePickerSelector.getYear(), datePickerSelector.getMonth(), datePickerSelector.getDayOfMonth());
        Date date = calendar.getTime();

        Expense expense = new Expense(description, amount, date, isIncome);
        Expense saved = dao.save(expense);

        if (saved != null) {
            Toast.makeText(this, "Transacción guardada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar la transacción", Toast.LENGTH_SHORT).show();
        }
    }
}
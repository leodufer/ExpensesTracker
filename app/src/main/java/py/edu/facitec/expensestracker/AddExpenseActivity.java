package py.edu.facitec.expensestracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Date;

import py.edu.facitec.expensestracker.databinding.ActivityAddExpenseBinding;

public class AddExpenseActivity extends AppCompatActivity {

    ActivityAddExpenseBinding binding;
    ExpenseDao dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dao = new ExpenseDao(this);
    }

    public void saveExpense(View view) {
        //TODO save expense
        String amount = binding.editTextAmount.toString();
        if(amount.isEmpty()){
            binding.editTextAmount.setError(getString(R.string.complete_the_amount));
            return;
        }
        String description = binding.editTextDescripcion.toString();
        if(description.isEmpty()){
            binding.editTextDescripcion.setError(getString(R.string.complete_the_description));
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(binding.datePickerSelector.getYear(),
                        binding.datePickerSelector.getMonth(),
                        binding.datePickerSelector.getDayOfMonth());

        Expense expense = new Expense(description,Double.parseDouble(amount),calendar.getTime());
        dao.save(expense);
    }
}
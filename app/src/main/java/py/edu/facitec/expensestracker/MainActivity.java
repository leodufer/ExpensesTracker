package py.edu.facitec.expensestracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.Locale;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ExpenseDao dao;
    private ListView listViewExpense;
    private TextView textViewAmount;
    private TextView textViewTotalIncome;
    private TextView textViewTotalExpense;
    // Container for month filter buttons
    private LinearLayout monthFilterContainer;
    // Currently selected month-year filter (e.g., "07-2026"). Null means all months.
    private String selectedMonthYear = null;
    // Cache all expenses loaded from DB
    private List<Expense> allExpenses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dao = new ExpenseDao(this);
        listViewExpense = findViewById(R.id.listViewExpense);
        textViewAmount = findViewById(R.id.textViewAmount);
        textViewTotalIncome = findViewById(R.id.textViewTotalIncome);
        textViewTotalExpense = findViewById(R.id.textViewTotalExpense);
        monthFilterContainer = findViewById(R.id.monthFilterContainer);
        // Initialize month filter UI
        populateMonthFilterContainer();

        listViewExpense.setOnItemLongClickListener((parent, view, position, id) -> {
            ExpenseAdapter adapter = (ExpenseAdapter) listViewExpense.getAdapter();
            if (adapter != null) {
                Expense expense = adapter.getItem(position);
                if (expense != null) {
                    new AlertDialog.Builder(this)
                            .setTitle("Eliminar transacción")
                            .setMessage("¿Está seguro de que desea eliminar esta transacción?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                dao.delete(expense.getId());
                                refreshData();
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        // Load all expenses from DB and cache them
        List<Expense> expenses = dao.queryAll();
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        allExpenses.clear();
        allExpenses.addAll(expenses);

        // Populate month filter UI based on loaded expenses
        populateMonthFilterContainer();

        // Apply current month filter (if any)
        List<Expense> filtered = filterExpensesByMonth(allExpenses, selectedMonthYear);

        double totalIncome = 0;
        double totalExpense = 0;
        for (Expense expense : filtered) {
            if (expense.isIncome()) {
                totalIncome += expense.getAmount();
            } else {
                totalExpense += expense.getAmount();
            }
        }
        double balance = totalIncome - totalExpense;

        textViewAmount.setText("G$. " + formatAmount(balance));
        textViewTotalIncome.setText("G$. " + formatAmount(totalIncome));
        textViewTotalExpense.setText("G$. " + formatAmount(totalExpense));

        ExpenseAdapter adapter = new ExpenseAdapter(this, filtered);
        listViewExpense.setAdapter(adapter);
    }

    private String formatAmount(double value) {
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(new java.util.Locale("es", "PY"));
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0", symbols);
        return formatter.format(value);
    }

    public void openAddExpense(View view) {
        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
        startActivity(intent);
    }

    // ------------------- Month Filter Helpers -------------------

    private void populateMonthFilterContainer() {
        if (monthFilterContainer == null) return;
        monthFilterContainer.removeAllViews();
        // Create "Todos" button
        Button allBtn = createMonthButton("Todos", "ALL");
        monthFilterContainer.addView(allBtn);

        // Extract unique month-year strings from allExpenses
        Set<String> monthSet = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es-PY"));
        for (Expense e : allExpenses) {
            String monthYear = sdf.format(e.getCreateAt());
            monthSet.add(monthYear);
        }
        // Sort months descending (most recent first)
        List<String> monthList = new ArrayList<>(monthSet);
        Collections.sort(monthList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    Date d1 = sdf.parse(o1);
                    Date d2 = sdf.parse(o2);
                    return d2.compareTo(d1);
                } catch (Exception ex) {
                    return 0;
                }
            }
        });
        // Add buttons for each month
        for (String month : monthList) {
            Button btn = createMonthButton(month, month);
            monthFilterContainer.addView(btn);
        }
        // Ensure selected state reflects current filter
        updateMonthButtonSelection();
    }

    private Button createMonthButton(String displayText, final String monthKey) {
        Button btn = new Button(this);
        btn.setText(displayText);
        btn.setAllCaps(false);
        btn.setBackgroundResource(R.drawable.bg_selector);
        btn.setTextColor(getResources().getColorStateList(R.color.black));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        btn.setLayoutParams(params);
        btn.setOnClickListener(v -> {
            selectedMonthYear = monthKey.equals("ALL") ? null : monthKey;
            updateFilteredData();
        });
        return btn;
    }

    private void updateMonthButtonSelection() {
        if (monthFilterContainer == null) return;
        for (int i = 0; i < monthFilterContainer.getChildCount(); i++) {
            View child = monthFilterContainer.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                String key = btn.getText().toString();
                boolean selected = (selectedMonthYear == null && key.equals("Todos")) || (selectedMonthYear != null && key.equals(selectedMonthYear));
                btn.setAlpha(selected ? 1.0f : 0.6f);
            }
        }
    }

    private List<Expense> filterExpensesByMonth(List<Expense> source, String monthYear) {
        if (monthYear == null) return source;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es-PY"));
        List<Expense> result = new ArrayList<>();
        for (Expense e : source) {
            String m = sdf.format(e.getCreateAt());
            if (m.equals(monthYear)) {
                result.add(e);
            }
        }
        return result;
    }

    private void updateFilteredData() {
        List<Expense> filtered = filterExpensesByMonth(allExpenses, selectedMonthYear);
        // Recalculate totals
        double totalIncome = 0;
        double totalExpense = 0;
        for (Expense e : filtered) {
            if (e.isIncome()) totalIncome += e.getAmount();
            else totalExpense += e.getAmount();
        }
        double balance = totalIncome - totalExpense;
        textViewAmount.setText("G$. " + formatAmount(balance));
        textViewTotalIncome.setText("G$. " + formatAmount(totalIncome));
        textViewTotalExpense.setText("G$. " + formatAmount(totalExpense));
        // Update list adapter
        ExpenseAdapter adapter = new ExpenseAdapter(this, filtered);
        listViewExpense.setAdapter(adapter);
        updateMonthButtonSelection();
    }

}
package py.edu.facitec.expensestracker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    public ExpenseAdapter(@NonNull Context context, List<Expense> expenses) {
        super(context, R.layout.item_expense, R.id.textViewDescription, expenses);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Expense expense = getItem(position);

        convertView = super.getView(position, convertView, parent);

        TextView textViewDescription = convertView.findViewById(R.id.textViewDescription);
        TextView textViewMonto = convertView.findViewById(R.id.textViewAmount);
        TextView fechaTextView = convertView.findViewById(R.id.textViewDate);
        ImageView imgType = convertView.findViewById(R.id.imgType);

        textViewDescription.setText(Objects.requireNonNull(expense).getDescription());
        fechaTextView.setText(expense.getDateDDMMYYYY());

        if (expense.isIncome()) {
            imgType.setImageResource(R.drawable.ic_up);
            textViewMonto.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
            textViewMonto.setText("+ G$. " + formatAmount(expense.getAmount()));
        } else {
            imgType.setImageResource(R.drawable.ic_down);
            textViewMonto.setTextColor(android.graphics.Color.parseColor("#C62828"));
            textViewMonto.setText("- G$. " + formatAmount(expense.getAmount()));
        }

        return convertView;
    }

    private String formatAmount(double value) {
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(new java.util.Locale("es", "PY"));
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0", symbols);
        return formatter.format(value);
    }
}

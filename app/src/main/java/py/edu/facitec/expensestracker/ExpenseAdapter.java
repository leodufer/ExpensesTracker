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

import py.edu.facitec.expensestracker.Expense;

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

        textViewDescription.setText(expense.getDescription());
        textViewMonto.setText(expense.getAmount()+"");
        fechaTextView.setText(expense.getDateDDMMYYYY());

        return convertView;
    }
}

package py.edu.facitec.expensestracker;

import android.content.Context;

public class ExpenseDao extends DBA<Expense>{
    public ExpenseDao(Context context){
        init(context, Expense.class);
    }
}

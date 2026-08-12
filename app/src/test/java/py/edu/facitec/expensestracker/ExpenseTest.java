package py.edu.facitec.expensestracker;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

public class ExpenseTest {

    @Test
    public void testExpenseConstructorAndGetters() {
        Date date = new Date();
        Expense expense = new Expense("Test Expense", 1000.0, date, false);

        assertEquals("Test Expense", expense.getDescription());
        assertEquals(1000.0, expense.getAmount(), 0.0);
        assertEquals(date, expense.getCreateAt());
        assertFalse(expense.isIncome());
    }

    @Test
    public void testSetters() {
        Expense expense = new Expense();
        Date date = new Date();
        
        expense.setDescription("Updated");
        expense.setAmount(500.0);
        expense.setCreateAt(date);
        expense.setIncome(true);

        assertEquals("Updated", expense.getDescription());
        assertEquals(500.0, expense.getAmount(), 0.0);
        assertEquals(date, expense.getCreateAt());
        assertTrue(expense.isIncome());
    }

    @Test
    public void testGetDateDDMMYYYY() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.AUGUST, 12);
        Date date = calendar.getTime();
        
        Expense expense = new Expense();
        expense.setCreateAt(date);

        assertEquals("12/08/2026", expense.getDateDDMMYYYY());
    }
}

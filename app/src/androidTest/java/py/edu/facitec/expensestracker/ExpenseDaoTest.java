package py.edu.facitec.expensestracker;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExpenseDaoTest {

    private ExpenseDao dao;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dao = new ExpenseDao(context);
        // Clear table for clean state
        try {
            com.j256.ormlite.table.TableUtils.clearTable(dao.getDao().getConnectionSource(), Expense.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveAndSelect() {
        Expense expense = new Expense("Pizza", 50000.0, new Date(), false);
        Expense saved = dao.save(expense);
        
        assertNotNull(saved);
        assertTrue(saved.getId() > 0);

        Expense retrieved = dao.select(saved.getId());
        assertNotNull(retrieved);
        assertEquals("Pizza", retrieved.getDescription());
        assertEquals(50000.0, retrieved.getAmount(), 0.0);
        assertFalse(retrieved.isIncome());
    }

    @Test
    public void testQueryAll() {
        dao.save(new Expense("Salary", 2000000.0, new Date(), true));
        dao.save(new Expense("Lunch", 25000.0, new Date(), false));

        List<Expense> expenses = dao.queryAll();
        assertEquals(2, expenses.size());
    }

    @Test
    public void testUpdate() {
        Expense expense = new Expense("Old Name", 10.0, new Date(), false);
        dao.save(expense);

        expense.setDescription("New Name");
        expense.setAmount(20.0);
        dao.update(expense);

        Expense retrieved = dao.select(expense.getId());
        assertEquals("New Name", retrieved.getDescription());
        assertEquals(20.0, retrieved.getAmount(), 0.0);
    }

    @Test
    public void testDelete() {
        Expense expense = new Expense("To be deleted", 100.0, new Date(), false);
        dao.save(expense);
        int id = expense.getId();

        dao.delete(id);
        Expense retrieved = dao.select(id);
        assertNull(retrieved);
    }
}

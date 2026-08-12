package py.edu.facitec.expensestracker;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntegrationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Clear database before each test
        ExpenseDao dao = new ExpenseDao(InstrumentationRegistry.getInstrumentation().getTargetContext());
        try {
            com.j256.ormlite.table.TableUtils.clearTable(dao.getDao().getConnectionSource(), Expense.class);
        } catch (SQLException e) {
            Log.e("ERROR_DAO", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    @Test
    public void testAddExpenseAndVerifyList() {
        // 1. Click FAB to add expense
        onView(withId(R.id.fabAddExpense)).perform(click());

        // 2. Fill the form in AddExpenseActivity
        onView(withId(R.id.editTextAmount)).perform(typeText("15000"));
        onView(withId(R.id.editTextDescripcion)).perform(typeText("Cena"));
        onView(withId(R.id.radioButtonExpense)).perform(click());
        onView(withId(R.id.buttonSave)).perform(click());

        // 3. Verify return to MainActivity and list contains the item
        onView(withId(R.id.listViewExpense)).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.listViewExpense))
                .atPosition(0)
                .onChildView(withId(R.id.textViewDescription))
                .check(matches(withText("Cena")));

        // 4. Verify totals
        onView(withId(R.id.textViewTotalExpense)).check(matches(withText(containsString("15.000"))));
        onView(withId(R.id.textViewAmount)).check(matches(withText(containsString("-15.000"))));
    }

    @Test
    public void testAddIncomeAndVerifyTotals() {
        // 1. Add Income
        onView(withId(R.id.fabAddExpense)).perform(click());
        onView(withId(R.id.editTextAmount)).perform(typeText("100000"));
        onView(withId(R.id.editTextDescripcion)).perform(typeText("Sueldo"));
        onView(withId(R.id.radioButtonIncome)).perform(click());
        onView(withId(R.id.buttonSave)).perform(click());

        // 2. Verify totals in MainActivity
        onView(withId(R.id.textViewTotalIncome)).check(matches(withText(containsString("100.000"))));
        onView(withId(R.id.textViewAmount)).check(matches(withText(containsString("100.000"))));
    }
}

package py.edu.facitec.expensestracker;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;

@DatabaseTable
public class Expense {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String description;

    @DatabaseField
    private double amount;

    @DatabaseField
    private Date createAt;

    @DatabaseField
    private boolean isIncome;

    public Expense(String description, double amount, Date createAt, boolean isIncome) {
        this.description = description;
        this.amount = amount;
        this.createAt = createAt;
        this.isIncome = isIncome;
    }

    public Expense() {
        this.description = "";
        this.amount = 0;
        this.createAt = new Date();
        this.isIncome = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }

    public String getDateDDMMYYYY(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(this.createAt);
    }
}

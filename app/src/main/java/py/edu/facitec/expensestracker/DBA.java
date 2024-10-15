package py.edu.facitec.expensestracker;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.oss.datahelper.DataBaseManager;

public abstract class DBA<T> {
	private static final int DB_VERSION = 1;
	private Dao<T, Integer> dao;
	
	public void init(Context context, Class clazz){


		String dbname = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString()+".sqlite";
                Log.i("INFO NAME",dbname);

        DataBaseManager.init(context, dbname, DB_VERSION);
		ConnectionSource source = DataBaseManager.getInstance()
												 .getHelper()
												 .getConnectionSource();
		try {
			TableUtils.createTableIfNotExists(source, clazz);
			this.dao = DataBaseManager.getInstance()
                                 .getHelper()
                                 .getDao(clazz);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    public  Dao<T, Integer> getDao(){
            return dao;
    }

    public T save(T entity){
        try {
            this.getDao().create(entity);
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<T> queryAll(){
        try {
            return this.getDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void delete(int id){
        try {
            this.getDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public T select(int id){
        try {
            return this.getDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(T t){
        try {
            this.getDao().update(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
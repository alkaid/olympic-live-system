package com.coodroid.olympic.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.coodroid.olympic.R;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 数据库帮助类，数据库相关的通用类
 * @author cater
 *
 */
public class DBHelper extends SQLiteOpenHelper{
	private static DBHelper mInstance =null;
	private SQLiteDatabase db ;
	private Context context;
		
	/**
	 * DBHelper的构造器
	 *如果数据库没有这个数据库，会创建数据库，如果有就会打开DB的操作类
	 * @param DBName 需要操作的数据库名称
	 */
	private DBHelper(Context context,String DBName) {
		super(context, DBName, null, Constants.Version);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		execCreateTableSQLScript(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	 /**单例模式**/  
    public static synchronized DBHelper getInstance(Context context,String DBName) {  
    if (mInstance == null) {  
        mInstance = new DBHelper(context, DBName);  
    }  
    return mInstance;  
    }  
	
	/**
	 * 用于打开数据库,返回数据库对象的方法
	 * 需要操作增删改查必须先调用这个方法
	 */
	public void open(){
		db = this.getWritableDatabase();
	}
	
	/**
	 * 用于执行创建数据库表脚本
	 */
	public void execCreateTableSQLScript(SQLiteDatabase db){
		InputStream is = context.getResources().openRawResource(R.raw.olympic);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = null;
			StringBuffer sql = new StringBuffer();
			while((str = br.readLine())!=null){
				if(str.substring(str.length()-1).equals(";")){
					sql.append(str.replaceAll(";", ""));
					db.execSQL(sql.toString());
					sql.delete(0, sql.length());
					continue;
				}
				sql.append(str+"\n");
				
			}
			LogUtil.i(sql.toString());			
		} catch (SQLException e) {
			LogUtil.e("脚本执行出错");
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e("IO操作出错");
			LogUtil.e(e);
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				LogUtil.e("输入的流不存在，为空");
				LogUtil.e(e);
			}
		}
		
	}
	
	/**
	 * 数据库建表的方法，并提供返回SQLiteDatabase,进行数据库后续操作
	 * create table(_id INTEGER PRIMARY KEY AUTOINCREMENT,time TEXT);
	 * @param table 创建的表的名称
	 * @param columns 是每列的集合,String的格式_id INTEGER PRIMARY KEY AUTOINCREMENT
	 * @return SQLiteDatabase对象，用于做关闭操作
	 */
	public  void createTable(String table,List<String> columns) {
			try {
				StringBuffer str = new StringBuffer();
				for(String column:columns){
					str.append(column+",");
				}
				str.replace(str.length()-1, str.length(), ";");
				String sql = "CREATE TABLE "+table+" ("+ str + ")";
				db.execSQL(sql);
			} catch (SQLException e) {
				LogUtil.e(e);
				LogUtil.e("建表的sql语句有错误.");
			}
	
	} 
	
	/**
	 * 删除数据库的表名为table的表
	 * @param table 删除的表的名称
	 */
	public  void dropTable(String table){		
			try {
				String sql = "DROP TABLE "+table;
				db.execSQL(sql);
			} catch (SQLException e) {
				LogUtil.e(e);
				LogUtil.e("删除数据库的sql有错误");
			}
	}
	
	/**
	 * 往数据库插入单行数据 insert into table(columnName1,columnName2) values(value1,value2)
	 * @param table
	 * @param values 包含着列名与值得键值对
	 * @return 返回的插入的行的id如果为-1表示插入错误
	 */
	public  long insert(String table,ContentValues values){		
		return db.insert(table, null, values);
	}
	
	/**
	 * 删除匹配条件的数据
	 * @param table 需要操作删除操作的表名
	 * @param matchs where子句匹配的String，格式为 _id = 1
	 */
	public  void delete(String table,List<String> matchs){
		//拼接where后面的
		try {
			StringBuffer str = new StringBuffer();
			for(String match:matchs){
				str.append(match + ",");
			}
			str.replace(str.length()-1, str.length(), ";");
			String sql = "DELETE FROM TABLE "+ table+" WHERE("+ str + ")";
			db.execSQL(sql);
		} catch (SQLException e) {
			LogUtil.e(e);
			LogUtil.e("删除的sql语句有错误");
		}
		 
	}
	
	
	/**
	 * 更新某个表匹配的数据 
	 * @param table 需要做更新操作的表名
	 * @param updateValues 所要更新的列的值 格式id=1
	 * @param matchValues 匹配的列的值 格式 id=1
	 */
	public  void update(String table,List<String> updates,List<String> matchs){
		try {
			StringBuffer updateStr = new StringBuffer();
			StringBuffer matchStr = new StringBuffer();
			//拼接需要update的值，即set后，where之前的值
			for(String update:updates){
				updateStr.append(update+",");
			}
			//拼接匹配的条件，where之后的值
			for(String match:matchs){
				matchStr.append(match+",");
			}
			updateStr.deleteCharAt(updateStr.length()-1);
			matchStr.replace(matchStr.length()-1, matchStr.length(), ";");
			String sql = "UPDATE "+table+" set "+updateStr+" WHERE "+matchStr;
LogUtil.i(sql);
			db.execSQL(sql);
		} catch (SQLException e) {
			LogUtil.e(e);
			LogUtil.e("更新sql语句出错");
		}
	}
	
	/**
	 * 根据条件查询的数据 如果columns和matchs都为null返回所有记录
	 * @param table 执行查询操作的表名
	 * @param columns 需要查找的列的名字 如果为null表示查询所有列
	 * @param matchs where子句后的匹配条件 每个matchs的格式是 ID=1 如果为null无where子句
	 * @return 如果无记录返回的是null
	 */
	public  Cursor query(String table,List<String> columns,List<String> matchs){
		if(table!=null){
			if(columns!=null&&matchs!=null){
				StringBuffer columnStr = new StringBuffer();
				StringBuffer matchStr = new StringBuffer();
				//拼接需要查询的列
				for(String update:columns){
					columnStr.append(update+",");
				}
				//拼接where子句的匹配的条件
				for(String match:matchs){
					matchStr.append(match+",");
				}
				matchStr.replace(matchStr.length()-1, matchStr.length(), ";");
				columnStr.substring(columnStr.length()-1);
				String sql = "SELECT "+columnStr+" FROM "+table+" WHERE "+matchStr;
				return processCurosr(db.rawQuery(sql, null));
			}else if(columns==null&&matchs!=null){
				StringBuffer matchStr = new StringBuffer();
				for(String match:matchs){
					matchStr.append(match+",");
				}
				matchStr.replace(matchStr.length()-1, matchStr.length(), ";");
				String sql = "SELECT *"+" FROM "+table+" WHERE "+matchStr;
				return processCurosr(db.rawQuery(sql, null));
			}else if(columns!=null&&matchs==null){
				StringBuffer columnStr = new StringBuffer();
				//拼接需要查询的列
				for(String update:columns){
					columnStr.append(update+",");
				}
				columnStr.substring(columnStr.length()-1);
				String sql = "SELECT "+columnStr+" FROM "+table;
				return processCurosr(db.rawQuery(sql, null));
			}else{
				return processCurosr(db.rawQuery("SELECT * FROM "+table, null));
			}
		}else{
			return null;
		}
	}
	
//	/**
//	 * 查询某个表所有的数据,如果columns为null,查询所有列
//	 * @param table 做查询操作的表的名称
//	 * @param columns查询的列的名名称
//	 * @return 返回Cursor可用于每一行的检索,如果为null表示输入的table数据库不存在
//	 */
//	public  Cursor queryAll(String table,List<String> columns){
//		if(columns!=null){
//			String[] sa = (String[]) columns.toArray();
//			return db.query(table, sa, null, null, null, null, null);
//		}else{
//			return db.rawQuery("SELECT * FROM"+table, null);
//		}
//		
//	}
	
	/**
	 * 通过sql语句查询语句
	 * @param sql SQL语句的String形式
	 * @return
	 */
	public Cursor query(String sql){
		return db.rawQuery(sql, null);
	}
	
	/**
	 * 用于DB做分页操作的。查询出的结果是所有字段的
	 * @param table从第几条数据开始查询
	 * @param firstResult跳过几行
	 * @param maxResult取几行
	 * @return
	 */
	public Cursor queryPaging(String table,List<String> columns,int firstResult,int maxResult){
		String sql = null;
		if(columns!=null){
			StringBuffer columnStr = new StringBuffer();
			//拼接需要查询的列
			for(String update:columns){
				columnStr.append(update+",");
			}
			columnStr.deleteCharAt(columnStr.length()-1);
			 sql = "SELECT "+columnStr.toString()+" FROM "+table+" LIMIT "+maxResult+" OFFSET "+firstResult;
		}else{
			 sql = "SELECT * FROM "+table+" LIMIT "+maxResult+" OFFSET "+firstResult;
		}
		return processCurosr(db.rawQuery(sql, null));
	}
	
	/**
	 * 用于判断查询出的Cursor是否为null
	 * @param cursor
	 * @return
	 */
	private Cursor processCurosr(Cursor cursor){
		if(cursor!=null){
			if(cursor.moveToFirst()){
				return cursor;
			}else{
				cursor.close();
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 用于对某些sql出现转义字符的进行处理
	 * @param str需要转义的字符
	 * @return
	 */
	public String formatSQL(String str){
		return str.replaceAll("'","''");
	}
	
	/**
	 * 用于关闭SqliteDatabase的操作
	 */
	public void close(){
		if(db!=null){
			db.close();
		}
	}
	

}
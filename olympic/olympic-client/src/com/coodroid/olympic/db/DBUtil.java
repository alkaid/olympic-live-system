package com.coodroid.olympic.db;

import java.util.List;

import com.coodroid.olympic.common.LogUtil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * 数据库帮助类，数据库相关的通用类
 * @author cater
 *
 */
public class DBUtil{
	
	private SQLiteDatabase db ;
	
	/**
	 * DBUtil的构造器
	 *如果数据库没有这个数据库，会创建数据库，如果有就会打开DB的操作类
	 * @param DBName 需要操作的数据库名称
	 */
	public DBUtil(String DBName) {
			db = SQLiteDatabase.openOrCreateDatabase(DBName,null);
			if(db==null){
				LogUtil.e("数据库名为空或者不存相应名称的数据库");
			}
	}
	
	
	
	/**
	 * 数据库建表的方法，并提供返回SQLiteDatabase,进行数据库后续操作
	 * create table(_id INTEGER PRIMARY KEY AUTOINCREMENT,time TEXT);
	 * @param db
	 * @param table
	 * @param columns 是一个String的List,String的格式_id INTEGER PRIMARY KEY AUTOINCREMENT
	 * @return
	 */
	public  SQLiteDatabase createTable(String table,List<String> columns){
		StringBuffer str = new StringBuffer();
		for(String column:columns){
			str.append(column+",");
		}
		str.replace(str.length()-1, str.length(), ";");
		String sql = "CREATE TABLE "+table+" ("+ str + ")";
		db.execSQL(sql);
		return db;
		
	} 
	
	/**
	 * 删除数据库的表名为table的表
	 * @param db
	 * @param table
	 */
	public  void dropTable(String table){
			String sql = "DROP TABLE "+table;
			db.execSQL(sql);
	}
	
	/**
	 * 往数据库插入单行数据 insert into table(columnName1,columnName2) values(value1,value2)
	 * @param db
	 * @param table
	 * @param values 包含着列名与值得键值对
	 */
	public  void insert(String table,ContentValues values){
		db.insert(table, null, values);
	}
	
	/**
	 * 删除匹配条件的数据，参数名称例: delete from 'table' where 'columnName1' = 'columnValue1' 
	 * 											and 'columnName2' = 'columnVaule2' 
	 * @param db
	 * @param table
	 * @param matchs 每个match为 _id = 1这样的结构
	 */
	public  void delete(String table,List<String> matchs){
		//拼接where后面的
		StringBuffer str = new StringBuffer();
		for(String match:matchs){
			str.append(match + ",");
		}
		str.replace(str.length()-1, str.length(), ";");
		String sql = "DELETE FROM TABLE "+ table+" WHERE("+ str + ")";
		db.execSQL(sql);
		 
	}
	
	
	/**
	 * 更新某个表匹配的数据 update 'table' set 'columnName1' = 'columnValue1' ,
	 * 								'columnName2' = 'columnValue2' where 'columnName3' = 'columnValue3'..  
	 * @param db
	 * @param table
	 * @param updateValues 所要更新的列的值 格式id=1
	 * @param matchValues 匹配的列的值 格式 id=1
	 */
	public  void update(String table,List<String> updates,List<String> matchs){
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
		updateStr.substring(updateStr.length()-1);
		matchStr.replace(matchStr.length()-1, matchStr.length(), ";");
		String sql = "UPDATE TABLE "+table+" set "+updateStr+" WHERE("+matchStr+ ")";
		db.execSQL(sql);
	}
	
	/**
	 * 根据条件查询的数据,如果columns为null表示查询所有列,matchs为null表示无匹配条件
	 * @param db
	 * @param table
	 * @param matchs
	 */
	public  Cursor query(String table,List<String> columns,List<String> matchs){
		StringBuffer columnStr = new StringBuffer();
		StringBuffer matchStr = new StringBuffer();
		//拼接需要查询的列
		for(String update:columns){
			columnStr.append(update+",");
		}
		for(String match:matchs){
			matchStr.append(match+",");
		}
		columnStr.substring(columnStr.length()-1);
		matchStr.replace(matchStr.length()-1, matchStr.length(), ";");
		String sql = "SELECT "+columnStr+" FROM "+table+" WHERE "+matchStr;
		db.rawQuery(sql, null);
		return null;
		
	}
	
	/**
	 * 查询某个表所有的数据,如果columns为null,查询所有列
	 * @param db
	 * @param table
	 * @param columns查询的列的名名称
	 * @return
	 */
	public  Cursor queryAll(String table,List<String> columns){
		if(columns!=null){
			String[] sa = (String[]) columns.toArray();
			return db.query(table, sa, null, null, null, null, null);
		}else{
			return db.rawQuery("SELECT * FROM"+table, null);
		}
		
	}
	
}
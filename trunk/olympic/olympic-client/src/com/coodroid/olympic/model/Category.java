package com.coodroid.olympic.model;

import android.widget.Adapter;

public class Category extends Model{

	private static final long serialVersionUID = -5912040328311704607L;
	/** 分类标题名称 */
	private String mTitle;
	/** 分类列表所对应排列adapter*/
	
    private Adapter mAdapter;  
    
    public Category(String title, Adapter adapter) {  
        mTitle = title;  
        mAdapter = adapter;  
    }  
    public void setTile(String title) {  
        mTitle = title;  
    }  
    public String getTitle() {  
        return mTitle;  
    }  
    public void setAdapter(Adapter adapter) {  
        mAdapter = adapter;  
    }  
    public Adapter getAdapter() {  
        return mAdapter;  
    }  

}

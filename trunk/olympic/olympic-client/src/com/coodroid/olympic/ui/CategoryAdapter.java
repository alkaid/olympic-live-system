package com.coodroid.olympic.ui;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import com.coodroid.olympic.model.Category;
/**
 * 用于listview分类的adapter，需要实现getViewTitle方法。
 * @author Cater
 *
 */
public  abstract class CategoryAdapter extends BaseAdapter{
	private List<Category> categories = new ArrayList<Category>();
	
	public void addCategory(String title,Adapter adapter){
		categories.add(new Category(title, adapter));
	}
	
	@Override
	public int getCount() {
		int total = 0;
		
		for(Category category:categories){
			total+=category.getAdapter().getCount()+1;
		}
		return total;
	}

	@Override
	public Object getItem(int position) {
        for (Category category : categories) {  
            if (position == 0) {  
                return category;  
            } 
            
            int size = category.getAdapter().getCount() + 1;  
            
            if (position < size) {  
                return category.getAdapter().getItem(position-1);  
            } 
            
            position -= size;  
        }  
        return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	 public int getViewTypeCount() {  
	        int total = 1;  
	          
	        for (Category category : categories) {  
	            total += category.getAdapter().getViewTypeCount();  
	        }  
	          
	        return total;  
	  } 
	 
	  public int getItemViewType(int position) {  
	        int typeOffset = 1;  
	          
	        for (Category category : categories) {  
	            if (position == 0) {  
	                return 0;  
	            }  
	              
	            int size = category.getAdapter().getCount() + 1;  
	            if (position < size) {  
	                return typeOffset + category.getAdapter().getItemViewType(position - 1);  
	            }  
	            position -= size;  
	              
	            typeOffset += category.getAdapter().getViewTypeCount();  
	        }  
	          
	        return -1;  
	   }
	  
	  

	@Override
	public  View getView(int position, View convertView, ViewGroup parent) {
//		ViewHolder holder;
//		if(convertView==null){
//			convertView = mInflater.inflate(R.layout.match_project_sort,null); 
//			holder = new ViewHolder();
//			holder.bjTime = (TextView) convertView.findViewById(R.id.match_time);
//			holder.name = (TextView) convertView.findViewById(R.id.match_name);
//			holder.project = (TextView) convertView.findViewById(R.id.match_project_title);
//			convertView.setTag(holder);
//		}else{
//			holder = (ViewHolder) convertView.getTag();				
//		}
//		Match m = matchsOrderByTime.get(position);
//		holder.bjTime.setText(m.getBjTime());
//		holder.name.setText(m.getName());
//
//		LogUtil.v("MAOXIA",position+"");
//		
//         int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色 
//         view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同 
//		return convertView; 
	        int categoryIndex = 0;  
	          
	        for (Category category : categories) {  
	            if (position == 0) {  
	                return getTitleView(category.getTitle(), categoryIndex,convertView, parent);  
	            }  
	            int size = category.getAdapter().getCount()+1;  
	            if (position < size) {  
	                return category.getAdapter().getView(position - 1, convertView, parent);  
	            }  
	            position -= size;  
	              
	            categoryIndex++;  
	        }  
	          
	        return null;  
	    } 
	
	protected abstract View getTitleView(String caption,int index,View convertView,ViewGroup parent);
	
//	static class ViewHolder{
//		
//	}
//	
}

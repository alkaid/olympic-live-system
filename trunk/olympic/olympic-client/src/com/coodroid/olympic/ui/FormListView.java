/**
 * 
 */
package com.coodroid.olympic.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.SystemUtil;

/**
 * @author Alkaid
 * 表单控件
 */
public class FormListView extends ListView {
	private Context context;
	private FormListAdapter adapter;
	/**用于初始化表单内容的数据*/
	private List<FormListAdapter.Item> data;

	/**
	 * 
	 * @param context
	 * @param data 用于初始化表单内容的数据
	 */
	public FormListView(Context context,List<FormListAdapter.Item> data) {
		super(context);
		this.context=context;
		this.data=data;
		initView();
	}
	/**
	 * 初始化view 设置adapter
	 */
	private void initView(){
		this.setBackgroundResource(R.drawable.loginbox);
		this.setCacheColorHint(0);
		this.setDivider(new ColorDrawable(0xffbbbbbb));
		this.setDividerHeight(1);
		adapter=new FormListAdapter(context, data);
		this.setAdapter(adapter);
		//设置第一个input获取焦点
//		adapter.viewHolders.get(data.get(0).tag).etInput.requestFocus();
	}
	/**
	 * 计算并获得ListView总高度
	 * @return
	 */
	public int getViewHeight(){
		return adapter.totalHeight;
	}
	/**
	 * 为表单独项设置错误信息
	 * @param tag
	 * @param warn
	 */
	public void setWarnInfo(String tag,String warn){
		adapter.setWarnInfo(tag, warn);
	}
	/**
	 * 服务器返回错误，客户端设置好错误后重设焦点
	 */
	public void requestFocusOnError(){
		adapter.requestFocusOnError();
	}
	/*public void showWarnIcon(String tag){
		adapter.showWarnIcon(tag);
	}
	public void removeWarnIcon(String tag){
		adapter.removeWarnIcon(tag);
	}
	public void showWarnInfo(String tag){
		adapter.showWarnInfo(tag);
	}
	public void showWarnInfo(String tag,String warn){
		adapter.showWarnInfo(tag, warn);
	}
	public void removeWarnInfo(String tag){
		adapter.removeWarnInfo(tag);
	}*/
	/**
	 * 校验表单内容  前置条件是必须设置表单 {@link #data}中每个item的校验器
	 * @return 是否通过校验
	 */
	public boolean validate(){
		return adapter.validate();
	}
	/**
	 * 获得表单数据
	 * @return
	 */
	public Map<String, String> getFormBean(){
		return adapter.getFormBean();
	}
	
	
	/**
	 * 适配外部类 {@link #FormListView(Context, List)}的专属适配器
	 * @author Alkaid
	 *
	 */
	public static class FormListAdapter extends BaseAdapter{
		private DisplayMetrics dm;
		private List<Item> data;
		private Context context;
		/** 持有listView的所有控件*/
		private Map<String,ViewHolder> viewHolders=new HashMap<String, ViewHolder>();
		/** 将data转化为Map方便根据 item.tag取数据*/
		private Map<String, Item> dataMap=new HashMap<String, Item>();
//		/** 验证不通过时弹出的popupWindow的xOffset*/
//		private int warntipOffset=0;
		/** 每个item的高度*/
		private int itemHeight=0;
		/** listview总高度*/
		private int totalHeight=0;
		/**
		 * 
		 * @param context
		 * @param data 用于初始化表单内容的数据
		 */
		public FormListAdapter(Context context,List<Item> data){
			this.data=data;
			this.context=context;
			for (Item item : data) {
				dataMap.put(item.tag, item);
			}
			dm=SystemUtil.getDm((Activity) this.context);
//			warntipOffset=(int) (-10*dm.density);
			//计算高度
			itemHeight=(int) (50*dm.density);
			totalHeight=(int) (itemHeight*data.size()+10*dm.density);
		}
		/**
		 * 每个Item中所有控件的持有类
		 * @author Alkaid
		 *
		 */
		private static class ViewHolder{
			EditText etInput;
			ImageView imgvWarn;
			PopupWindow popWarnDetail;
		}
		
		@Override
		public int getCount() {
			return data.size();
		}
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		private void showWarnIcon(String tag){
			ImageView imgWarn=viewHolders.get(tag).imgvWarn;
			imgWarn.setVisibility(View.VISIBLE);
		}
		/*private void showWarnInfo(String tag,String warn){
			PopupWindow pop=createWarnTip(tag, warn);
			pop.showAsDropDown(viewHolders.get(tag).imgvWarn);
			
		}*/
		public void showWarnInfo(String tag){
			if(!viewHolders.get(tag).popWarnDetail.isShowing())
				viewHolders.get(tag).popWarnDetail.showAsDropDown(viewHolders.get(tag).imgvWarn);
		}
		/**
		 * 为表单独项设置错误信息
		 * @param tag
		 * @param warn
		 */
		public void setWarnInfo(final String tag,String warn){
			showWarnIcon(tag);
			final PopupWindow pop=createWarnTip(tag, warn);
			viewHolders.get(tag).imgvWarn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(pop.isShowing())
						pop.dismiss();
					else
						pop.showAsDropDown(v);
				}
			});
			final ImageView warnIcon=viewHolders.get(tag).imgvWarn;
			viewHolders.get(tag).etInput.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						if(warnIcon.getVisibility()==View.VISIBLE){
							pop.showAsDropDown(warnIcon);
						}
					}else{
						//失去焦点时校验该项
						pop.dismiss();
						Validater validater=dataMap.get(tag).validater;
						String text=((EditText)v).getText().toString();
						if(validater.validate(text)){
							removeWarnIcon(tag);
						}else{
							setWarnInfo(tag, validater.warn);
						}
					}
				}
			});
		}
		private PopupWindow createWarnTip(String tag,String warn){
			PopupWindow pop;
			if(null==viewHolders.get(tag).popWarnDetail){
				TextView tvWarnDetail=new TextView(this.context);
				tvWarnDetail.setText(warn);
				tvWarnDetail.setTextColor(0xffff0000);
				tvWarnDetail.setSingleLine();
				pop=new PopupWindow(tvWarnDetail,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				pop.setFocusable(false);
				pop.setOutsideTouchable(false);
				Drawable background=this.context.getResources().getDrawable(R.drawable.warntip);
				background.setAlpha(200);
				pop.setBackgroundDrawable(background);
				pop.update();
				viewHolders.get(tag).popWarnDetail=pop;
			}else{
				pop=viewHolders.get(tag).popWarnDetail;
				((TextView)pop.getContentView()).setText(warn);
				pop.update();
			}
			return pop;
		}
		private void removeWarnIcon(String tag){
			ImageView imgWarn=viewHolders.get(tag).imgvWarn;
			imgWarn.setVisibility(View.INVISIBLE);
		}
		private void removeWarnInfo(String tag){
			PopupWindow pop=viewHolders.get(tag).popWarnDetail;
			if(null!=pop){
				pop.dismiss();
			}
		}
		/**
		 * 服务器返回错误，客户端设置好错误后重设焦点并弹框
		 */
		public void requestFocusOnError(){
			for (Item item : data) {
				if(viewHolders.get(item.tag).imgvWarn.getVisibility()==View.VISIBLE){
					viewHolders.get(item.tag).etInput.requestFocus();
					showWarnInfo(item.tag);
					return;
				}
			}
		}
		/**
		 * 校验表单内容  前置条件是必须设置表单 {@link #data}中每个item的校验器
		 * @return 是否通过校验
		 */
		public boolean validate(){
			boolean pass=true;
			boolean firstErrorItem=true;
			for(Item item:data){
				if(null!=item.validater){
					boolean itemPass = item.validater.validate(viewHolders.get(item.tag).etInput.getText().toString());
					if(itemPass){
						removeWarnInfo(item.tag);
						removeWarnIcon(item.tag);
					}else{
						setWarnInfo(item.tag, item.validater.warn);
						if(firstErrorItem){
							showWarnInfo(item.tag);
							firstErrorItem=false;
							viewHolders.get(item.tag).etInput.requestFocus();
						}
						pass=false;
					}
				}
			}
			return pass;
		}
		/**
		 * 获得表单数据
		 * @return
		 */
		public Map<String, String> getFormBean(){
			Map<String, String> form=new HashMap<String, String>();
			for(Item item:data){
				form.put(item.tag, viewHolders.get(item.tag).etInput.getText().toString());
			}
			return form;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder=null;
			if(convertView==null){
				LinearLayout llItem=new LinearLayout(this.context);
				llItem.setBackgroundColor(0);
				llItem.setGravity(Gravity.CENTER_VERTICAL);
				EditText etInput=new EditText(this.context);
				etInput.setBackgroundColor(0);
				etInput.setTextColor(0xff000000);
				etInput.setSingleLine();
				ImageView imgvWarn=new ImageView(this.context);
				imgvWarn.setImageResource(R.drawable.warn);
				LinearLayout.LayoutParams lpInput=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,itemHeight);
				lpInput.weight=1;
				lpInput.gravity=Gravity.CENTER_VERTICAL;
				llItem.addView(etInput,lpInput);
				int imgSize=(int) (19*dm.density);
				LinearLayout.LayoutParams lpWarn=new LinearLayout.LayoutParams(imgSize,imgSize);
				lpWarn.setMargins(0, 0, (int) (10*dm.density), 0);
				llItem.addView(imgvWarn,lpWarn);
				viewHolder=new ViewHolder();
				viewHolder.etInput=etInput;
				viewHolder.imgvWarn=imgvWarn;
				convertView=llItem;
				convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder) convertView.getTag();
			}
			viewHolders.put(data.get(position).tag, viewHolder);
			viewHolder.etInput.setHint(data.get(position).hint);
			//若是密码
			if(data.get(position).tag.contains("password")){
				viewHolder.etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			viewHolder.imgvWarn.setVisibility(View.INVISIBLE);
			return convertView;
		}
		/**
		 * 用于初始化表单内容的当个输入项数据
		 * @author Alkaid
		 *
		 */
		public static class Item{
			/** 输入框为空时的提示*/
			public String hint;
			/** 唯一标识 推荐用'email' 'password' 等表单输入项名称*/
			public String tag;
			/** 内容合法校验器*/
			public Validater validater;
			/**
			 * 若用此构造方法，必须在后面手动设置{@link #validater}
			 * @param tag 唯一标识 推荐用'email' 'password' 等表单输入项名称
			 * @param hint 输入框为空时的提示
			 */
			public Item(String tag,String hint){
				this.tag=tag;
				this.hint=hint;
			}
			/**
			 * 
			 * @param tag 唯一标识 推荐用'email' 'password' 等表单输入项名称
			 * @param hint 输入框为空时的提示
			 * @param validater
			 */
			public Item(String tag,String hint,Validater validater){
				this.tag=tag;
				this.hint=hint;
				this.validater=validater;
			}
		}
		/**
		 * 内容合法校验器  子类必须在 validate()方法中重写父类的 warn 变量
		 * @author Alkaid
		 *
		 */
		public static abstract class Validater{
			public String warn;
			public abstract boolean validate(String text);
		}
	}
	
}

package com.coodroid.olympic.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityGroup;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.GuessDBDAO;
import com.coodroid.olympic.model.Answer;
import com.coodroid.olympic.model.Question;
import com.coodroid.olympic.view.account.LoginActivity;
import com.coodroid.olympic.view.account.RegisterActivity;

/**
 * 
 * @author Cater
 *
 */

public class GuessActivity extends BaseActivity{
	public static final String ACTION="com.coodroid.olympic.view.GuessActivity";
	private static final String PROMPT = "请选择答案，按提交按钮进行提交！！";
	
	private GuessDBDAO db;
//	/**用于存放需要显示的答案的map，integer表示问题id对应相应的answer*/
//	private Map<Integer, List<Answer>> answers;
	/**用于存放需要显示的问题的List*/
	private List<Question> questions;
	private LayoutInflater mInflater;
	
	
	/**初始化view*/
	private ListView guessList;
	private TextView guessDate;
	private TextView guessHistory;
	private LinearLayout container;
	private ImageView refresh;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guess);
		init();
	}
	 
	 
	/**
	 * 做一些操作 包括view的初始化
	 */
	private void init(){
		guessList = (ListView) findViewById(R.id.guess_content_list);
		guessDate = (TextView) findViewById(R.id.guess_date);
		guessHistory = (TextView) findViewById(R.id.guess_history);
		refresh = (ImageView) findViewById(R.id.refresh_btn);
		container = (LinearLayout)((ActivityGroup)getParent()).getWindow().findViewById(R.id.containerBody);
		
		db = new GuessDBDAO(this);
//		answers = new HashMap<Integer, List<Answer>>();
		questions = new ArrayList<Question>();
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		loadData();
		QuizAdapter adapter = new QuizAdapter();
		guessDate.setText("最后更新时间: "+SystemUtil.getCurrentDateTime());
		guessList.setAdapter(adapter);
		//点击跳转到
		guessHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GuessActivity.this,UserAnswersActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				container.removeAllViews();
				container.addView(((ActivityGroup)GuessActivity.this.getParent()).getLocalActivityManager()
							.startActivity("UserAnswerActivity", 
								intent)
								.getDecorView());
			}
		});
		
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GuessActivity.this,RegisterActivity.class);
				GuessActivity.this.startActivity(intent);
			}
		});
	}
	 
	private void loadData(){
		List<Question> qs = null;
		if(SystemUtil.checkNet(this)){	
			qs = analyze(getServerData());
			if(qs!=null){
				for(Question q:qs){
					db.addOrUpdate(q);
				}
			}
		}
		Cursor c = db.query(SystemUtil.getCurrentDate());
		if(c.moveToFirst()){
			int lastQuestionId = -1;
			do{				
				if(c.getInt(0)!=lastQuestionId){
					lastQuestionId = c.getInt(0);
					Question q = new Question(c.getInt(0));
					q.setOrder(c.getInt(1));
					q.setScore(c.getInt(2));
					q.setDate(c.getString(3));
					q.setText(c.getString(4));
					q.setAnswers(new ArrayList<Answer>());
					questions.add(q);
//					answers.put(lastQuestionId, new ArrayList<Answer>());
				}
				Answer a = new Answer(c.getInt(5));
				a.setOrder(c.getInt(6));
				a.setQuestionId(lastQuestionId);
				a.setText(c.getString(7));
				questions.get(questions.size()-1).getAnswers().add(a);
//				answers.get(lastQuestionId).add(a);
			}while(c.moveToNext());
		}		
	}
	 
	/**
	 * 发送请求 返回textLive更新的数据
	 * @return JSON用于解析
	 */
	
	private String getServerData(){
		String matchServerData = null;
    	try {
			HttpRequest request=new HttpRequest();
			matchServerData=request
					.setConnectionTimeout(3000)
					.setRetryCount(0)
					.setCookieStore(global.cookieStore)
					.setUrl(Constants.url.api.question)
					.setMethod(HttpRequest.METHOD_GET)
					.setCharset("utf-8")
					.getContent();
			LogUtil.i(matchServerData);
		} catch (MalformedURLException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return matchServerData;
		
	}
	
	/**
	 * 将服务端返回的数据进行解析获得
	 * @param matchServerData 服务端的Json
	 * @return 返回多条比赛记录
	 */
	private List<Question> analyze(String ServerData){
		if(ServerData!=null){
			List<Question> questions = new ArrayList<Question>();
			try {
				JSONObject questionJSON = new JSONObject(ServerData);
				if(questionJSON.getString("status").equals("2")){
					JSONArray questionArray = questionJSON.getJSONArray("data");
					for(int i=0;i<questionArray.length();i++){
						JSONObject questionObject = (JSONObject) questionArray.opt(i);
						Question q = new Question(Integer.parseInt(questionObject.getString("id")));
						q.setOrder(Integer.parseInt(questionObject.getString("order")));
						q.setDate(questionObject.getString("date"));
						q.setText(questionObject.getString("text"));
						q.setScore(Integer.parseInt(questionObject.getString("score")));
						q.setAnswerId(questionObject.getString("answerId"));
						JSONArray answerArray = questionObject.getJSONArray("answer");
						List<Answer> answers = new ArrayList<Answer>();
						for(int j=0;j<answerArray.length();j++){
							JSONObject answerObject = (JSONObject) answerArray.opt(j);
							Answer answer = new Answer(Integer.parseInt(answerObject.getString("id")));
							answer.setOrder(Integer.parseInt(answerObject.getString("order")));
							answer.setQuestionId(Integer.parseInt(answerObject.getString("questionId")));
							answer.setText(answerObject.getString("text"));
							answers.add(answer);
						}
						q.setAnswers(answers);
						questions.add(q);
					}
				}
			} catch (JSONException e) {
				LogUtil.e(e);
				LogUtil.e("JSON解析异常");
			}
			return questions;
		}else{
			return null;
		}
	}
	
	private class QuizAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return questions.size();
		}

		@Override
		public Object getItem(int position) {
			return questions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.quiz_item,null); 
				holder = new ViewHolder();
				holder.questionView = (TextView) convertView.findViewById(R.id.question_content);
				holder.questionOrder = (TextView) convertView.findViewById(R.id.question_order);
				holder.questionScore = (TextView) convertView.findViewById(R.id.question_score);
				holder.answerGroup =  (RadioGroup) convertView.findViewById(R.id.answer_radioGroup);
				holder.answerPrompt = (TextView) convertView.findViewById(R.id.answer_prompt);
				holder.submitBtn = (Button) convertView.findViewById(R.id.quiz_sumbit_btn);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();				
			}
			final Question q = questions.get(position);
			holder.questionOrder.setText(q.getOrder()+"");
			holder.questionView.setText(q.getText());
			holder.questionScore.setText("(奖"+q.getScore()+"分)");
//			List<Answer> as = answers.get(q.getId());
			List<Answer> as = q.getAnswers();
			for(int i=0;i<as.size();i++){
				Answer a = as.get(i);
				RadioButton rb = new RadioButton(GuessActivity.this);
				rb.setText(a.getText());
				holder.answerGroup.addView(rb);
				if(i==0){
					rb.setChecked(true);
				}
		
			}
			if(q.getAnswer()!=null){
				holder.answerPrompt.setText("你已提交答案，你提交的答案是"+q.getAnswer().getText());
			}else{
				holder.answerPrompt.setText(PROMPT);
			}
			//用于提交答案。
			holder.submitBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new SubmitAsyncTask(holder.answerGroup,q,holder.answerPrompt).execute(null);
				}
			});
			return convertView;
		}
		
	}
	
	private class SubmitAsyncTask extends AsyncTask<Void, Void, String>{
		
		private RadioGroup rg;
		private Question q;
		private TextView prompt;
		private Answer answer;
		
		public SubmitAsyncTask(RadioGroup rg,Question q,TextView prompt) {
			this.rg = rg;
			this.q = q;
			this.prompt = prompt;
		}

		@Override
		protected String doInBackground(Void... params) {
			
			int questionId = q.getId();
			for(int i =0;i<q.getAnswers().size();i++){
				if(rg.getChildAt(i).getId()==rg.getCheckedRadioButtonId()){
					answer = q.getAnswers().get(i);
				}
			}
			int answerId = answer.getId();			
			//返回的json状态,用于判断是否提交成功
			String statusData = null;
			Map<String, String> httpParams=null;
			if(questionId>=0&&answerId>=0){
				httpParams = new HashMap<String, String>();
				String jsonStr="[{\"questionId\":"+questionId+",\"answerId\":"+answerId+"}]";
				httpParams.put("answer", jsonStr);
		    	try {
//		    		Map<String,String> debugHeader=new HashMap<String, String>();
//		    		debugHeader.put("cookie", "PHPSESSID=8e98e665374e67c640ab1663334e4fd9; language=en; currency=USD; PHPSESSID=8e98e665374e67c640ab1663334e4fd9; ZDEDebuggerPresent=php,phtml,php3; debug_host=192.168.235.101,127.0.0.1; debug_fastfile=1; debug_port=10137; start_debug=1; send_debug_header=1; send_sess_end=1; debug_jit=1; original_url=http://192.168.235.101/ocdemo/index.php?route=olympic/grepdata; debug_stop=1; use_remote=1; debug_session_id=75942145");
//		    		debugHeader.put("cookie", "PHPSESSID=8e98e665374e67c640ab1663334e4fd9; language=en; currency=USD; PHPSESSID=8e98e665374e67c640ab1663334e4fd9; ZDEDebuggerPresent=php,phtml,php3;");
					HttpRequest request=new HttpRequest();
					statusData=request
//							.setHeader(debugHeader)
							.setConnectionTimeout(3000)
							.setRetryCount(0)
							.setCookieStore(global.cookieStore)
							.setUrl(Constants.url.user.guessSubmit)
							.setParams(httpParams)
							.setMethod(HttpRequest.METHOD_POST)
							.setCharset("utf-8")
							.getContent();
				} catch (MalformedURLException e) {
					LogUtil.e(e);
					LogUtil.e("提交答题遇到网络问题");
				} catch (IOException e) {
					LogUtil.e(e);
					LogUtil.e("提交答题遇到网络问题");
				}
			}
			//解析json用于判断是否提交成功
			if(statusData!=null){			
				try {
					JSONObject statusJSON = new JSONObject(statusData);
					String statusSend = statusJSON.getString("status");
					if(statusSend.equals("2")){
						return 2+"";
					}else if(statusSend.equals("-1")){
						return -1+"";
					}else{
						return null;
					}
				} catch (JSONException e) {
					LogUtil.e(e);
					LogUtil.e("JSON解析异常 statusData="+statusData);
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(null==result){
				Toast.makeText(GuessActivity.this, "提交失败，请稍后再重新提交", Toast.LENGTH_SHORT);
				return;
			}
			if(result.equals("2")){
				Toast.makeText(GuessActivity.this, "答题成功提交", Toast.LENGTH_SHORT);
				prompt.setText("你已提交答案，你提交的答案是"+answer.getText());
			}else if(result.equals("-1")){
LogUtil.v("请登录");
				Intent intent = new Intent(GuessActivity.this,LoginActivity.class);
				intent.putExtra("from", GuessActivity.ACTION);
				GuessActivity.this.startActivity(intent);
			}else{
				Toast.makeText(GuessActivity.this, "提交失败，请稍后再重新提交", Toast.LENGTH_SHORT);
			}
		}
	}
	
	
	static class ViewHolder{
		TextView questionView;
		TextView questionOrder;
		TextView questionScore;
		TextView answerPrompt;
		RadioGroup answerGroup;
		Button submitBtn;
	}
}

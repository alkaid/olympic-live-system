package com.coodroid.olympic.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityGroup;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coodroid.olympic.R;
import com.coodroid.olympic.common.Constants;
import com.coodroid.olympic.common.HttpRequest;
import com.coodroid.olympic.common.LogUtil;
import com.coodroid.olympic.common.StringUtil;
import com.coodroid.olympic.common.SystemUtil;
import com.coodroid.olympic.data.UserAnswerDBDAO;
import com.coodroid.olympic.model.Answer;
import com.coodroid.olympic.model.Question;
import com.coodroid.olympic.view.account.LoginActivity;

public class UserAnswersActivity extends BaseActivity{
	
	private UserAnswerDBDAO db;
	/**用于存放需要显示的问题的List*/
	private List<Question> userQuestions;
	private LayoutInflater mInflater;
	private QuizAdapter adapter;
	/**服务端状态用于判断是否登录*/
	
	/**初始化view*/
	private ListView userGuessList;
	private TextView userGuessDate;
	private ImageView refreshBtn;
	private ProgressBar userGuessBar;
	private LinearLayout container;
	private TextView btnBack;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_answers);
		init();
	}
	
	private void init(){
		//初始化view
		userGuessList = (ListView) findViewById(R.id.user_guess_content_list);
		userGuessDate = (TextView) findViewById(R.id.user_guess_date);
		refreshBtn = (ImageView) findViewById(R.id.refresh_btn);
		userGuessBar = (ProgressBar) findViewById(R.id.userAnswersProcessBar);
		container = (LinearLayout)((ActivityGroup)getParent()).getWindow().findViewById(R.id.containerBody);
		btnBack = (TextView) findViewById(R.id.btnBack);
		//初始化对象
		db = new UserAnswerDBDAO(this);
		userQuestions = new ArrayList<Question>();
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		//初始化操作
		adapter = new QuizAdapter();
		refreshOperator();
		
		refreshBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshOperator();
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserAnswersActivity.this,GuessActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				container.removeAllViews();
				container.addView(((ActivityGroup)UserAnswersActivity.this.getParent()).getLocalActivityManager()
							.startActivity("GuessActivity", 
								intent)
								.getDecorView());
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0){
			Intent intent = new Intent(UserAnswersActivity.this,GuessActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			container.removeAllViews();
			container.addView(((ActivityGroup)UserAnswersActivity.this.getParent()).getLocalActivityManager()
						.startActivity("GuessActivity", 
							intent)
							.getDecorView());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		userGuessList.setAdapter(adapter);
//		userGuessDate.setText("最后查看: "+SystemUtil.getCurrentDateTime());
//		refreshBtn.setVisibility(View.VISIBLE);
//		userGuessBar.setVisibility(View.GONE);
//		
//	}
	
	/**
	 * 做一系列操作，更新数据
	 */
	private void refreshOperator(){
		refreshBtn.setVisibility(View.GONE);
		userGuessBar.setVisibility(View.VISIBLE);
		new UserAnswersAsyncTask().execute(null);
	}
	
	/**
	 * 发送请求 返回用户答题列表所有的数据
	 * @return JSON用于解析
	 */
	
	private String getServerData(){
		String userAnswerServerData = null;
	    	try {
//	    		Map<String, String> params = new HashMap<String, String>();
				HttpRequest request=new HttpRequest();
//	    		Map<String,String> debugHeader=new HashMap<String, String>();
//	    		debugHeader.put("cookie", "PHPSESSID=8e98e665374e67c640ab1663334e4fd9; language=en; currency=USD; PHPSESSID=8e98e665374e67c640ab1663334e4fd9; ZDEDebuggerPresent=php,phtml,php3; debug_host=192.168.235.101,127.0.0.1; debug_fastfile=1; debug_port=10137; start_debug=1; send_debug_header=1; send_sess_end=1; debug_jit=1; original_url=http://192.168.235.101/ocdemo/index.php?route=olympic/grepdata; debug_stop=1; use_remote=1; debug_session_id=75942145");
//	    		debugHeader.put("cookie", "PHPSESSID=8e98e665374e67c640ab1663334e4fd9; language=en; currency=USD; PHPSESSID=8e98e665374e67c640ab1663334e4fd9; ZDEDebuggerPresent=php,phtml,php3;");
				userAnswerServerData=request
						.setConnectionTimeout(3000)
						.setRetryCount(1)
//						.setHeader(debugHeader)
						.setCookieStore(global.cookieStore)
						.setUrl(Constants.url.user.history)
						.setMethod(HttpRequest.METHOD_GET)
						.setCharset("utf-8")
						.getContent();
			} catch (MalformedURLException e) {
				Toast.makeText(this, "网络不给力,请稍后再试!!", Toast.LENGTH_SHORT).show();
				LogUtil.e(e);
			} catch (IOException e) {
				Toast.makeText(this, "网络不给力,请稍后再试!!", Toast.LENGTH_SHORT).show();
				LogUtil.e(e);
			}
		return userAnswerServerData;	
	}
	
	/**
	 * 将服务端返回的数据进行解析获得
	 * @param matchServerData 服务端的Json
	 * @return 返回多条比赛记录
	 */
	public List<Question> analyze(String answersServerData){
		if(answersServerData!=null){
			List<Question> questions = new ArrayList<Question>();
			try {
				JSONObject questionJSON = new JSONObject(answersServerData);
				if(questionJSON.getString("status").equals("2")){
					JSONArray questionArray = questionJSON.getJSONArray("data");
					for(int i=0;i<questionArray.length();i++){
						JSONObject questionObject = (JSONObject) questionArray.opt(i);
						Question q = new Question(Integer.parseInt(questionObject.getString("id")));
						q.setOrder(Integer.parseInt(questionObject.getString("order")));
						q.setDate(questionObject.getString("date"));
						q.setText(questionObject.getString("text"));
						q.setAnswerId(StringUtil.formatJSON(questionObject.getString("answerId")));
						q.setScore(Integer.parseInt(questionObject.getString("score")));
						JSONArray answerArray = questionObject.getJSONArray("answer");
						List<Answer> answers = new ArrayList<Answer>();
						for(int j=0;j<answerArray.length();j++){
							JSONObject answerObject = (JSONObject) answerArray.opt(j);
							Answer answer = new Answer(Integer.parseInt(answerObject.getString("id")));
							answer.setOrder(Integer.parseInt(answerObject.getString("order")));
							answer.setQuestionId(Integer.parseInt(answerObject.getString("questionId")));
							answer.setText(answerObject.getString("text"));
							//判断是否是用户所答的answer
							String uAnswerId = StringUtil.formatJSON(questionObject.getString("uanswerId"));
							if(!TextUtils.isEmpty(uAnswerId)&&
									uAnswerId.equals(answerObject.getString("id"))){
								String isRight = questionObject.getString("isRight");
								if(TextUtils.isEmpty(isRight)){
									answer.setIsRight(-1);
								}else if(isRight.equals("1")||isRight.equals("0")){
									answer.setIsRight(Integer.parseInt(isRight));
								}
								q.setAnswer(answer);
							}
							answers.add(answer);
						}
						q.setAnswers(answers);
						questions.add(q);
					}
				}else if(questionJSON.getString("status").equals("-1")){
LogUtil.v("请登录");
					Intent intent = new Intent(UserAnswersActivity.this,LoginActivity.class);
					intent.putExtra("from", GuessActivity.ACTION);
					UserAnswersActivity.this.startActivity(intent);
					return null;
				}
			} catch (JSONException e) {
				Toast.makeText(this, "网络不给力,请稍后再试!!", Toast.LENGTH_SHORT).show();
				LogUtil.e(e);
				LogUtil.e("JSON解析异常");
			}
			return questions;
		}else{
			return null;
		}
	}
	
	/**
	 * 将最新数据更新到数据库中
	 */
	private void updateData(){
			if(SystemUtil.checkNet(this)){	
				 List<Question> qs = analyze(getServerData());
				if(qs!=null){
					for(Question q:qs){
						db.addOrUpdate(q);
					}
				}
			}
	}
	
	/**
	 * 查询数据库，将相应的数据读取到内存中
	 */
	private void loadAllUserAnswer(){
		userQuestions.clear();
		Cursor c = db.queryAllUserAnswer();
		if(c!=null){
			if(c.moveToFirst()){
				int lastQuestionId = -1;
				Question q = null;
				do{	
					if(c.getInt(0)!=lastQuestionId){
						lastQuestionId = c.getInt(0);
						q = new Question(c.getInt(0));
						q.setDate(c.getString(1));
						q.setText(c.getString(2));
						q.setScore(c.getInt(3));
						Answer selectAnswer = new Answer(c.getInt(4));
						selectAnswer.setIsRight(c.getInt(5));
						q.setAnswer(selectAnswer);
						q.setAnswers(new ArrayList<Answer>());
						q.setAnswerId(StringUtil.formatJSON(c.getString(9)));
						userQuestions.add(q);
					}
					if(c.getInt(4)==c.getInt(6)){
						Answer ua = userQuestions.get(userQuestions.size()-1).getAnswer();
						ua.setOrder(c.getInt(7));
						ua.setText(c.getString(8));
						q.setAnswer(ua);
					}
					Answer a = new Answer(c.getInt(6));
					a.setOrder(c.getInt(7));
					a.setQuestionId(lastQuestionId);
					a.setText(c.getString(8));
					userQuestions.get(userQuestions.size()-1).getAnswers().add(a);
				}while(c.moveToNext());
		}
	}	
	}
	
	private class QuizAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return userQuestions.size();
		}

		@Override
		public Object getItem(int position) {
			return userQuestions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.user_answers_item,null); 
				holder = new ViewHolder();
				holder.questionView = (TextView) convertView.findViewById(R.id.user_question_content);
				holder.questionOrder = (TextView) convertView.findViewById(R.id.user_question_order);
				holder.questionScore = (TextView) convertView.findViewById(R.id.user_question_score);
				holder.answerGroup =  (RadioGroup) convertView.findViewById(R.id.user_answer_radioGroup);
				holder.answerPrompt = (TextView) convertView.findViewById(R.id.user_answer_prompt);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();				
			}
			final Question q = userQuestions.get(position);
			holder.questionOrder.setText((position+1)+"");
			holder.questionView.setText(q.getText());
			holder.questionScore.setText("(奖"+q.getScore()+"分)");
//			List<Answer> as = answers.get(q.getId());
			List<Answer> as = q.getAnswers();
			holder.answerGroup.removeAllViews();
			for(int i=0;i<as.size();i++){
				Answer a = as.get(i);
				RadioButton rb = new RadioButton(UserAnswersActivity.this);
				rb.setText(a.getText());
				holder.answerGroup.addView(rb);
				if(q.getAnswerId()!=null){
					if(a.getId()==Integer.parseInt(q.getAnswerId())){
						rb.setChecked(true);
					}
				}
				rb.setEnabled(false);
		
			}
			if(q.getAnswer().getIsRight()==1&&q.getAnswer()!=null&&q.getAnswerId()!=null){
				holder.answerPrompt.setText("你的答案是正确的，你的答案是『"+q.getAnswer().getText()+"』");
			}else if(q.getAnswer().getIsRight()==0&&q.getAnswer()!=null&&q.getAnswerId()!=null){
				holder.answerPrompt.setText("你的答案是不正确，你的答案是"+"『"+q.getAnswer().getText()+"』");
			}else if(q.getAnswerId()==null){
				holder.answerPrompt.setText("答案还未给出，请耐心等待，你的选择是"+"『"+q.getAnswer().getText()+"』");
			}
			return convertView;
		}
		
	}
	
	private class UserAnswersAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			updateData();
			loadAllUserAnswer();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			userGuessList.setAdapter(adapter);
			userGuessDate.setText("最后查看: "+SystemUtil.getCurrentDateTime());
			refreshBtn.setVisibility(View.VISIBLE);
			userGuessBar.setVisibility(View.GONE);
			
		}
		
	}
	
	static class ViewHolder{
		TextView questionView;
		TextView questionOrder;
		TextView questionScore;
		TextView answerPrompt;
		RadioGroup answerGroup;
	}
}

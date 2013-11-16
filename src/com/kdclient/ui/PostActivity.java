package com.kdclient.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.kdclient.GridViewFaceAdapter;
import com.kdclient.R;
import com.kdclient.SaveData;
import com.kdclient.Utils;
import com.kdclient.api.kdApi;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class PostActivity extends BaseActivity implements OnClickListener {
	private EditText mEdit;
    private GridView mGridView;
    private GridViewFaceAdapter mGVFaceAdapter;
    private InputMethodManager imm;
    private TextView mTextNum;
    private int INPUT_MAX_LENGTH = 1000;
    
    private String mContent = "";
    private AQuery aq;
    private kdApi api = new kdApi();
    private boolean _isPost = false;
    private String tid;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        
        Intent intent = getIntent(); 
        JSONObject content = (JSONObject)JSONValue.parse(intent.getStringExtra("topic").toString()); 
        tid = content.get("tid").toString();
        
        if(tid != "0"){
        	_isPost = true;
        }
        
        initView();
        initGridView();
    }
    
    public void initView() {
        this.setContentView(R.layout.newtopic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        aq = new AQuery(this);

        aq.id(R.id.ll_text_limit_unit).clicked(this);
        aq.id(R.id.ib_face_keyboard).clicked(this);
        
        mTextNum = (TextView) findViewById(R.id.tv_text_limit);
        
        mEdit = (EditText) this.findViewById(R.id.et_mblog);
        mEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mText = mEdit.getText().toString();
                int len = mText.length();
                if (len <= INPUT_MAX_LENGTH) {
                    len = INPUT_MAX_LENGTH - len;
                    mTextNum.setTextColor(getResources().getColor(R.color.gray));

                } else {
                    len = len - INPUT_MAX_LENGTH;
                    mTextNum.setTextColor(Color.RED);
                }
                
                mTextNum.setText(String.valueOf(len));
            }
        });
        
        mEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //显示软键盘
                showIMM();
            }
        });
        
        //aq.id(R.id.ly_loadlocation).visible();
    }
    
    
    //初始化表情控件
    private void initGridView() {
        mGVFaceAdapter = new GridViewFaceAdapter(this);
        mGridView = (GridView)findViewById(R.id.tweet_pub_faces);
        mGridView.setAdapter(mGVFaceAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //插入的表情
                SpannableString ss = new SpannableString(view.getTag().toString());
                Drawable d = getResources().getDrawable((int)mGVFaceAdapter.getItemId(position));
                d.setBounds(0, 0, 35, 35);//设置表情图片的显示大小
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                ss.setSpan(span, 0, view.getTag().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);              
                //在光标所在处插入表情
                mEdit.getText().insert(mEdit.getSelectionStart(), ss);              
            }           
        });
    }
    
    private void composeNewPost() {
    	HashMap<String, String> HMap = new HashMap<String, String>();
        HMap.put("token", SaveData.GetToken(PostActivity.this));
        HMap.put("tid", tid);
        HMap.put("content", mEdit.getText().toString());
    	
    	api.ASyncAccess("post_topic", HMap, new AsyncHttpResponseHandler () {
    		@Override
            public void onStart() {
    			Utils.debug("start post");
            }
    		
    		@Override
            public void onSuccess(String response) {
        		JSONObject result = api.Parse(response);
            	
            	displayToast("post result: " + result.get("code").toString());
            }
    	});
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.send, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                composeNewPost();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();

        aq.dismiss();
    }
    
    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return;
        }
        
        if (viewId == R.id.ll_text_limit_unit) {
            mContent = mEdit.getText().toString();
            if (TextUtils.isEmpty(mContent)) return;
            
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mEdit.setText("");
                }
            };

            Utils.showAlert(this, R.string.attention, R.string.delete_all,
                    getString(R.string.ok), listener,
                    getString(R.string.cancel), null);
        } else if (viewId == R.id.ib_face_keyboard) {
            showOrHideIMM();
        }
    }
    
    private void showIMM() {
        aq.id(R.id.ib_face_keyboard).tag(1);
        showOrHideIMM();
    }

    private void showFace() {
        aq.id(R.id.ib_face_keyboard).image(R.drawable.btn_insert_keyboard).tag(1);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void hideFace() {
        aq.id(R.id.ib_face_keyboard).image(R.drawable.btn_insert_face).tag(null);
        mGridView.setVisibility(View.GONE);
    }

    private void showOrHideIMM() {

        if (aq.id(R.id.ib_face_keyboard).getTag() == null) {
            ImageView faceOrKeyboard = (ImageView) findViewById(R.id.ib_face_keyboard);
            imm.hideSoftInputFromWindow(faceOrKeyboard.getWindowToken(), 0);
            showFace();
        } else {
            imm.showSoftInput(mEdit, 0);
            hideFace();
        }
    }
}
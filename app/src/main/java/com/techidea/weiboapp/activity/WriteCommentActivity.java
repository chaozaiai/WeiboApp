package com.techidea.weiboapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.techidea.weiboapp.BaseActivity;
import com.techidea.weiboapp.R;
import com.techidea.weiboapp.api.BoreWeiboApi;
import com.techidea.weiboapp.api.SimpleRequestListener;
import com.techidea.weiboapp.entity.Status;
import com.techidea.weiboapp.util.TitleBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/8/7.
 */
public class WriteCommentActivity extends BaseActivity implements View.OnClickListener{

    //评论输入框
    private EditText et_write_status;
    //底部按钮
    private ImageView iv_image;
    private ImageView iv_at;
    private ImageView iv_topic;
    private ImageView iv_emoji;
    private ImageView iv_add;

    //待评论微博
    private Status status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_status);
        //获取Intent传入的微博
        status = (Status)intent.getSerializableExtra("status");
        initView();
    }

    private void initView(){
        new TitleBuilder(this)
                .setTitleText("发表评论")
                .setLeftText("取消")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //取消发送评论，关闭本页面
                        WriteCommentActivity.this.finish();
                    }
                })
        .setRightText("发送")
        .setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment();
            }
        });

        et_write_status =(EditText)findViewById(R.id.et_write_status);
        iv_image = (ImageView)findViewById(R.id.iv_image);
        iv_at = (ImageView)findViewById(R.id.iv_at);
        iv_topic = (ImageView)findViewById(R.id.iv_topic);
        iv_emoji = (ImageView)findViewById(R.id.iv_emoji);
        iv_add = (ImageView)findViewById(R.id.iv_add);

        iv_image.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        iv_topic.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);
        iv_add.setOnClickListener(this);
    }

    private void sendComment(){

        String comment = et_write_status.getText().toString();
        if(TextUtils.isEmpty(comment)){
            showToast("微博内容不能为空");
            return;
        }

        try{
            comment = URLEncoder.encode(comment,"UTF-8");

            weiboApi.commentsCreate(status.getId(), comment,
                    new SimpleRequestListener(this, null) {
                        @Override
                        public void onComplete(String response) {

                            showToast("微博发送成功");
                            //微博发送成功后 设置result结果数据 关闭本页
                            Intent data = new Intent();
                            data.putExtra("sendCommentSuccess", true);
                            setResult(RESULT_OK, data);
                            WriteCommentActivity.this.finish();
                        }
                    });
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())){
            case R.id.iv_image:
                break;
            case R.id.iv_at:
                break;
            case R.id.iv_topic:
                break;
            case R.id.iv_emoji:
                break;
            case R.id.iv_add:
                break;
        }
    }


}

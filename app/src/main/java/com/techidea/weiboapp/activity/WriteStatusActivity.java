package com.techidea.weiboapp.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.techidea.weiboapp.BaseActivity;
import com.techidea.weiboapp.R;
import com.techidea.weiboapp.adapter.EmotionGvAdapter;
import com.techidea.weiboapp.adapter.EmotionPagerAdapter;
import com.techidea.weiboapp.adapter.WriteStatusGridImgsAdapter;
import com.techidea.weiboapp.entity.Emotion;
import com.techidea.weiboapp.entity.Status;
import com.techidea.weiboapp.util.DisplayUtils;
import com.techidea.weiboapp.util.StringUtils;
import com.techidea.weiboapp.util.TitleBuilder;
import com.techidea.weiboapp.widget.WrapHeightGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2015/8/9.
 */
public class WriteStatusActivity  extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener{

    //输入框
    private EditText et_write_status;
    //添加的九宫格图片
    private WrapHeightGridView gv_write_status;
    //转发微博内容
    private View include_retweeted_status_card;
    private ImageView iv_rstatus_img;
    private TextView tv_rstatus_username;
    private TextView tv_rstatus_content;

    //底部添加栏
    private ImageView iv_image;
    private ImageView iv_at;
    private ImageView iv_topis;
    private ImageView iv_emoji;
    private ImageView iv_add;

    //表情选择面板
    private LinearLayout ll_emotion_dashboard;
    private ViewPager vp_emotion_dashboard;

    //进度框
    private ProgressDialog progressDialog;

    private WriteStatusGridImgsAdapter statusImgsAdapter;
    private ArrayList<Uri> imgUris = new ArrayList<Uri>();
    private EmotionPagerAdapter emotionPagerGvAdapter;

    //引用微博
    private Status retweeted_status;
    //显示在页面中 实际需要转发内容的微博
    private Status cardStatus;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_status);
        retweeted_status = (Status)getIntent().getSerializableExtra("status");
        initView();
    }

    private void initView(){
        //标题栏
        new TitleBuilder(this)
                .setTitleText("发微博")
                .setLeftText("取消")
                .setLeftOnClickListener(this)
                .setRightText("发送")
                .setRightOnClickListener(this);
        //输入框
        et_write_status = (EditText)findViewById(R.id.et_write_status);
        //添加九宫格图片
        gv_write_status = (WrapHeightGridView)findViewById(R.id.gv_write_status);
        //转发微博内容
        include_retweeted_status_card = findViewById(R.id.include_retweeted_status_card);
        iv_rstatus_img = (ImageView)findViewById(R.id.iv_rstatus_img);
        tv_rstatus_username = (TextView)findViewById(R.id.tv_rstatus_username);
        tv_rstatus_content = (TextView)findViewById(R.id.tv_rstatus_content);

        //底部添加栏
        iv_image = (ImageView)findViewById(R.id.iv_image);
        iv_at = (ImageView)findViewById(R.id.iv_at);
        iv_topis = (ImageView)findViewById(R.id.iv_topic);
        iv_emoji = (ImageView)findViewById(R.id.iv_emoji);
        iv_add = (ImageView)findViewById(R.id.iv_add);

        //表情选择面板
        ll_emotion_dashboard = (LinearLayout)findViewById(R.id.ll_emotion_dashboard);
        vp_emotion_dashboard = (ViewPager)findViewById(R.id.vp_emotion_dashboard);

        //进度框
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("微博发布中...");

        statusImgsAdapter = new WriteStatusGridImgsAdapter(this,imgUris,gv_write_status);
        gv_write_status.setAdapter(statusImgsAdapter);
        gv_write_status.setOnClickListener(this);

        iv_image.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        iv_topis.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);
        iv_add.setOnClickListener(this);

        initRetweetedStatus();
        initEmotion();
    }

    /**
     * 初始化引用微博内容
     */
    private void initRetweetedStatus(){
        //转发微博特殊处理
        if(retweeted_status != null){
            //转发微博是否包含转发内容
            Status rrStatus = retweeted_status.getRetweeted_status();
            if(rrStatus != null){
                String content = "//@" + retweeted_status.getUser().getName()
                        + ":" + retweeted_status.getText();
                et_write_status.setText(StringUtils.getWeiboContent(this,et_write_status,content));
                //如果引用的为转发微博，则使用它转发内容
                cardStatus = rrStatus;
            }else{
                et_write_status.setText("转发微博");
                //如果引用的为转发微博,则使用它自己的转发
                cardStatus = retweeted_status;
            }

            //设置转发图片内容
            String imgUri = cardStatus.getThumbnail_pic();
            if(TextUtils.isEmpty(imgUri)){
                iv_rstatus_img.setVisibility(View.GONE);
            }else{
                iv_rstatus_img.setVisibility(View.VISIBLE);
                imageLoader.displayImage(cardStatus.getThumbnail_pic(),iv_rstatus_img);
            }
            //设置转发文字内容
            tv_rstatus_username.setText("@" + cardStatus.getUser().getName());
            tv_rstatus_content.setText(cardStatus.getText());

            //转发微博时，不能添加图片
            iv_image.setVisibility(View.GONE);
            include_retweeted_status_card.setVisibility(View.VISIBLE);

        }
    }

    private void initEmotion(){
        //获取屏幕宽度
        int gvWidth = DisplayUtils.getScreenWidthPixels(this);
        //表情边距
        int spacing = DisplayUtils.dp2px(this,8);
        //GridView中item的宽度
        int itemWidth = (gvWidth-spacing*8)/7;
        int gvHeight = itemWidth*3 + spacing*4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<String> emotionNames = new ArrayList<String>();
        //遍历所有表情的名字
        for(String emojiName : Emotion.emojiMap.keySet()){
            emotionNames.add(emojiName);
            //每20个表情作为一组，同时添加到ViewPager对应的view集合中
            if(emotionNames.size() == 20){
                GridView gv = createEmotionGridView(emotionNames,gvWidth,spacing,itemWidth,gvHeight);
                gvs.add(gv);
                //添加完一组表情，重新创建一个表情名字集合
                emotionNames = new ArrayList<String>();
            }
        }

        //检查最后是否有不足20个表情的剩余情况
        if(emotionNames.size() > 0){
            GridView gv = CreateEmotionGridView(emotionNames,gvWidth,spacing,itemWidth,gvHeight);
            gvs.add(gv);
        }
        //将多个GridView添加显示到ViewPager中
        emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
        vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth,gvHeight);
        vp_emotion_dashboard.setLayoutParams(params);
    }

    /**
     * 创建显示表情的GridView
     * @param emotionNames
     * @param gvWidth
     * @param padding
     * @param itemWidth
     * @param gvHeight
     * @return
     */
    private GridView createEmotionGridView(List<String> emotionNames,int gvWidth,int padding,int itemWidth,int gvHeight){
        GridView gv = new GridView(this);
        gv.setBackgroundResource(R.color.bg_gray);
        gv.setSelector(R.color.transparent);
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(gvWidth,gvHeight);
        gv.setLayoutParams(params);
        //给GridView设置表情图片
        EmotionGvAdapter adapter = new EmotionGvAdapter(this,emotionNames,itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        return  gv;
    }

    private void updateImgs(){
        if(imgUris.size()>0){
            //如果有图片则显示gridview，同时更新内容
            gv_write_status.setVisibility(View.VISIBLE);
            statusImgsAdapter.notifyDataSetChanged();
        }else{
            //无图片则不显示gridview
            gv_write_status.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.titlebar_iv_left:
                finish();
                break;
            case R.id.titlebar_iv_right:
                sendStatus();
                break;
            case R.id.iv_image:
                DialogUtils.showImagePickDialog(this);
                break;
            case R.id.iv_at:
                break;
            case R.id.iv_topic:
                break;
            case R.id.iv_emoji:
                if(ll_emotion_dashboard.getVisibility() == View.VISIBLE){
                    //显示表情面板时点击，将按钮图片设为笑脸，同时隐藏面板
                    iv_emoji.setImageResource(R.drawable.btn_insert_emotion);
                    ll_emotion_dashboard.setVisibility(View.GONE);
                }else{
                    //未显示表情面板时点击，将按钮图片设为键盘，同时显示面板
                    iv_emoji.setImageResource(R.drawable.btn_insert_keyboard);
                    ll_emotion_dashboard.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_add:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object itemAdapter = adapterView.getAdapter();

        if(itemAdapter instanceof WriteStatusGridImgsAdapter){
            //点击的是添加的图片
            if(position == statusImgsAdapter.getCount() -1){
                //如果点击了最后一个加号图标，则显示选择图片对话框
                DialogUtils.showImagePickDialog(this);
            }
        }else if(itemAdapter instanceof EmotionGvAdapter){
            //点击的是表情
            EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter)itemAdapter；

            if(position == emotionGvAdapter.getCount() -1){
                //如果点击了最后一个回退按钮，则调用删除键事件
                et_write_status.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DEL
                ));
            }else{
                //如果点击了表情，则添加到输入框中
                String emotionName = emotionGvAdapter.getItem(position);

                //获取当前光标位置，在指定位置上添加表情图片文本
                int curPosition = et_write_status.getSelectionStart();
                StringBuilder sb = new StringBuilder(et_write_status.getText().toString());
                sb.insert(curPosition,emotionName);

                //特殊文字处理，将表情等转换一下
                et_write_status.setText(StringUtils.getWeiboContent(this,et_write_status,sb.toString()));

                //将光标设置到新增完表情的右侧
                et_write_status.setSelection(curPosition + emotionName.length());
            }
        }
    }

    private void showIfNeedEditDialog(final Uri imageUri){
        DialogUtils.show
    }
}
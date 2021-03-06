package com.techidea.weiboapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.techidea.weiboapp.R;
import com.techidea.weiboapp.activity.StatusDetailActivity;
import com.techidea.weiboapp.activity.UserInfoActivity;
import com.techidea.weiboapp.activity.WriteCommentActivity;
import com.techidea.weiboapp.activity.WriteStatusActivity;
import com.techidea.weiboapp.entity.PicUrls;
import com.techidea.weiboapp.entity.Status;
import com.techidea.weiboapp.entity.User;
import com.techidea.weiboapp.util.DateUtils;
import com.techidea.weiboapp.util.StringUtils;
import com.techidea.weiboapp.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/30.
 */
public class StatusAdapter extends BaseAdapter {

    private Context context;
    private List<Status> datas;
    private ImageLoader imageLoader;

    public StatusAdapter(Context context, List<Status> datas) {
        this.context = context;
        this.datas = datas;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Status getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(context, R.layout.item_status, null);

            holder.ll_card_content = (LinearLayout) convertView
                    .findViewById(R.id.ll_card_content);

            holder.iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            holder.rl_content = (RelativeLayout) convertView
                    .findViewById(R.id.rl_content);
            holder.tv_subhead = (TextView) convertView
                    .findViewById(R.id.tv_subhead);
            holder.tv_caption = (TextView) convertView
                    .findViewById(R.id.tv_caption);

            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_content);
            holder.include_status_image = (FrameLayout) convertView
                    .findViewById(R.id.include_status_image);
            holder.gv_images = (GridView) holder.include_status_image
                    .findViewById(R.id.gv_images);
            holder.iv_image = (ImageView) holder.include_status_image
                    .findViewById(R.id.iv_image);

            holder.include_retweeted_status = (LinearLayout) convertView
                    .findViewById(R.id.include_retweeted_status);
            holder.tv_retweeted_content = (TextView) holder.include_retweeted_status
                    .findViewById(R.id.tv_retweeted_content);
            holder.include_retweeted_status_image = (FrameLayout) holder
                    .include_retweeted_status.findViewById(R.id.include_status_image);
            holder.gv_retweeted_images = (GridView) holder.include_retweeted_status_image
                    .findViewById(R.id.gv_images);
            holder.iv_retweeted_image = (ImageView) holder.include_retweeted_status_image
                    .findViewById(R.id.iv_image);

            holder.ll_share_bottom = (LinearLayout) convertView.findViewById(
                    R.id.ll_share_bottom);
            holder.iv_share_bottom = (ImageView) convertView.findViewById(R.id.iv_share_bottom);
            holder.tv_share_bottom = (TextView) convertView.findViewById(R.id.tv_share_bottom);

            holder.ll_comment_bottom = (LinearLayout) convertView.findViewById(R.id.ll_comment_bottom);
            holder.iv_comment_bottom = (ImageView) convertView.findViewById(R.id.iv_comment_bottom);
            holder.tv_comment_bottom = (TextView) convertView.findViewById(R.id.tv_comment_bottom);

            holder.ll_like_bottom = (LinearLayout) convertView.findViewById(R.id.ll_like_bottom);
            holder.cb_like_bottom = (CheckBox) convertView.findViewById(R.id.cb_like_bottom);
            holder.tv_like_bottom = (TextView) convertView.findViewById(R.id.tv_like_bottom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //bind data
        final Status status = getItem(position);
        final User user = status.getUser();
        //报空指针异常
        imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar);
        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("userName", user.getName());
                context.startActivity(intent);
            }
        });

        holder.tv_subhead.setText(user.getName());
        holder.tv_subhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("userName", user.getName());
                context.startActivity(intent);
            }
        });

        holder.tv_caption.setText(DateUtils.getShortTime(status.getCreated_at())
                + " 来自 " + Html.fromHtml(status.getSource()));
//        holder.tv_content.setText(status.getText());
        holder.tv_content.setText(StringUtils
                .getWeiboContent(context, holder.tv_content, status.getText()));

        setImages(status, holder.include_status_image, holder.gv_images, holder.iv_image);

        final Status retweeted_status = status.getRetweeted_status();
        if (retweeted_status != null) {

            User retUser = retweeted_status.getUser();

            holder.include_retweeted_status.setVisibility(View.VISIBLE);
            String retweetedContent = "@" + retUser.getName() + ":" +
                    retweeted_status.getText();
            holder.tv_retweeted_content.setText(StringUtils
                    .getWeiboContent(context, holder.tv_retweeted_content, retweetedContent));

            setImages(retweeted_status,
                    holder.include_retweeted_status_image,
                    holder.gv_retweeted_images, holder.iv_retweeted_image);

            holder.include_retweeted_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, StatusDetailActivity.class);
                    intent.putExtra("status", retweeted_status);
                    context.startActivity(intent);
                }
            });
        } else {
            holder.include_retweeted_status.setVisibility(View.GONE);
        }

        holder.tv_share_bottom.setText(status.getReposts_count() == 0 ?
                "转发" : status.getComments_count() + "");
        holder.cb_like_bottom.setChecked(status.isLiked());


        holder.tv_comment_bottom.setText(status.getComments_count() == 0 ?
                "评论" : status.getComments_count() + "");
        holder.tv_like_bottom.setText(status.getAttitudes_count() == 0 ?
                "赞" : status.getAttitudes_count() + "");


        holder.ll_card_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StatusDetailActivity.class);
                intent.putExtra("status", status);
                context.startActivity(intent);
            }
        });

        holder.include_retweeted_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StatusDetailActivity.class);
                intent.putExtra("status", retweeted_status);
                context.startActivity(intent);
            }
        });

        holder.ll_share_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WriteStatusActivity.class);
                intent.putExtra("status", status);
                context.startActivity(intent);
            }
        });

        holder.ll_comment_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.getComments_count() > 0) {
                    Intent intent = new Intent(context, StatusDetailActivity.class);
                    intent.putExtra("status", status);
                    intent.putExtra("scroll2Comment", true);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, WriteCommentActivity.class);
                    intent.putExtra("status", status);
                    context.startActivity(intent);
                }
                ToastUtils.showToast(context, "评论", Toast.LENGTH_SHORT);
            }
        });

        holder.ll_like_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.5f, 1f, 1.5f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation2.setDuration(150);

                ScaleAnimation scaleAnimation1 = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation1.setDuration(200);
                scaleAnimation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.cb_like_bottom.setChecked(!holder.cb_like_bottom.isChecked());
                        holder.cb_like_bottom.setAnimation(scaleAnimation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                holder.cb_like_bottom.setAnimation(scaleAnimation1);
            }
        });
        return convertView;
    }

    private void setImages(Status status, FrameLayout imgContainer,
                           GridView gv_images, ImageView iv_image) {
        ArrayList<PicUrls> pic_urls = status.getPic_urls();
        String thumbnail_pic = status.getThumbnail_pic();

        if (pic_urls != null && pic_urls.size() > 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.VISIBLE);
            iv_image.setVisibility(View.GONE);

            StatusGridImgsAdapter gvAdapter = new StatusGridImgsAdapter(context, pic_urls);
            gv_images.setAdapter(gvAdapter);
        } else if (thumbnail_pic != null) {
            imgContainer.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.GONE);
            iv_image.setVisibility(View.VISIBLE);
            imageLoader.displayImage(thumbnail_pic, iv_image);
        } else {
            imgContainer.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder {

        public LinearLayout ll_card_content;
        public ImageView iv_avatar;
        public RelativeLayout rl_content;
        public TextView tv_subhead;
        public TextView tv_caption;

        public TextView tv_content;
        public FrameLayout include_status_image;
        public GridView gv_images;
        public ImageView iv_image;

        public LinearLayout include_retweeted_status;
        public TextView tv_retweeted_content;
        public FrameLayout include_retweeted_status_image;
        public GridView gv_retweeted_images;
        public ImageView iv_retweeted_image;

        public LinearLayout ll_share_bottom;
        public ImageView iv_share_bottom;
        public TextView tv_share_bottom;
        public LinearLayout ll_comment_bottom;
        public ImageView iv_comment_bottom;
        public TextView tv_comment_bottom;
        public LinearLayout ll_like_bottom;
        public CheckBox cb_like_bottom;
        public TextView tv_like_bottom;
    }
}

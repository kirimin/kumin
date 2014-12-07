package me.kirimin.kumin.ui.adapter;

import me.kirimin.kumin.R;
import me.kirimin.kumin.model.Tweet;
import me.kirimin.kumin.twitter.TwitterUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * タイムラインをリスト表示するAdapter
 */
public class TimeLineListViewAdapter extends ArrayAdapter<Tweet> {

    public TimeLineListViewAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_timeline, null);
            holder = new ViewHolder();
            holder.textTweet = (TextView) convertView.findViewById(R.id.timeLineTextTweet);
            holder.textUserName = (TextView) convertView.findViewById(R.id.timeLineTextUserName);
            holder.textScreenName = (TextView) convertView.findViewById(R.id.timeLineTextId);
            holder.textTweetTime = (TextView) convertView.findViewById(R.id.timeLineTextTime);
            holder.imageIcon = (ImageView) convertView.findViewById(R.id.timeLineImageIcon);
            holder.imageIcon.setAlpha(200);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textTweet.setText(tweet.getText());
        holder.textUserName.setText(tweet.getName());
        holder.textScreenName.setText("@" + tweet.getScreenName());
        holder.textTweetTime.setText(TwitterUtil.getTimeStamp(tweet.getCreatedAt()));

        Picasso.with(getContext()).load(tweet.getIconUrl())
                .error(R.drawable.no_image)
                .resize(48, 48)
                .into(holder.imageIcon);
        return convertView;
    }

    private static class ViewHolder {
        TextView textTweet;
        TextView textUserName;
        TextView textScreenName;
        TextView textTweetTime;
        ImageView imageIcon;
    }
}
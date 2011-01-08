/**
 * 
 */
package com.ch_linghu.android.fanfoudroid.ui.widget;

import java.text.ParseException;
import java.util.Date;

import com.ch_linghu.android.fanfoudroid.R;
import com.ch_linghu.android.fanfoudroid.TwitterApplication;
import com.ch_linghu.android.fanfoudroid.R.id;
import com.ch_linghu.android.fanfoudroid.R.layout;
import com.ch_linghu.android.fanfoudroid.data.Tweet;
import com.ch_linghu.android.fanfoudroid.data.db.TwitterDbAdapter;
import com.ch_linghu.android.fanfoudroid.helper.Utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetCursorAdapter extends CursorAdapter implements TweetAdapter {
	private static final String TAG = "TweetCursorAdapter";

	public TweetCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor);

		if (context != null) {
			mInflater = LayoutInflater.from(context);
		}

		if (cursor != null) {
			mUserTextColumn = cursor
					.getColumnIndexOrThrow(TwitterDbAdapter.KEY_USER);
			mTextColumn = cursor
					.getColumnIndexOrThrow(TwitterDbAdapter.KEY_TEXT);
			mProfileImageUrlColumn = cursor
					.getColumnIndexOrThrow(TwitterDbAdapter.KEY_PROFILE_IMAGE_URL);
			mCreatedAtColumn = cursor
					.getColumnIndexOrThrow(TwitterDbAdapter.KEY_CREATED_AT);
			mSourceColumn = cursor
					.getColumnIndexOrThrow(TwitterDbAdapter.KEY_SOURCE);
			mInReplyToScreenName = cursor
					.getColumnIndexOrThrow(TwitterDbAdapter.KEY_IN_REPLY_TO_SCREEN_NAME);
			mFavorited = cursor
					.getColumnIndexOrThrow(TwitterDbAdapter.KEY_FAVORITED);
		}
		mMetaBuilder = new StringBuilder();
	}

	private LayoutInflater mInflater;

	private int mUserTextColumn;
	private int mTextColumn;
	private int mProfileImageUrlColumn;
	private int mCreatedAtColumn;
	private int mSourceColumn;
	private int mInReplyToScreenName;
	private int mFavorited;

	private StringBuilder mMetaBuilder;

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.tweet, parent, false);

		TweetCursorAdapter.ViewHolder holder = new ViewHolder();
		holder.tweetUserText = (TextView) view
				.findViewById(R.id.tweet_user_text);
		holder.tweetText = (TextView) view.findViewById(R.id.tweet_text);
		holder.profileImage = (ImageView) view.findViewById(R.id.profile_image);
		holder.metaText = (TextView) view.findViewById(R.id.tweet_meta_text);
		holder.fav = (ImageView) view.findViewById(R.id.tweet_fav);
		view.setTag(holder);

		return view;
	}

	private static class ViewHolder {
		public TextView tweetUserText;
		public TextView tweetText;
		public ImageView profileImage;
		public TextView metaText;
		public ImageView fav;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TweetCursorAdapter.ViewHolder holder = (TweetCursorAdapter.ViewHolder) view
				.getTag();

		holder.tweetUserText.setText(cursor.getString(mUserTextColumn));
		Utils.setTweetText(holder.tweetText, cursor.getString(mTextColumn));

		String profileImageUrl = cursor.getString(mProfileImageUrlColumn);

		if (!Utils.isEmpty(profileImageUrl)) {
			holder.profileImage.setImageBitmap(TwitterApplication.mImageManager
					.get(profileImageUrl));
		}

		if (cursor.getString(mFavorited).equals("true")) {
			holder.fav.setVisibility(View.VISIBLE);
		} else {
			holder.fav.setVisibility(View.INVISIBLE);
		}

		try {
			Date createdAt = TwitterDbAdapter.DB_DATE_FORMATTER.parse(cursor
					.getString(mCreatedAtColumn));
			holder.metaText.setText(Tweet.buildMetaText(mMetaBuilder,
					createdAt, cursor.getString(mSourceColumn), cursor
							.getString(mInReplyToScreenName)));
		} catch (ParseException e) {
			Log.w(TAG, "Invalid created at data.");
		}
	}

	@Override
	public void refresh() {
		getCursor().requery();
	}
}
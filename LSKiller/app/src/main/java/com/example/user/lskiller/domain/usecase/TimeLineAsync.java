package com.example.user.lskiller.domain.usecase;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.user.lskiller.R;
import com.example.user.lskiller.presentation.view.adapter.RecyclerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by USER on 2016/10/09.
 */
public class TimeLineAsync extends AsyncTask<Void, Void, List<Status>> {

    private Twitter mTwitter;
    private List<twitter4j.Status> statuses = new ArrayList<>();
    private ArrayList<String> mediaList = new ArrayList<String>();
    private Activity activity;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Parcelable recyclerViewState;
    private int endPage = 40;
    private User user;
    private boolean isUser;

    public TimeLineAsync(
            Twitter mTwitter,
            Activity activeView,
            RecyclerView recyclerView,
            User user,
            boolean isUser
    ) {
        this.mTwitter = mTwitter;
        this.activity = activeView;
        this.recyclerView = recyclerView;
        this.isUser = isUser;
        this.user = user;
    }

    public TimeLineAsync(
            Twitter mTwitter,
            Activity activeView,
            RecyclerView recyclerView,
            int endPage,
            User user,
            boolean isUser
    ) {
        this.mTwitter = mTwitter;
        this.activity = activeView;
        this.recyclerView = recyclerView;
        this.endPage = endPage;
        this.isUser = isUser;
        this.user = user;
    }

    @Override
    public void onPreExecute() {
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        progressBar = (ProgressBar) activity.findViewById(R.id.progress);
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected List<twitter4j.Status> doInBackground(Void... voids) {
        try {
            // 1ページ当たりの取得ツイート数
            Paging page = new Paging(1, endPage);

            if (isUser) {
                return mTwitter.getUserTimeline(user.getId(), page);
            } else {
                return mTwitter.getHomeTimeline(page);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<twitter4j.Status> result) {
        progressBar.setVisibility(ProgressBar.GONE);
        if (result != null) {
            mediaList.clear();
            statuses.clear();
            int count = 1;
            for (twitter4j.Status status : result) {
                statuses.add(status);
                // mediaEntities
                MediaEntity[] mediaEntities = status.getExtendedMediaEntities();
                if (mediaEntities.length > 0) {
                    for (MediaEntity mediaResult : mediaEntities) {
                        mediaList.add(mediaResult.getMediaURL());
                    }
                } else {
                    mediaList.add(null);
                }
                count++;
            }
            RecyclerAdapter adapter = new RecyclerAdapter(
                    activity,
                    statuses,
                    (OnRecyclerListener) activity);
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            recyclerView.setAdapter(adapter);
        } else {
            final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
                    "タイムラインの取得に失敗しました\n" +
                            "時間を置いてから再度起動してください",
                    Snackbar.LENGTH_INDEFINITE);
            View view = snackbar.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }
}


package com.example.parseinstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.parseinstagram.Post;
import com.example.parseinstagram.R;
import com.example.parseinstagram.TimelineAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment {
    public static final String TAG = "TimelineFragment";

    private RecyclerView rvTimeline;
    private SwipeRefreshLayout swipeContainer;
    protected TimelineAdapter timelineAdapter;
    protected List<Post> mPosts;
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_timeline, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvTimeline = view.findViewById(R.id.rvTimeline);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mPosts = new ArrayList<>();
                timelineAdapter.clear();
                queryPosts();
                timelineAdapter.addAll(mPosts);
            }
        });

        //1. create the datasource
        mPosts = new ArrayList<>();
        //2. create the adapter
        timelineAdapter = new TimelineAdapter(getContext(), mPosts);
        //3. set the adapter on the recyclerview
        rvTimeline.setAdapter(timelineAdapter);
        //4. set the layout manager
        rvTimeline.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts();
    }

    protected void queryPosts() {
        ParseQuery<Post> postParseQuery = new ParseQuery<Post>(Post.class);
        postParseQuery.include(Post.KEY_USER);
        postParseQuery.setLimit(20);
        postParseQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postParseQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                Log.i(TAG, "querying posts");
                if(e == null){
                    mPosts.addAll(objects);
                    timelineAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

}

package com.example.parseinstagram.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    private RecyclerView rvTimeline;
    private SwipeRefreshLayout swipeContainer;
    protected TimelineAdapter timelineAdapter;
    protected List<Post> mPosts;
    private Button btnLogout;

    private OnItemSelectedListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvTimeline = view.findViewById(R.id.rvTimeline);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        btnLogout = view.findViewById(R.id.btnLogout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mPosts = new ArrayList<>();
                timelineAdapter.clear();
                queryPosts();
                timelineAdapter.addAll(mPosts);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSomeClick(v);
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
        postParseQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postParseQuery.setLimit(20);
        postParseQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void logout();
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    // Now we can fire the event when the user selects something in the fragment
    public void onSomeClick(View v) {
        listener.logout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(getContext(), "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}

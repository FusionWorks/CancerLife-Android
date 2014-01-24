package com.platforms.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.platforms.objects.Comment;
import com.platforms.adapter.CommentsAdapter;
import com.platforms.async.ATComments;
import com.platforms.R;
import com.platforms.utils.Endpoints;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/14/13.
 */
public class CommentsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        RelativeLayout loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        int postId = extras.getInt("postId");
        String endPoint = Endpoints.comments + postId;
        ATComments ATC = new ATComments(endPoint, this, loading);
        ATC.execute();
    }

    public void hadleResponse(ArrayList<Comment> data){
        initiateComments(data);
    }

    public void initiateComments(ArrayList<Comment> data){
        ListView usersList = (ListView)findViewById(R.id.commentsList);
        CommentsAdapter userAdapter = new CommentsAdapter(this, data);
        usersList.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }
}

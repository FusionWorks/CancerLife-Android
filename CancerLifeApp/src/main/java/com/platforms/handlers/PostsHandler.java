package com.platforms.handlers;

import com.platforms.objects.PostItem;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/9/13.
 */
public class PostsHandler {
    ArrayList<PostItem> posts;

    public ArrayList<PostItem> getPosts(){

        return posts;
    }

    public void setPosts(ArrayList<PostItem> posts){
        this.posts=posts;

    }

}
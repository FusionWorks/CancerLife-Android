package com.platforms.handlers;

import com.platforms.objects.Comment;

import java.util.ArrayList;


public class CommentHandler {
    ArrayList<Comment> comments;

    public ArrayList<Comment> getComments(){
        return comments;
    }

    public void setUsers(ArrayList<Comment> comments){
        this.comments=comments;

    }


}

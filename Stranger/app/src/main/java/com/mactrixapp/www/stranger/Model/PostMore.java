package com.mactrixapp.www.stranger.Model;

public class PostMore {

    Post post;
    String postkey;

    public PostMore(Post post, String postkey) {
        this.post = post;
        this.postkey = postkey;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }
}

package main.java;

import java.util.Date;
import java.sql.*;

public class Listentry {
    public String username;
    public Integer postid;
    public String title;
    public String body;
    public String modified;
    public String created;

    /**
     * Post() - Username and postid required.
     */
    public Listentry() {
        this.username = null;
        this.postid = 0;
        this.title = null;
        this.body = null;
        this.modified = null;
        this.created = null;
    }

    public String getUsername() {
        return this.username;
    }

    public Integer getPostid() {
        return this.postid;
    }

    public String getTitle() {
        return this.title;
    }

    public String getBody() {
        return this.body;
    }

    public String getModified() {
        return this.modified;
    }

    public String getCreated() {
        return this.created;
    }
}

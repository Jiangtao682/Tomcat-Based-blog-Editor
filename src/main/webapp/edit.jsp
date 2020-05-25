<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Post</title>
    <link rel="stylesheet" type="text/css" href="css/background.css">
    <link rel="stylesheet" type="text/css" href="css/font.css">
    <link rel="stylesheet" type="text/css" href="css/edit.css">
    <link rel="stylesheet" type="text/css" href="css/button.css">
</head>
<body>
    <div><h1>Edit Post</h1></div>
    <form action="post" method="POST">
        <div class='flex'>
            <button type="submit" name="action" value="save">Save</button>
            <button type="submit" name="action" value="list">Close</button>
            <button type="submit" name="action" value="preview">Preview</button>
            <button type="submit" name="action" value="delete">Delete</button>
        </div>
        <input type="hidden" name="username" value='<%= request.getParameter("username") %>'>
        <input type="hidden" name="postid" value='<%= request.getParameter("postid") %>'>
        <div class='titleInEditor'>
            <div class="heading"><label for="title">Title</label></div>
            <input type="text" style="height: 1rem; width: 30rem" id="title" name="title" value='<%= request.getAttribute("title")%>'>
        </div>
        <div class='bodyInEditor'>
            <div class="heading"><label for="body">Body</label></div>
            <textarea style="height: 20rem; width: 30rem" id="body" name="body"><%= request.getAttribute("body")%></textarea>
        </div>
    </form>
</body>
</html>

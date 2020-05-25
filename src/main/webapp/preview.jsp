<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Preview Post</title>
    <link rel="stylesheet" type="text/css" href="css/background.css">
    <link rel="stylesheet" type="text/css" href="css/preview.css">
    <link rel="stylesheet" type="text/css" href="css/button.css">
</head>

<body>
<div class="flex">
    <form action="post" method="POST">
        <input type="hidden" name="username" value='<%= request.getParameter("username") %>'>
        <input type="hidden" name="postid" value='<%= request.getParameter("postid") %>'>
        <input type="hidden" name="title" value='<%= request.getParameter("title") %>'>
        <input type="hidden" name="body" value='<%= request.getParameter("body") %>'>
        <button class="flex_item" type="submit" name="action" value="open">Close Preview</button>
    </form>
</div>
<div>
    <h1 id="title"><p><%= request.getAttribute("Md_Title") %></p></h1>
    <div id="preview"><p><%= request.getAttribute("Md_Body") %></p></div>
</div>
</body>
</html>
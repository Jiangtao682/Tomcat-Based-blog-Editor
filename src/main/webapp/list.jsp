<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<%@ page import="java.util.ArrayList, main.java.Listentry" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>Post List</title>
    <link rel="stylesheet" type="text/css" href="css/background.css">
    <link rel="stylesheet" type="text/css" href="css/button.css">
    <link rel="stylesheet" type="text/css" href="css/font.css">
    <link rel="stylesheet" type="text/css" href="css/list.css">
</head>
<body>
<div>
    <h1>Post List</h1>
    <form action="post" id="0" method="POST">
        <div class='flex'>
            <input type="hidden" name="username" value=<%= request.getParameter("username") %>>
            <input type="hidden" name="postid" value="0">
            <input type="hidden" name="newpost" value="1">
            <div>
                <button id="newPost" type="submit" name="action" value="open">New Post</button>
            </div>
        </div>
    </form>
</div>
<table align="left|center|center|center">
    <tr>
        <th>Title</th>
        <th>Created</th>
        <th>Modified</th>
        <th>Operation</th>
    </tr>
    <% ArrayList<Listentry> list = (ArrayList<Listentry>)request.getAttribute("list"); %>
    <c:forEach items="${list}" var="entry">
        <tr>
            <form action="post" method="POST">
                <td id="title"><c:out value="${entry.getTitle()}" /></td>
                <td><c:out value="${entry.getCreated()}" /></td>
                <td><c:out value="${entry.getModified()}" /></td>
                <input type="hidden" name="username" value="<c:out value="${entry.getUsername()}"/>" />
                <input type="hidden" name="postid"   value="<c:out value="${entry.getPostid()}"/>" />
                <input type="hidden" name="newpost" value="0">
                <td>
                    <button id="open" type="submit" name="action" value="open">Open</button><button id="delete" type="submit" name="action" value="delete">Delete</button>
                </td>
            </form>
        </tr>
    </c:forEach>
</table>
</body>
</html>
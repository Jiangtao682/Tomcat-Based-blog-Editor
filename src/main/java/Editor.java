//package main.java;

import java.io.IOException;
import java.rmi.ServerException;
import java.sql.* ;
import java.util.List;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.management.BadAttributeValueExpException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import main.java.Listentry;
import java.util.regex.Pattern;

/**
 * Servlet implementation class for Servlet: ConfigurationTest
 *
 */
public class Editor extends HttpServlet {
    /**
     * The Servlet constructor
     * 
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public Editor() {}

    public void init() throws ServletException
    {
        /*  write any servlet initialization code here or remove this function */
    }
    
    public void destroy()
    {
        /*  write any servlet cleanup code here or remove this function */
    }

    /**
     * Handles HTTP GET requests
     * 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
        try{
            String action = request.getParameter("action");
            switch (action) {
                case "open": {
                    actionOpen(request, response);
                    break;
                }
                case "save": {
                    request.setAttribute("error", "save action do not allow GET method");
                    showError_400(request, response);
                    break;
                }
                case "delete": {
                    request.setAttribute("error", "delete action do not allow GET method");
                    showError_400(request, response);
                }
                case "preview": {
                    actionPreview(request, response);
                    break;
                }
                case "list": {
                    actionList(request, response);
                    break;
                }
                default: {
                    request.setAttribute("error", "invalid action");
                    showError_400(request, response);
                }
            }
        }catch (Exception e){
            request.setAttribute("error", "400 invalid request");
            showError_400(request, response);
        }
    }
    
    /**
     * Handles HTTP POST requests
     * 
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
        try {
            String action = request.getParameter("action");
            switch (action) {
                case "open": {
                    actionOpen(request, response);
                    break;
                }
                case "save": {
                    actionSave(request, response);
                    break;
                }
                case "delete": {
                    actionDelete(request, response);
                    break;
                }
                case "preview": {
                    actionPreview(request, response);
                    break;
                }
                case "list": {
                    actionList(request, response);
                    break;
                }
                default: {
                    request.setAttribute("error", "invalid action");
                    showError_400(request, response);
                }
            }
        }catch (Exception e){
            request.setAttribute("error", "400 invalid request");
            showError_400(request, response);
        }
    }
    public static boolean isNumeric(String str) {
         Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    private void showError_404(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.getRequestDispatcher("/invalid.jsp").forward(request, response);
    }

    private void showError_400(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        request.getRequestDispatcher("/invalid.jsp").forward(request, response);
    }

    private void actionOpen(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String action = request.getParameter("action");
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String username = request.getParameter("username");
        String postid = request.getParameter("postid"); //bug is here!!!!

        if (username == null || postid == null){
            request.setAttribute("error", "400 open action find ivalid username or postid");
            showError_400(request, response);
        } else if (!isNumeric(postid)){
            request.setAttribute("error", "400 postid must be integer");
            showError_400(request, response);
        }else {
            int postid_int = Integer.valueOf(postid);
            if (postid_int <= 0){
                if (title == null && body == null){
                    request.setAttribute("title", "");
                    request.setAttribute("body", "");
                }else{
                    request.setAttribute("title", title != null ? title : "");
                    request.setAttribute("body", body != null ? body : "");
                }
                request.getRequestDispatcher("/edit.jsp").forward(request, response);
            } else{ // postid > 0
                if (title == null && body == null)
                {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException ex) {
                        request.setAttribute("error", "400" + ex);
                        showError_400(request, response);
                        return;
                    }

                    Connection c = null;
                    PreparedStatement s = null;
                    ResultSet rs = null;

                    try {
                        c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", "");
                        s = c.prepareStatement("SELECT * FROM Posts WHERE username = ? AND postid = ?");
                        s.setString(1, username);
                        s.setInt(2, postid_int);
                        rs = s.executeQuery();
                        if (!rs.next()) {
                            request.setAttribute("error", "404 username and postid do not exist in the database");
                            showError_404(request, response);
                        } else {
                            title = rs.getString("title");
                            body = rs.getString("body");
                            request.setAttribute("title", title);
                            request.setAttribute("body", body);
                            request.getRequestDispatcher("/edit.jsp").forward(request, response);
                        }
                    } catch (SQLException ex) {
                        request.setAttribute("error", "SQLException caught");
                        showError_404(request, response);
                    } finally {
                        try {
                            rs.close();
                        } catch (Exception e) { /* ignored */ }
                        try {
                            s.close();
                        } catch (Exception e) { /* ignored */ }
                        try {
                            c.close();
                        } catch (Exception e) { /* ignored */ }
                    }
                }else {
                    request.setAttribute("title", title);
                    request.setAttribute("body", body);
                    request.getRequestDispatcher("/edit.jsp").forward(request, response);
                }
            }
        }
    }


    private void actionSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        String action = request.getParameter("action");
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");
        int postid_int = 0;

        if (username == null || postid == null || title==null || body == null){
            request.setAttribute("error", "400 invalid parameters");
            showError_400(request, response);
        } else if (!isNumeric(postid)){
            request.setAttribute("error", "400 postid must be integer");
            showError_400(request, response);
        }else {
            postid_int = Integer.parseInt(postid);
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                request.setAttribute("error", "400" + ex);
                showError_400(request, response);
                return;
            }
            Connection c = null;
            PreparedStatement s1 = null;
            PreparedStatement s2 = null;
            ResultSet rs = null;

            if (postid_int <= 0) {
                try {
                    c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", "");
                    s1 = c.prepareStatement("SELECT MAX(postid) AS max FROM Posts WHERE username = ?");
                    s1.setString(1, username);
                    rs = s1.executeQuery();
                    if (!rs.next()) { // can not find any entry in the DB.
                        postid_int = 1;
                    } else {
                        postid_int = rs.getInt("max") + 1;
                    } //  if don't close s may cause problem????
                    s2 = c.prepareStatement("INSERT INTO Posts(username, postid, title, body, modified, created) "
                            + "VALUES(?,?,?,?,NOW(),NOW());");
                    s2.setString(1, username);
                    s2.setInt(2, postid_int);
                    s2.setString(3, title);
                    s2.setString(4, body);
                    s2.executeUpdate();
                    actionList(request, response);
                } catch (SQLException ex) {
                    request.setAttribute("error", "SQLException caught");
                    showError_404(request, response);
                } finally {
                    try {
                        rs.close();
                    } catch (Exception e) { /* ignored */ }
                    try {
                        s1.close();
                        s2.close();
                    } catch (Exception e) { /* ignored */ }
                    try {
                        c.close();
                    } catch (Exception e) { /* ignored */ }
                }
            }else{
                try {
                    c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", "");
                    s1 = c.prepareStatement("SELECT * FROM Posts WHERE username = ? AND postid = ?");
                    s1.setString(1, username);
                    s1.setInt(2, postid_int);
                    rs = s1.executeQuery();
                    if (rs.next()) { // the row exit in the DB. update the row
                        s2 = c.prepareStatement("UPDATE Posts SET title=?,body=?,modified=NOW() WHERE username = ? AND postid = ?");
                        s2.setString(1, title);
                        s2.setString(2, body);
                        s2.setString(3, username);
                        s2.setInt(4, postid_int);
                        s2.executeUpdate();
                        actionList(request, response);
                    }
                } catch (SQLException ex) {
                    request.setAttribute("error", "SQLException caught");
                    showError_404(request, response);
                } finally {
                    try {
                        rs.close();
                    } catch (Exception e) { /* ignored */ }
                    try {
                        s1.close();
                        s2.close();
                    } catch (Exception e) { /* ignored */ }
                    try {
                        c.close();
                    } catch (Exception e) { /* ignored */ }
                }
            }
        }
    }


    private void actionList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String username = request.getParameter("username");
        int postid_int = 0;

        if (username == null){
            request.setAttribute("error", "400 open action find ivalid username");
            showError_400(request, response);
        } else {
            List<Listentry> list = new ArrayList<>();
            list = getList(username, request, response);
            request.setAttribute("list",list);
            request.getRequestDispatcher("/list.jsp").forward(request, response);
        }
    }

    private List<Listentry> getList(String username, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            request.setAttribute("error", "400: " + ex);
            showError_400(request, response);
        }
        SimpleDateFormat dateShow = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        Connection c = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        ArrayList<Listentry> list = new ArrayList<>();

        try {
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", "");
            s = c.prepareStatement("SELECT * FROM Posts WHERE username = ?");
            s.setString(1, username);
            rs = s.executeQuery();
            ArrayList<Listentry> listentry = new ArrayList<>();
            while(rs.next()) {
                Listentry row = new Listentry();
                row.username = rs.getString("username");
                row.postid = rs.getInt("postid");
                row.title = rs.getString("title");
                row.body = rs.getString("body");
                row.modified = dateShow.format(rs.getTimestamp("modified"));
                row.created = dateShow.format(rs.getTimestamp("created"));
                list.add(row);
            }
            return list;

        } catch (SQLException ex) {
            request.setAttribute("error", "SQLException caught");
            showError_404(request, response);

        } finally {
            try {
                rs.close();
            } catch (Exception e) { /* ignored */ }
            try {
                s.close();
            } catch (Exception e) { /* ignored */ }
            try {
                c.close();
            } catch (Exception e) { /* ignored */ }
        }
        return list;
    }

    private void actionPreview(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException{
        String action = request.getParameter("action");
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");

        if (username == null || postid == null || title==null || body == null){
            request.setAttribute("error", "400 invalid parameters");
            showError_400(request, response);
        } else if (!isNumeric(postid)){
            request.setAttribute("error", "400 postid must be integer");
            showError_400(request, response);
        }else {
            Parser parser = Parser.builder().build();
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            Node document = parser.parse(title);
            String Md_Title = renderer.render(document);
            document = parser.parse(body);
            String Md_Body = renderer.render(document);
            request.setAttribute("Md_Title", Md_Title);
            request.setAttribute("Md_Body", Md_Body);
            request.getRequestDispatcher("/preview.jsp").forward(request, response);
        }
    }

    private void actionDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");
        int postid_int = 0;

        if (username == null || postid == null){
            request.setAttribute("error", "400 open action find ivalid username or postid");
            showError_400(request, response);
        } else if (!isNumeric(postid)){
            request.setAttribute("error", "400 postid must be integer");
            showError_400(request, response);
        }else {
            postid_int = Integer.parseInt(postid);
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                request.setAttribute("error", "400" + ex);
                showError_400(request, response);
                return;
            }

            Connection c = null;
            PreparedStatement s = null;
            ResultSet rs = null;

            try {
                c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", "");
                s = c.prepareStatement("DELETE FROM Posts WHERE username = ? AND postid = ?");
                s.setString(1, username);
                s.setInt(2, postid_int);
                s.executeUpdate();
                actionList(request, response);
            } catch (SQLException ex) {
                request.setAttribute("error", "SQLException caught");
                showError_404(request, response);
            } finally {
                try {
                    rs.close();
                } catch (Exception e) { /* ignored */ }
                try {
                    s.close();
                } catch (Exception e) { /* ignored */ }
                try {
                    c.close();
                } catch (Exception e) { /* ignored */ }
            }
        }
    }

}
























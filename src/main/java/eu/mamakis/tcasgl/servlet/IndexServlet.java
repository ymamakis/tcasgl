/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.servlet;

import eu.mamakis.tcasgl.db.SqlDbUtils;
import eu.mamakis.tcasgl.greek.train.GreekTrainer;
import eu.mamakis.tcasgl.train.Trainer;
import eu.mamakis.tcasgl.db.Database;
import eu.mamakis.tcasgl.utils.EntityCache;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author gmamakis
 */
public class IndexServlet extends HttpServlet {

    static {
        EntityCache.getInstance();
    }
    private Connection con;
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            Properties props = new Properties();
             props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("tcasgl.properties"));
            out.println("<html>");
            out.println("<head>");
             out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");
            out.println("<title>TCASGL Web Implementation</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>TCASGL classifier and summarizer</h1>");
            out.println("<br/>This is a prototype of TCASGL algorithm for Classification and Summarization in Greek language");
            out.println("<br /> The available categories the system can identify and summarize are: <br />");
            try {
                
                
            Database cm = new Database("jdbc/cognitive");
                con = cm.getConnection();
                DatabaseMetaData dbmd = con.getMetaData();
            ResultSet rs = dbmd.getTables(null,null,null,null);
            //rs.beforeFirst();
                
                
                out.println("<table>");
                while(rs.next()){
                    if(!rs.getString("TABLE_NAME").equalsIgnoreCase("TOTALS")&&!rs.getString("TABLE_NAME").startsWith("SYS")){
                      out.println("<tr><td>"+rs.getString("TABLE_NAME")+"</td></tr>") ;
                    }
                }
                out.println("</table><br/>");
            } catch (SQLException ex) {
                Logger.getLogger(IndexServlet.class.getName()).log(Level.SEVERE, null, ex);
            } 
//            catch (ClassNotFoundException ex) {
//                Logger.getLogger(IndexServlet.class.getName()).log(Level.SEVERE, null, ex);
//            }
            
            out.println("<form method=POST action='/tcasgl/ActionServlet' accept-charset='UTF-8'>");
             out.println("<select name='action'>");
            out.println("<option name='both'>Summarization</option>");
            out.println("<option name='classification'>Classification</option>");
            out.println("</select><br/>");
            out.println("<textarea id='document' name='document'></textarea><br />Percentage (e.g. 20 for 20%)<br /><input type='text' name='percentage' id='percentage'/><br />");
            out.println("<input type='submit'/>");
            out.println("</form>");
            out.println("<br /><br />This application is distributed under the Apache Software Foundation License v2.0");
            out.println("available at  http://www.apache.org/licenses/LICENSE-2.0 . ");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
   
}

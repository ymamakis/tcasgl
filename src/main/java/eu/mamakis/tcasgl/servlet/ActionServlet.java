/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.servlet;

import eu.mamakis.tcasgl.classify.Classifier;
import eu.mamakis.tcasgl.greek.GreekPreProcessor;
import eu.mamakis.tcasgl.greek.GreekStemmer;
import eu.mamakis.tcasgl.greek.train.GreekTrainer;
import eu.mamakis.tcasgl.summarize.Summarizer;
import eu.mamakis.tcasgl.train.Trainer;
import java.io.IOException;
import java.io.PrintWriter;
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
public class ActionServlet extends HttpServlet {

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
         request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            out.println("<html>");
            out.println("<head>");
             out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");
            out.println("<title>Results</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Original Document</h2>");
            String doc = (String)request.getParameter("document");
            out.println("<br/>"+doc);
            Trainer tr = new GreekTrainer();
            
            Classifier classifier = new Classifier(tr, new GreekPreProcessor(), new GreekStemmer());
            
            out.println("<h2>Identified Category</h2><br />"+classifier.classify(doc));
            
            if(request.getParameter("action").equalsIgnoreCase("Summarization")){
                float percentage = Float.parseFloat(request.getParameter("percentage"))/100;
                Summarizer summarizer = new Summarizer(classifier, new GreekStemmer(), new GreekPreProcessor(), percentage);
                out.println("<br /><h2>Summary</h2>");
                out.println(summarizer.summarize(doc));
            }
        
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

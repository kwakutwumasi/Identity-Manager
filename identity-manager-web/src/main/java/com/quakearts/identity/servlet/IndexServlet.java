package com.quakearts.identity.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/index/*")
public class IndexServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4825235863583568830L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		try {
			doPost(req, resp);
		} catch (IOException | ServletException e) {
			log("Unable to process response", e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		try {
			if(req.isUserInRole("IdentityAdmin")) {
				resp.sendRedirect(req.getContextPath()+"/ui/identities.jsf");
			} else {
				resp.sendRedirect(req.getContextPath()+"/us/modify.jsf");
			}
		} catch (IOException e) {
			log("Unable to process response", e);
		}
	}
}

<%
	if(request.isUserInRole("IdentityAdmin")){
		response.sendRedirect(request.getContextPath()+"/ui/identities.jsf");
	} else {
		response.sendRedirect(request.getContextPath()+"/us/modify.jsf");
	}
%>
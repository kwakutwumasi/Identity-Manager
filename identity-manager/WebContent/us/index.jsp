<%
	if(request.isUserInRole("IdentityUser")){
		response.sendRedirect(request.getContextPath()+"/ui/identities.jsf");
	} else {
		response.sendRedirect(request.getContextPath()+"/us/modify.jsf");
	}
%>
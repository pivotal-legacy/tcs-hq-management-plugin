<%--
  ~ Copyright (C) 2009-2015  Pivotal Software, Inc
  ~
  ~ This program is is free software; you can redistribute it and/or modify
  ~ it under the terms version 2 of the GNU General Public License as
  ~ published by the Free Software Foundation.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
  --%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %><%-- 
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%-- 
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%-- 
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%-- 
--%><jsp:include page="top.jsp" />
  

<div id="content-no-nav">
      
   
		   <h1>Applications</h1>
		   <p>Result of the last operation: 'Application undeployed'.</p>         
            <h1>Deployed Applications</h1>
            <table class="bordered-table">
	          <tr>
			   <th>Select</th>
		        <th>Name</th>
		        <th>Status</th>
	          </tr>
	         		
		        <tr>
			     <td> <input type="checkbox" /> </td>
                   <td class="sublevel1-even">docs</td>
				<td class="sublevel1-even">Running</td>
			   </tr>
  <tr>
			     <td> <input type="checkbox" /> </td>
                   <td class="sublevel1-even">examples</td>
				<td class="sublevel1-even">Running</td>
			   </tr>
  <tr>
			     <td> <input type="checkbox" /> </td>
                   <td class="sublevel1-even">host-manager</td>
				<td class="sublevel1-even">Running</td>
			   </tr>
  <tr>
			     <td> <input type="checkbox" /> </td>
                   <td class="sublevel1-even">manager</td>
				<td class="sublevel1-even">Running</td>
			   </tr>
  <tr>
			     <td> <input type="checkbox" /> </td>
                   <td class="sublevel1-even">swf-booking-mvc</td>
				<td class="sublevel1-even">Stopped</td>
			   </tr>



		       	        
 </table>
<br/>
	      <c:if test="${!readOnly}">
  				<input type="submit" value="Start"/>
					<input type="submit" value="Stop"/>
					<input type="submit" value="Reload"/>
					<input type="submit" value="Undeploy"/>
	      </c:if>
		         
              

            <h1>Deploy an Application</h1>
            <p>
            Select an application to upload and deploy to the server. Valid file formats: <em>war</em>.
            </p>
            <form name="uploadForm" action="<c:url value="deploy.htm" />" method="post" enctype="multipart/form-data" onsubmit="return validateUploadForm();">
	            <table class="bordered-table">
	              <tr>
	                <th>Application Location</th>
	                <th></th>
	              </tr>
	 	          <tr>
			          <td colspan="2"><input type="file" name="application" size="80"/></td>
</tr>
<tr>
<td><label for="ContextPath">Context path (optional)</label>   <input type="text" name="ContextPath" id="ContextPath" size="40"/></td>
			        </tr>
</table>

<br/>
<c:if test="${!readOnly}">
	<input type="submit" value="Upload"/>     
</c:if>
            </form>
            
           <jsp:include page="bottom.jsp" />
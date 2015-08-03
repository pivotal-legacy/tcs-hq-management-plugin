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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="/WEB-INF/layouts/top.jsp" />

<div id="content-no-nav">&nbsp;
<div id="breadcrumb"><spring:url value="/app/{settingsId}/"
	var="url">
	<spring:param name="settingsId" value="${settings.humanId}" />
</spring:url> <a href="${fn:escapeXml(url)}">Home</a></div>

<jsp:include page="/WEB-INF/layouts/displayErrors.jsp" /> <jsp:include
	page="/WEB-INF/layouts/saveChanges.jsp" />

<h1>Home</h1>
<jsp:include page="/WEB-INF/layouts/messages.jsp" /> <c:if
	test="${not empty error}">
	<span class="error"><c:out value="${error}" /></span>
</c:if> 

<script type="text/javascript">
<!--
function switchMenu(obj) {
	var el = document.getElementById(obj);
	if ( el.style.display != "none" ) {
		el.style.display = 'none';
	}
	else {
		el.style.display = '';
	}
}
//-->
</script>
<table class="full-width">
	<tr>
		<td>
		<table class="bordered-table full-width">
			<tr>
				<th>Configuration</th>
			</tr>
			<tr>
				<td><spring:url value="/app/{settingsId}/configuration/"
					var="url">
					<spring:param name="settingsId" value="${settings.humanId}" />
				</spring:url> <a href="${fn:escapeXml(url)}">General</a></td>
			</tr>
			<c:if test="${not empty settings.configuration.environment}">
				<tr>
					<td><spring:url
						value="/app/{settingsId}/configuration/startup/" var="url">
						<spring:param name="settingsId" value="${settings.humanId}" />
					</spring:url> <a href="${fn:escapeXml(url)}">Server Start</a></td>
				</tr>
			</c:if>
			<tr>
				<td><spring:url
					value="/app/{settingsId}/configuration/container/" var="url">
					<spring:param name="settingsId" value="${settings.humanId}" />
				</spring:url> <a href="${fn:escapeXml(url)}">Context Container</a></td>
			</tr>
			<tr>
				<td><spring:url
					value="/app/{settingsId}/configuration/defaults-jsp/" var="url">
					<spring:param name="settingsId" value="${settings.humanId}" />
				</spring:url> <a href="${fn:escapeXml(url)}">Server Defaults: JSP</a></td>
			</tr>
			<tr>
				<td><spring:url
					value="/app/{settingsId}/configuration/defaults-static/" var="url">
					<spring:param name="settingsId" value="${settings.humanId}" />
				</spring:url> <a href="${fn:escapeXml(url)}">Server Defaults: Static Content</a>
				</td>
			</tr>
		</table>
		</td>
		<td>
		<table class="bordered-table full-width">
			<tr>
				<th>Resources</th>
			</tr>
			<tr>
				<td><spring:url value="/app/{settingsId}/resources/jdbc/"
					var="url">
					<spring:param name="settingsId" value="${settings.humanId}" />
				</spring:url> <a href="${fn:escapeXml(url)}">JDBC Data Sources</a></td>
			</tr>
		</table>
		<br />
		<table class="bordered-table full-width">
			<tr>
				<th>Services</th>
			</tr>
			<tr>
				<td><spring:url value="/app/{settingsId}/services/" var="url">
					<spring:param name="settingsId" value="${settings.humanId}" />
				</spring:url> <a href="${fn:escapeXml(url)}">Services List</a></td>
			</tr>
		</table>
		</td>
	</tr>
	<%--
    <tr>
      <td colspan="2">
        <br />
        <table class="bordered-table full-width">
          <tr>
            <th>Profile</th>
          </tr>
          <tr>
            <td>
              <p>
                A profile is...
              </p>
              
              <c:if test="${not hasErrors}">
                <spring:url value="/app/{settingsId}/profile/" var="url">
                  <spring:param name="settingsId" value="${settings.humanId}" />
                </spring:url>
                <form:form method="get" action="${fn:escapeXml(url)}">
                  <label>
                    Profile Name:
                    <input type="text" name="name" id="profileName" class="spring-js-text" />
                  </label>
                  <br /><br />
                  <input type="submit" value="Save configuration as profile" />
                </form:form>
                <br /><br />
              </c:if>
              
              <spring:url value="/app/{settingsId}/profile/" var="url">
                <spring:param name="settingsId" value="${settings.humanId}" />
              </spring:url>
              <form:form method="post" action="${fn:escapeXml(url)}" enctype="multipart/form-data">
                Select a <strong>profile.xml</strong> configuration file to load.  You will be able to tweak the settings for pushing to the server.
                <br />
                <br />
                <label>
                  Profile Location
                  <br />
                  <input type="file" name="profile" id="profile" size="80" />
                </label>
                <br />
                <br />
                <input type="submit" value="Upload"/>
              </form:form>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    --%>
	<tr>
		<td colspan="2">
		<table class="bordered-table full-width">
		<tr>
			<th>Advanced</th>
		</tr>
		<tr>
			<td>
				<spring:url value="/app/{settingsId}/revert/" var="url">
					<spring:param name="settingsId" value="${settings.humanId}" />
				</spring:url> <a href="${fn:escapeXml(url)}">Reload settings from server</a>
			</td>
		</tr>
		<tr>
		<!-- <div id="advanced" class="spring-titlePane-open"> -->
		<!-- <h2>Advanced</h2> -->
		<td>
		<spring:url value="/app/{settingsId}/revertToPreviousConfiguration/" var="url">
			<spring:param name="settingsId" value="${settings.humanId}" />
		</spring:url> 
		<a href="${fn:escapeXml(url)}">Revert to previously saved configuration</a> 
		</td>
		</tr>
		<tr>
			<td>
        <a href="#" onclick="switchMenu('uploader');">Upload a configuration file</a>
		<div id="uploader" style="display:none;">
		<table>
			<tr>
			<td>
		<spring:url
			value="/app/{settingsId}/upload/" var="url">
			<spring:param name="settingsId" value="${settings.humanId}" />
		</spring:url> <form:form method="post" action="${fn:escapeXml(url)}"
			enctype="multipart/form-data">
			<br>
			Select a configuration file to upload to the server.
            <br />
			<br />
            Configuration File:
            <label> <select id="fileName" name="fileName"
				class="spring-js-select">
				<option value="/conf/server.xml">server.xml</option>
				<option value="/conf/web.xml">web.xml</option>
				<option value="/conf/context.xml">context.xml</option>
				<option value="/conf/catalina.properties">catalina.properties</option>
				<option value="/conf/logging.properties">logging.properties</option>
			</select> </label>
			<br />
			<br />
			<label> Local Configuration File Location: <br />
			<input type="file" name="file" id="file" size="80" /> </label>
			<br />
			<br />
			<c:if test="${!readOnly}">
				<input type="submit" value="Upload" />
			</c:if>
		</form:form>
		</td></tr>
		</table>
		</div>
		</td>
	</tr>
	<!--	</div> -->
		</td>
	</tr>
</table>
</div>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />

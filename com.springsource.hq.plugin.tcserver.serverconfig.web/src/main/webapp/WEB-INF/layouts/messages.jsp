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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:choose>
  <c:when test="${param.message eq 'saved'}">
    <p class="strong">Configuration saved successfully</p>
  </c:when>
  <c:when test="${param.message eq 'deleted'}">
    <p class="strong">Configuration deleted successfully</p>
   </c:when>
  <c:when test="${param.message eq 'added'}">
    <p class="strong">Configuration added successfully</p>
  </c:when>
  <c:when test="${param.message eq 'reloaded'}">
    <p class="strong">Configuration reloaded</p>
  </c:when>
  <c:when test="${param.message eq 'reverted'}">
    <p class="strong">Configuration reverted to latest backup</p>
  </c:when>
  <c:when test="${param.message eq 'restarted'}">
    <p class="strong">Server restarted</p>
  </c:when>
  <c:when test="${param.message eq 'profile-loaded'}">
    <p class="strong">Profile loaded successfully</p>
  </c:when>
  <c:when test="${param.message eq 'error-loading-profile'}">
    <p class="error">An error occurred loading the profile.  The profile may be corrupt.</p>
  </c:when>
  <c:when test="${param.message eq 'saved-to-server'}">
    <spring:url value="/app/{settingsId}/restart/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <p class="strong">Configuration pushed to server successfully.  The server must be <a href="${fn:escapeXml(url)}">restarted</a> for the changes to take effect.</p>
  </c:when>
</c:choose>


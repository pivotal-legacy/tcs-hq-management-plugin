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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${changePending or restartPending}">
  <br />
  <br />
  
  <table class="bordered-table">
    <tr>
      <td width="16"><img src="<spring:url value="/resources/images/error.png" />"/></td>
      <td>
        <c:if test="${changePending}">
          Changes have been made locally.
          <ul>
            <li>
              <spring:url value="/app/{settingsId}/save/" var="url">
                <spring:param name="settingsId" value="${settings.humanId}" />
              </spring:url>
              <a href="${fn:escapeXml(url)}">Push</a> configuration changes to tc Runtime instance
            </li>
            <li>
              <spring:url value="/app/{settingsId}/revert/" var="url">
                <spring:param name="settingsId" value="${settings.humanId}" />
              </spring:url>
              <a href="${fn:escapeXml(url)}">Undo</a> all changes
            </li>
          </ul>
        </c:if>
        <c:if test="${restartPending}">
          <spring:url value="/app/{settingsId}/restart/" var="url">
            <spring:param name="settingsId" value="${settings.humanId}" />
          </spring:url>
          The server must be <a href="${fn:escapeXml(url)}">restarted</a> for changes to take effect.
        </c:if>
      </td>
    </tr>
  </table>
</c:if>

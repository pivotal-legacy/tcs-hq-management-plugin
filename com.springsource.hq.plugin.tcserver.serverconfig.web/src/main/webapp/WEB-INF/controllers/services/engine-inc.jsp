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

<table class="bordered-table">
  <tr>
    <th colspan="2">Engine Properties</th>
  </tr>
  <tr>
    <td><form:label path="name" cssClass="required">Name</form:label></td>
    <td>
      <form:input path="name" cssClass="spring-js-text spring-js-required" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesenginename"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="name" cssClass="error" />
    </td>
  </tr>
  <c:if test="${not empty hosts}">
    <tr>
      <td><form:label path="defaultHost" cssClass="required">Default Host</form:label></td>
      <td>
        <form:select path="defaultHost" cssClass="spring-js-select">
          <form:options items="${hosts}" itemLabel="name" itemValue="name" />
        </form:select>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesenginedefaultHost"
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="defaultHost" cssClass="error" />
      </td>
    </tr>
  </c:if>
  <tr>
    <td><form:label path="jvmRoute">JVM Route</form:label></td>
    <td>
      <form:input path="jvmRoute" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesenginejvmRoute"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="jvmRoute" cssClass="error" />
    </td>
  </tr>
</table>

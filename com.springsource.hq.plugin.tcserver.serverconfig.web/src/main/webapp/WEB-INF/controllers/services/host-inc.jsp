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

<form:hidden path="id" />
<table class="bordered-table">
  <tr>
    <th colspan="2">Host Properties</th>
  </tr>
  <tr>
    <td><form:label path="name" cssClass="required">Name</form:label></td>
    <td>
      <form:input path="name" cssClass="spring-js-hostname spring-js-required" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-serviceshostname" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="name" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="appBase" cssClass="required">Application Base Directory</form:label></td>
    <td>
      <form:input path="appBase" cssClass="spring-js-text spring-js-required spring-js-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-serviceshostappBase" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="appBase" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="autoDeploy" cssClass="spring-js-boolean" /> Auto Deploy Web Applications</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-serviceshostautoDeploy" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="autoDeploy" cssClass="error" />
      <ul class="recomendation">
        <li>Auto deployment is not recommended</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="deployOnStartup" cssClass="spring-js-boolean" /> Deploy Applications on Startup</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-serviceshostdeployOnStartup" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="deployOnStartup" cssClass="error" />
      <ul class="recomendation">
        <li>Deploy on startup is recommended</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="unpackWARs" cssClass="spring-js-boolean" /> Unpack WARs</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-serviceshostunpackWARs"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="unpackWARs" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="deployXML" cssClass="spring-js-boolean" /> Deploy XML</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-serviceshostworkDir"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="deployXML" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="workDir">Work Directory</form:label></td>
    <td>
      <form:input path="workDir" cssClass="spring-js-text spring-js-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-serviceshostworkDir" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="workDir" cssClass="error" />
    </td>
  </tr>
</table>

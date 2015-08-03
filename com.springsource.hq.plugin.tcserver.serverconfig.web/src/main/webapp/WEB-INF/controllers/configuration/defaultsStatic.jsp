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

<jsp:include page="/WEB-INF/layouts/top.jsp" />

<div id="secondary-navigation">
  <jsp:include page="local-navigation.jsp" />
</div><!-- /secondary-navigation -->

<div id="content">
  &nbsp;
  <div id="breadcrumb">
    <spring:url value="/app/{settingsId}/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Home</a>
    &gt;
    <spring:url value="/app/{settingsId}/configuration/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Configuration</a>
    &gt;
    <spring:url value="/app/{settingsId}/configuration/defaults-static/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Server Defaults: Static Content</a>
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    Server Defaults
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStatic" 
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  <jsp:include page="/WEB-INF/layouts/messages.jsp" />
  
  <spring:url value="/app/{settingsId}/configuration/defaults-static/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <form:form action="${url}" modelAttribute="staticDefaults" method="put">
    <table class="bordered-table">
      <tr>
        <th colspan="2">Static Content</th>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="listings" cssClass="spring-js-boolean" /> Show Directory Listings</label>
          <a class="help-link allow-unsaved-changes" 
	         href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStaticlistings" 
	         target="help">
	         <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="listings" cssClass="error" />
          <ul class="recomendation">
            <li>For security, directory listings are not recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="debug">Debug Level</form:label></td>
        <td>
          <form:input path="debug" cssClass="spring-js-numeric" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStaticdebug" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="debug" cssClass="error" />
          <ul class="recomendation">
            <li>Valid values are 0 - 10. 0 is debugging disabled. 10 is all debugging</li>
            <li>0 is recommended</li>
          </ul>
          <spring:bind path="debug">
            <script type="text/javascript">
              ams.putId("id.staticDefaults.debug", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="fileEncoding">File Encoding</form:label></td>
        <td>
          <form:input path="fileEncoding" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStaticfileEncoding" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="fileEncoding" cssClass="error" />
        </td>
      </tr>
      <tr>
        <td><form:label path="input">Input Buffer Size (B)</form:label></td>
        <td>
          <form:input path="input" cssClass="spring-js-numeric spring-js-unit-B" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStaticinput" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="input" cssClass="error" />
          <ul class="recomendation">
            <li>Larger values, 8,096 B, may improve performance</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="output">Output Buffer Size (B)</form:label></td>
        <td>
          <form:input path="output" cssClass="spring-js-numeric spring-js-unit-B" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStaticoutput" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="output" cssClass="error" />
          <ul class="recomendation">
            <li>Larger values, 8,096 B, may improve performance</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="sendfileSize">Min Sendfile Size (KB)</form:label></td>
        <td>
          <form:input path="sendfileSize" cssClass="spring-js-numeric spring-js-unit-KB" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStaticsendfileSize" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="sendfileSize" cssClass="error" />
          <ul class="recomendation">
            <li>Values less than 48 KB may harm performance</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="readmeFile">Readme File Name</form:label></td>
        <td>
          <form:input path="readmeFile" cssClass="spring-js-text spring-js-wide" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStaticreadmeFile" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="readmeFile" cssClass="error" />
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="readonly" cssClass="spring-js-boolean" /> Read Only</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsStaticreadonly" 
             target=ehelp">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="readonly" cssClass="error" />
          <ul class="recomendation">
            <li>For security, read only is <strong>strongly</strong> recommended</li>
          </ul>
        </td>
      </tr>
    </table>
    <br />
    <c:if test="${!readOnly}">
      <input type="submit" class="spring-js-submit" value="Save"/>
    </c:if>
  </form:form>
</div>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    var debug = ams.getWidgetByKey("id.staticDefaults.debug");
    debug.constraints.min =  0;
    debug.constraints.max = 10;
    debug.rangeMessage = "A value between 0 and 10 is expected";
  });
</script>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />

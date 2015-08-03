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
    <th colspan="2">General</th>
  </tr>
  <tr>
    <td><form:label path="port" cssClass="required">Port</form:label></td>
    <td>
      <form:input path="port" cssClass="spring-js-port spring-js-required" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorport" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="port" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="address">IP Address</form:label></td>
    <td>
      <form:input path="address" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectoraddress" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="address" cssClass="error" />
      <ul class="recomendation">
        <li>Use of a hostname, instead of IP address, is discouraged</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><form:label path="protocol">Protocol</form:label></td>
    <td>
      <form:select path="protocol" cssClass="spring-js-select">
        <form:option label="AJP/Java" value="org.apache.coyote.ajp.AjpProtocol" />
        <form:option label="AJP/APR" value="org.apache.coyote.ajp.AjpAprProtocol" />
      </form:select>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorprotocol" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="protocol" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="maxThreads">Max Threads</form:label></td>
    <td>
      <form:input path="maxThreads" cssClass="spring-js-numeric" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectormaxThreads" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="maxThreads" cssClass="error" />
      <spring:bind path="maxThreads">
        <script type="text/javascript">
          ams.putId("id.ajp-connector.maxThreads", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="connectionTimeout">Connection Timeout (ms)</form:label></td>
    <td>
      <form:input path="connectionTimeout" cssClass="spring-js-numeric spring-js-unit-ms" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorconnectionTimeout" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="connectionTimeout" cssClass="error" />
      <ul class="recomendation">
        <li>3,000 ms recommended</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><form:label path="scheme">Scheme</form:label></td>
    <td>
      <form:select path="scheme" cssClass="spring-js-select">
        <form:option value="http" label="http" />
        <form:option value="https" label="https" />
      </form:select>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorscheme" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="scheme" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="proxyName">Proxy Host</form:label></td>
    <td>
      <form:input path="proxyName" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorproxyName" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="proxyName" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="proxyPort">Proxy Port</form:label></td>
    <td>
      <form:input path="proxyPort" cssClass="spring-js-port" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorproxyPort" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="proxyPort" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="redirectPort">Redirect Port</form:label></td>
    <td>
      <form:input path="redirectPort" cssClass="spring-js-port" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorredirectPort" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="redirectPort" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="requestSecret">Request Secret Keyword</form:label></td>
    <td>
      <form:input path="requestSecret" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorrequestSecret" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="requestSecret" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="requestUseSecret" cssClass="spring-js-boolean" /> Use Request Secret Keyword</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectoruseRequestSecret" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="requestUseSecret" cssClass="error" />
    </td>
  </tr>
</table>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    var maxThreads = ams.getWidgetByKey("id.ajp-connector.maxThreads");
    
    maxThreads.constraints.min = 1;
  });
</script>

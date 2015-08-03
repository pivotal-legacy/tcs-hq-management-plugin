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
    <th colspan="2">Thread Diagnostics</th>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="enabled" cssClass="spring-js-boolean" /> Enable Thread Diagnostics</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesthreadDiagnosticsenableThreadDiagnostics"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="enabled" cssClass="error" />
      <spring:bind path="enabled">
        <script type="text/javascript">
          ams.putId("id.thread-diagnostics.enabled", "${fn:escapeXml(status.expression)}1");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="history">History</form:label></td>
    <td>
      <form:input path="history" cssClass="spring-js-numeric" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesthreadDiagnosticshistory" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="history" cssClass="error" />
      <spring:bind path="history">
        <script type="text/javascript">
          ams.putId("id.thread-diagnostics.history", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="threshold">Threshold (ms)</form:label></td>
    <td>
      <form:input path="threshold" cssClass="spring-js-numeric spring-js-unit-ms" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesthreadDiagnosticsthreshold" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="threshold" cssClass="error" />
      <spring:bind path="threshold">
        <script type="text/javascript">
          ams.putId("id.thread-diagnostics.threshold", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
</table>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    var enabled = ams.getWidgetByKey("id.thread-diagnostics.enabled");
    var history = ams.getWidgetByKey("id.thread-diagnostics.history");
    var threshold = ams.getWidgetByKey("id.thread-diagnostics.threshold");
    
    history.constraints.min = 1;
    threshold.constraints.min = 1;
    
    dojo.connect(enabled, "onClick", enabled, function() {
      if (dojo.attr(enabled, "checked")) {
        ams.setReadOnly([history, threshold], false);
      }
      else {
        ams.setReadOnly([history, threshold], true);
      }
    });
    enabled.onClick();
  });
</script>

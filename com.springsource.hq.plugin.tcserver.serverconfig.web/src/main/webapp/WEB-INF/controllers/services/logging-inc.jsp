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
    <th colspan="2">HTTP Access Logging</th>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="enabled" cssClass="spring-js-boolean" /> Enable Logging</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesloggingenableLogging" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="enabled" cssClass="error" />
      <spring:bind path="enabled">
        <script type="text/javascript">
          ams.putId("id.logging.enabled", "${fn:escapeXml(status.expression)}1");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="directory">Directory</form:label></td>
    <td>
      <form:input path="directory" cssClass="spring-js-text spring-js-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesloggingdirectory" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="directory" cssClass="error" />
      <spring:bind path="directory">
        <script type="text/javascript">
          ams.putId("id.logging.directory", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="pattern">Pattern</form:label></td>
    <td>
      <form:input path="pattern" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesloggingpattern" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="pattern" cssClass="error" />
      <spring:bind path="pattern">
        <script type="text/javascript">
          ams.putId("id.logging.pattern", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="prefix">File Name Prefix</form:label></td>
    <td>
      <form:input path="prefix" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesloggingprefix" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="prefix" cssClass="error" />
      <spring:bind path="prefix">
        <script type="text/javascript">
          ams.putId("id.logging.prefix", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="suffix">File Name Suffix</form:label></td>
    <td>
      <form:input path="suffix" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesloggingsuffix" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="suffix" cssClass="error" />
      <spring:bind path="suffix">
        <script type="text/javascript">
          ams.putId("id.logging.suffix", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="fileDateFormat">File Date Format</form:label></td>
    <td>
      <form:input path="fileDateFormat" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesloggingfileDateFormat"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="fileDateFormat" cssClass="error" />
      <spring:bind path="fileDateFormat">
        <script type="text/javascript">
          ams.putId("id.logging.fileDateFormat", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
</table>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    var enabled = ams.getWidgetByKey("id.logging.enabled");
    var loggingGroup = [
        ams.getWidgetByKey("id.logging.directory"),
        ams.getWidgetByKey("id.logging.pattern"),
        ams.getWidgetByKey("id.logging.prefix"),
        ams.getWidgetByKey("id.logging.suffix"),
        ams.getWidgetByKey("id.logging.fileDateFormat")
      ];
    
    dojo.connect(enabled, "onClick", enabled, function() {
      var fileDateFormatGuideButton = dojo.byId("fileDateFormat-helper-button");
      var patternGuideButton = dojo.byId("pattern-helper-button");
      if (dojo.attr(enabled, "checked")) {
        ams.setReadOnly(loggingGroup, false);
        if (fileDateFormatGuideButton) {
          dojo.attr(fileDateFormatGuideButton, "disabled", false);
        }
        if (patternGuideButton) {
          dojo.attr(patternGuideButton, "disabled", false);
        }
      }
      else {
        ams.setReadOnly(loggingGroup, true);
        if (fileDateFormatGuideButton) {
          dojo.attr(fileDateFormatGuideButton, "disabled", true);
        }
        if (patternGuideButton) {
          dojo.attr(patternGuideButton, "disabled", true);
        }
      }
    });
    enabled.onClick();
  });
</script>

<script type="text/javascript" src="<c:url value="/resources/scripts/loggingPattern.js,/scripts/loggingFileDateFormat.js" />"></script>

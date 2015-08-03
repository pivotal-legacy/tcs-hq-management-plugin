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
    <spring:url value="/app/{settingsId}/configuration/defaults-jsp/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Server Defaults: JSP</a>
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    Server Defaults
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-ServerDefaults%3AJSP" 
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  <jsp:include page="/WEB-INF/layouts/messages.jsp" />
  
  <spring:url value="/app/{settingsId}/configuration/defaults-jsp/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <form:form action="${url}" modelAttribute="jspDefaults" method="put">
    <table class="bordered-table">
      <tr>
        <th colspan="2">JSP</th>
      </tr>
      <tr>
        <td><form:label path="checkInterval">Recompile Check Interval (s)</form:label></td>
        <td>
          <form:input path="checkInterval" cssClass="spring-js-numeric spring-js-unit-s" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspcheckInterval" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="checkInterval" cssClass="error" />
          <ul class="recomendation">
            <li>0 s recommended</li>
          </ul>
          <spring:bind path="checkInterval">
            <script type="text/javascript">
              ams.putId("id.jsp.checkInterval", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="development" cssClass="spring-js-boolean" /> Development Mode</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspdevelopment" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="development" cssClass="error" />
          <ul class="recomendation">
            <li>Development mode is not recommended for production</li>
          </ul>
          <spring:bind path="development">
            <script type="text/javascript">
              ams.putId("id.jsp.development", "${fn:escapeXml(status.expression)}1");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="modificationTestInterval">Modification Test Interval (s)</form:label></td>
        <td>
          <form:input path="modificationTestInterval" cssClass="spring-js-numeric spring-js-unit-s" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspmodificationTestInterval" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="modificationTestInterval" cssClass="error" />
          <ul class="recomendation">
            <li>Recommended use only in development mode</li>
          </ul>
          <spring:bind path="modificationTestInterval">
            <script type="text/javascript">
              ams.putId("id.jsp.modificationTestInterval", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="compiler">Compiler</form:label></td>
        <td>
          <form:input path="compiler" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspcompiler" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="compiler" cssClass="error" />
          <ul class="recomendation">
            <li>Set only to override the default, not recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="compilerTargetVM">Compiler Target VM</form:label></td>
        <td>
          <form:input path="compilerTargetVM" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspcompilerTargetVM" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="compilerTargetVM" cssClass="error" />
          <ul class="recomendation">
            <li>Set only to override the JVM default, not recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="compilerSourceVM">Compiler Source VM</form:label></td>
        <td>
          <form:input path="compilerSourceVM" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspcompilerSourceVM" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="compilerSourceVM" cssClass="error" />
          <ul class="recomendation">
            <li>Set only to override the JVM default, not recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="classdebuginfo" cssClass="spring-js-boolean" /> Compile Class with Debug Information</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspclassdebuginfo" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="classdebuginfo" cssClass="error" />
          <ul class="recomendation">
            <li>Debug information recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="fork" cssClass="spring-js-boolean" /> Fork JSP Page Compile to Separate JVM</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspfork" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="fork" cssClass="error" />
          <ul class="recomendation">
            <li>Not recommended for use with the Ant compiler</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="enablePooling" cssClass="spring-js-boolean" /> Enable Tag Handler Pooling</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspenablePooling" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="enablePooling" cssClass="error" />
          <ul class="recomendation">
            <li>Tag handler pooling recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="ieClassId">Internet Explorer class-id for &lt;jsp:plugin&gt; Tags</form:label></td>
        <td>
          <form:input path="ieClassId" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspieClassId" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="ieClassId" cssClass="error" />
        </td>
      </tr>
      <tr>
        <td><form:label path="javaEncoding">Java File Encoding</form:label></td>
        <td>
          <form:input path="javaEncoding" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspjavaEncoding" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="javaEncoding" cssClass="error" />
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="keepgenerated" cssClass="spring-js-boolean" /> Keep Generated Source Code</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspkeepgenerated" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="keepgenerated" cssClass="error" />
          <ul class="recomendation">
            <li>Recommended to aid debugging</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="mappedfile" cssClass="spring-js-boolean" /> Generate One Print Statement Per Input Line</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspmappedfield" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="mappedfile" cssClass="error" />
          <ul class="recomendation">
            <li>One print statement per line recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="trimSpaces" cssClass="spring-js-boolean" /> Trim Spaces In Template Text</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJsptrimSpaces" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="trimSpaces" cssClass="error" />
          <ul class="recomendation">
            <li>Recommended for improved performance</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="suppressSmap" cssClass="spring-js-boolean" /> Suppress SMAP Information</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspsuppressSmap" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="suppressSmap" cssClass="error" />
          <ul class="recomendation">
            <li>To aid debugging, suppressing SMAP is not recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="dumpSmap" cssClass="spring-js-boolean" /> Dump SMAP Information</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspdumpSmap" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="dumpSmap" cssClass="error" />
          <ul class="recomendation">
            <li>Dumping SMAP is not recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="genStrAsCharArray" cssClass="spring-js-boolean" /> Generate Strings as Char Arrays</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspgenStrAsCharArray" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="genStrAsCharArray" cssClass="error" />
          <ul class="recomendation">
            <li>Recommended for better performance</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="errorOnUseBeanInvalidClassAttribute" cssClass="spring-js-boolean" /> Issue Error For Invalid useBean Class Attribute</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-onfigurationdefaultsJsperrorOnUseBeanInvalidC..." 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="errorOnUseBeanInvalidClassAttribute" cssClass="error" />
          <ul class="recomendation">
            <li>Recommended to aid debugging</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="scratchdir">Scratch Directory</form:label></td>
        <td>
          <form:input path="scratchdir" cssClass="spring-js-text spring-js-extra-wide" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspscratchdir" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="scratchdir" cssClass="error" />
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="xpoweredBy" cssClass="spring-js-boolean" /> Add X-Powered-By Response Header</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationdefaultsJspxpoweredBy" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="xpoweredBy" cssClass="error" />
          <ul class="recomendation">
            <li>For security, adding response header is not recommended</li>
          </ul>
        </td>
      </tr>
    </table>
    <br/>
    <c:if test="${!readOnly}">
      <input type="submit" class="spring-js-submit" value="Save"/>
    </c:if>
  </form:form>
</div>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    var development = ams.getWidgetByKey("id.jsp.development");
    var checkInterval = ams.getWidgetByKey("id.jsp.checkInterval");
    var modificationTestInterval = ams.getWidgetByKey("id.jsp.modificationTestInterval");
    
    dojo.connect(development, "onClick", development, function() {
      if (dojo.attr(development, "checked")) {
        ams.setReadOnly(checkInterval, true);
        ams.setReadOnly(modificationTestInterval, false);
      }
      else {
        ams.setReadOnly(checkInterval, false);
        ams.setReadOnly(modificationTestInterval, true);
      }
    });
    development.onClick();
  });
</script>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />

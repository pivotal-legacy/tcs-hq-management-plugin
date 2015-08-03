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
    <spring:url value="/app/{settingsId}/configuration/startup/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Server Start</a>
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    Server Start Configuration
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-ServerStartConfiguration" 
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  <jsp:include page="/WEB-INF/layouts/messages.jsp" />
  
  <spring:url value="/app/{settingsId}/configuration/startup/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <form:form action="${url}" modelAttribute="environment" method="put">
    <table class="bordered-table">
      <tr>
        <th colspan="3">General</th>
      </tr>
      <tr>
        <td><form:label path="javaHome">Java Home</form:label></td>
        <td>
          <form:input path="javaHome" cssClass="spring-js-text spring-js-extra-wide" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupjavaHome" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="javaHome" cssClass="error" />
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="jvmOptions.general.server" cssClass="spring-js-boolean" /> Use Server HotSpot VM</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupserver" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jvmOptions.general.server" cssClass="error" />
          <ul class="recomendation">
            <li>The server VM is recommended</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td><form:label path="jvmOptions.memory.ms">Min Heap Size (MB)</form:label></td>
        <td>
          <form:input path="jvmOptions.memory.ms" cssClass="spring-js-numeric spring-js-unit-MB" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupms" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jvmOptions.memory.ms" cssClass="error" />
          <spring:bind path="jvmOptions.memory.ms">
            <script type="text/javascript">
              ams.putId("id.jvmOptions.memory.ms", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jvmOptions.memory.mx">Max Heap Size (MB)</form:label></td>
        <td>
          <form:input path="jvmOptions.memory.mx" cssClass="spring-js-numeric spring-js-unit-MB" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupmx" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jvmOptions.memory.mx" cssClass="error" />
          <ul class="recomendation">
            <li>32-bit OS, 32-bit JVM: 256 MB - 1,024 MB recommended</li>
            <li>64-bit OS, 32-bit JVM: 256 MB - 1,600 MB recommended</li>
            <li>64-bit OS, 64-bit JVM: 256 MB - 3,000 MB recommended</li>
          </ul>
          <spring:bind path="jvmOptions.memory.mx">
            <script type="text/javascript">
              ams.putId("id.jvmOptions.memory.mx", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jvmOptions.memory.ss">Thread Stack Size (KB)</form:label></td>
        <td>
          <form:input path="jvmOptions.memory.ss" cssClass="spring-js-numeric spring-js-unit-KB" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupss" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
         <form:errors path="jvmOptions.memory.ss" cssClass="error" />
          <ul class="recomendation">
            <li>192 KB recommended</li>
          </ul>
          <spring:bind path="jvmOptions.memory.ss">
            <script type="text/javascript">
              ams.putId("id.jvmOptions.memory.ss", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
    </table>
    <br />
    <div id="statup-sun" class="spring-titlePane-open bordered-table">
      <h2>Sun Specific JVM Options</h2>
      <table>
        <tr>
          <td colspan="2">
            The following options are specific to Sun virtual machines.
            These values may cause unintended side effects if running on another vendor's VM.
          </td>
        </tr>
        <tr>
          <td colspan="2"><h3>Memory</h3></td>
        </tr>
        <tr>
          <td><form:label path="jvmOptions.memory.newSize">Min Young Generation Size (MB)</form:label></td>
          <td>
            <form:input path="jvmOptions.memory.newSize" cssClass="spring-js-numeric spring-js-unit-MB" />
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupnewSize" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.memory.newSize" cssClass="error" />
            <ul class="recomendation">
              <li>20% - 50% of Min Heap Size recommended</li>
            </ul>
          </td>
          <spring:bind path="jvmOptions.memory.newSize">
            <script type="text/javascript">
              ams.putId("id.jvmOptions.memory.newSize", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </tr>
        <tr>
          <td><form:label path="jvmOptions.memory.maxNewSize">Max Young Generation Size (MB)</form:label></td>
          <td>
            <form:input path="jvmOptions.memory.maxNewSize" cssClass="spring-js-numeric spring-js-unit-MB" />
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupmaxNewSize" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.memory.maxNewSize" cssClass="error" />
            <ul class="recomendation">
              <li>20% - 50% of Max Heap Size recommended</li>
            </ul>
          <spring:bind path="jvmOptions.memory.maxNewSize">
            <script type="text/javascript">
              ams.putId("id.jvmOptions.memory.maxNewSize", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
          </td>
        </tr>
        <tr>
          <td><form:label path="jvmOptions.memory.permSize">Min Perm Gen Size (MB)</form:label></td>
          <td>
            <form:input path="jvmOptions.memory.permSize" cssClass="spring-js-numeric spring-js-unit-MB" />
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartuppermSize" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.memory.permSize" cssClass="error" />
            <spring:bind path="jvmOptions.memory.permSize">
              <script type="text/javascript">
                ams.putId("id.jvmOptions.memory.permSize", "${fn:escapeXml(status.expression)}");
              </script>
            </spring:bind>
          </td>
        </tr>
        <tr>
          <td><form:label path="jvmOptions.memory.maxPermSize">Max Perm Gen Size (MB)</form:label></td>
          <td>
            <form:input path="jvmOptions.memory.maxPermSize" cssClass="spring-js-numeric spring-js-unit-MB" />
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupmaxPermSize" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.memory.maxPermSize" cssClass="error" />
            <ul class="recomendation">
              <li>64 MB - 512 MB recommended</li>
            </ul>
            <spring:bind path="jvmOptions.memory.maxPermSize">
              <script type="text/javascript">
                ams.putId("id.jvmOptions.memory.maxPermSize", "${fn:escapeXml(status.expression)}");
              </script>
            </spring:bind>
          </td>
        </tr>
        <tr>
          <td colspan="2"><h3>Garbage Collection</h3></td>
        </tr>
        <tr>
          <td><form:label path="jvmOptions.garbageCollection.maxGCPauseMillis">Max GC Pause (ms)</form:label></td>
          <td>
            <form:input path="jvmOptions.garbageCollection.maxGCPauseMillis" cssClass="spring-js-numeric spring-js-unit-ms" />
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupmaxGCPauseMillis" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.garbageCollection.maxGCPauseMillis" cssClass="error" />
            <ul class="recomendation">
              <li>100 ms recommended</li>
            </ul>
            <spring:bind path="jvmOptions.garbageCollection.maxGCPauseMillis">
              <script type="text/javascript">
                ams.putId("id.jvmOptions.garbageCollection.maxGCPauseMillis", "${fn:escapeXml(status.expression)}");
              </script>
            </spring:bind>
          </td>
        </tr>
        <tr>
          <td><form:label path="jvmOptions.garbageCollection.maxGCMinorPauseMillis">Max GC Minor Pause (ms)</form:label></td>
          <td>
            <form:input path="jvmOptions.garbageCollection.maxGCMinorPauseMillis" cssClass="spring-js-numeric spring-js-unit-ms" />
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupmaxGCMinorPauseMillis" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.garbageCollection.maxGCMinorPauseMillis" cssClass="error" />
            <ul class="recomendation">
              <li>500 ms recommended</li>
            </ul>
            <spring:bind path="jvmOptions.garbageCollection.maxGCMinorPauseMillis">
              <script type="text/javascript">
                ams.putId("id.jvmOptions.garbageCollection.maxGCMinorPauseMillis", "${fn:escapeXml(status.expression)}");
              </script>
            </spring:bind>
          </td>
        </tr>
        <tr>
          <td colspan="2"><h3>Debug</h3></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <label>
              <form:checkbox path="jvmOptions.debug.heapDumpOnOutOfMemoryError" cssClass="spring-js-boolean" />
              Heap Dump on Out of Memory Error
            </label>
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupheapDumpOnOutOfMemoryError" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.debug.heapDumpOnOutOfMemoryError" cssClass="error" />
            <ul class="recomendation">
              <li>Recommended for Sun JVMs</li>
            </ul>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <label>
              <form:checkbox path="jvmOptions.debug.printGC" cssClass="spring-js-boolean" />
              Print Message at GC
            </label>
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupprintGC" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.debug.printGC" cssClass="error" />
            <ul class="recomendation">
              <li>Recommended when debugging</li>
            </ul>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <label>
              <form:checkbox path="jvmOptions.debug.printHeapAtGC" cssClass="spring-js-boolean" />
              Print Heap at GC
            </label>
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupprintHeapAtGC" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.debug.printHeapAtGC" cssClass="error" />
            <ul class="recomendation">
              <li>Recommended when debugging</li>
            </ul>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <label>
              <form:checkbox path="jvmOptions.debug.printGCApplicationStoppedTime" cssClass="spring-js-boolean" />
              Print GC Application Stopped Time
            </label>
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupprintGCApplicationStoppedTime" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.debug.printGCApplicationStoppedTime" cssClass="error" />
            <ul class="recomendation">
              <li>Recommended when debugging</li>
            </ul>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <label>
              <form:checkbox path="jvmOptions.debug.printGCTimeStamps" cssClass="spring-js-boolean" />
              Print GC Timestamps
            </label>
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupprintGCTimeStamps" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.debug.printGCTimeStamps" cssClass="error" />
            <ul class="recomendation">
              <li>Recommended when debugging</li>
            </ul>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <label>
              <form:checkbox path="jvmOptions.debug.printGCDetails" cssClass="spring-js-boolean" />
              Print GC Details
            </label>
            <form:errors path="jvmOptions.debug.printGCDetails" cssClass="error" />
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupprintGCDetails" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <ul class="recomendation">
              <li>Recommended when debugging</li>
            </ul>
          </td>
        </tr>
        <tr>
          <td><form:label path="jvmOptions.debug.loggc">GC Log File</form:label></td>
          <td>
            <form:input path="jvmOptions.debug.loggc" cssClass="spring-js-text spring-js-extra-wide" />
            <a class="help-link allow-unsaved-changes" 
               href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartuploggc" 
               target="help">
               <img src="<c:url value="/resources/images/help.png" />">
            </a>
            <form:errors path="jvmOptions.debug.loggc" cssClass="error" />
            <ul class="recomendation">
              <li>If specified, must be complete system path. For example: <code>/opt/springsource/springsource-tcserver/logs/gc.log</code></li>
            </ul>
          </td>
        </tr>
      </table>
    </div>
    <br />
    <div id="startup-advanced" class="spring-titlePane bordered-table">
      <h2>Advanced</h2>
      <table>
        <tr>
          
        </tr>
        <tr>
          <td>
            Additional, free-form JVM options may be specified below.
            Many JVM options are specific to the JVM vendor you are using, these options can be set here manually as specific by your JVM vendor.
            <br /><br />
            <label>
              Command Line Arguments
              <a class="help-link allow-unsaved-changes" 
                 href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationstartupcliArgs" 
                 target="help">
                 <img src="<c:url value="/resources/images/help.png" />">
              </a>
              <br />
              <form:textarea path="jvmOptions.advanced.cliArgs" cssClass="spring-js-textarea spring-js-extra-wide" rows="5" cols="80" />
            </label>
            <form:errors path="jvmOptions.advanced.cliArgs" cssClass="error" />
          </td>
        </tr>
      </table>
    </div>
    <br />
    <c:if test="${!readOnly}">
      <input type="submit" class="spring-js-submit" value="Save"/>
    </c:if>
  </form:form>
  <spring:hasBindErrors name="environment">
    <script type="text/javascript">
      dojo.addOnLoad(ams.openAllBlinds);
    </script>
  </spring:hasBindErrors>
</div>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    ams.getWidgetByKey("id.jvmOptions.memory.ms").constraints.min = 1;
    ams.getWidgetByKey("id.jvmOptions.memory.mx").constraints.min = 1;
    ams.getWidgetByKey("id.jvmOptions.memory.ss").constraints.min = 1;
    ams.getWidgetByKey("id.jvmOptions.memory.newSize").constraints.min = 1;
    ams.getWidgetByKey("id.jvmOptions.memory.maxNewSize").constraints.min = 1;
    ams.getWidgetByKey("id.jvmOptions.memory.permSize").constraints.min = 1;
    ams.getWidgetByKey("id.jvmOptions.memory.maxPermSize").constraints.min = 1;
    ams.getWidgetByKey("id.jvmOptions.garbageCollection.maxGCPauseMillis").constraints.min = 1;
    ams.getWidgetByKey("id.jvmOptions.garbageCollection.maxGCMinorPauseMillis").constraints.min = 1;
    
    ams.createMinMaxConstraint(
      ams.getWidgetByKey("id.jvmOptions.memory.ms"),
      ams.getWidgetByKey("id.jvmOptions.memory.mx")
    );
    ams.createMinMaxConstraint(
      ams.getWidgetByKey("id.jvmOptions.memory.newSize"),
      ams.getWidgetByKey("id.jvmOptions.memory.maxNewSize")
    );
    ams.createMinMaxConstraint(
      ams.getWidgetByKey("id.jvmOptions.memory.permSize"),
      ams.getWidgetByKey("id.jvmOptions.memory.maxPermSize")
    );
  });
</script>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />

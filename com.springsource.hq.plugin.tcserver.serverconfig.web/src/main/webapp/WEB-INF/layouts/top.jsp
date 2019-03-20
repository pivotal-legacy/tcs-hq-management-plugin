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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "https://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>tc Runtime instance Configuration</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="shortcut icon" href="<c:url value="/resources/images/favicon.ico" />" />
    <link rel="stylesheet" href="<c:url value="/resources/dijit/themes/tundra/tundra.css" />" type="text/css" />
    <link rel="stylesheet" href="<c:url value="/resources/styles/main.css" />" type="text/css" />
    <link rel="stylesheet" href="<c:url value="/resources/styles/colors.css" />" type="text/css" />
    <!--[if lte IE 6]>
    <link rel="stylesheet" href="<c:url value="/resources/styles/ie_hack.css" />" type="text/css" />
    <![endif]-->
    <link rel="stylesheet" href="<c:url value="/resources/styles/print.css" />" type="text/css" media="print" />
    <script type="text/javascript" src="<c:url value="/resources/dojo/dojo.js,/spring/Spring.js,/spring/Spring-Dojo.js" />" djconfig="locale:'en-us'"></script>
    <script type="text/javascript" src="<c:url value="/resources/scripts/ams.js,/scripts/frameResize.js,/scripts/blinds.js,/scripts/validation.js,/scripts/unsavedChanges.js,/scripts/helpLinks.js" />"></script>
    <!-- 
    Some icons from Silk icon set 1.3 by Mark James, http://www.famfamfam.com/lab/icons/silk/
    -->
  </head>
  <body class="main tundra">
    <div id="page">
      <div id="primary-navigation">
        <div id="primary-left">
          <c:if test="${not empty settings}">
            <ul>
              <li>
                <spring:url value="/app/{settingsId}/" var="url">
                  <spring:param name="settingsId" value="${settings.humanId}" />
                </spring:url>
                <a href="${fn:escapeXml(url)}">Home</a>
              </li>
              <li>
                <spring:url value="/app/{settingsId}/configuration/" var="url">
                  <spring:param name="settingsId" value="${settings.humanId}" />
                </spring:url>
                <a href="${fn:escapeXml(url)}">Configuration</a>
              </li>
              <li>
                <spring:url value="/app/{settingsId}/resources/" var="url">
                  <spring:param name="settingsId" value="${settings.humanId}" />
                </spring:url>
                <a href="${fn:escapeXml(url)}">Resources</a>
              </li>
              <li>
                <spring:url value="/app/{settingsId}/services/" var="url">
                  <spring:param name="settingsId" value="${settings.humanId}" />
                </spring:url>
                <a href="${fn:escapeXml(url)}">Services</a>
              </li>
            </ul>
          </c:if>
        </div>
        <div id="primary-right"></div>
      </div><!-- /primary-navigation -->
      <div id="container">

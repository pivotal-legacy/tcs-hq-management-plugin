/*
 * Copyright (C) 2009-2015  Pivotal Software, Inc
 *
 * This program is is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

var buildPatternHelper = function() {
  var dialog = createPatternDialog();
  dialog.startup();
  dojo.body().appendChild(dialog.domNode);
  dialog.startup();
  
  dojo.place(createPatternHelpButton(dialog), "widget_" + ams.getId("id.logging.pattern"), "after");
  ams.getWidgetByKey("id.logging.enabled").onClick();
}

var createPatternHelpButton = function(dialog) {
  var helpButton = document.createElement("input");
  helpButton.setAttribute("type", "button");
  helpButton.setAttribute("value", "Guide me...");
  helpButton.setAttribute("class", "guide");
  helpButton.setAttribute("id", "pattern-helper-button");
  dojo.connect(helpButton, "onclick", helpButton, function(evt) {
    dialog.show();
    dojo.stopEvent(evt);
  });
  return helpButton;
}

var createPatternDialog = function() {
  dojo.require("dijit.Dialog");
  
  var dialogContent = document.createElement("div");
  dialogContent.appendChild(document.createTextNode("Prefill settings for: "));
  var select = createPatternSelect();
  dialogContent.appendChild(select);
  var previewNode;
  var preview = document.createElement("dl");
  previewNode = document.createElement("dt");
  previewNode.appendChild(document.createTextNode("Pattern: "));
  preview.appendChild(previewNode);
  previewNode = document.createElement("dd");
  previewNode.setAttribute("id", "pattern-helper-format");
  previewNode.appendChild(document.createTextNode("[select a pattern]"));
  preview.appendChild(previewNode);
  dialogContent.appendChild(preview);
  var buttonContainer = document.createElement("div");
  buttonContainer.setAttribute("class", "center");
  var button = document.createElement("button");
  button.setAttribute("id", "pattern-helper-apply");
  button.appendChild(document.createTextNode("Apply"));
  buttonContainer.appendChild(button);
  dialogContent.appendChild(buttonContainer);
  var helpText = document.createElement("div");
  helpText.setAttribute("id", "pattern-helper-text");
  dojo.html.set(helpText, getPatternHelpText());
  dialogContent.appendChild(helpText);
  
  var dialog = new dijit.Dialog({ title: "Pattern Helper", id: "pattern-helper-dialog", "class" : "helper-dialog" }, dialogContent);
  
  dojo.connect(select, "onchange", select, function() {
    dojo.html.set(dojo.byId("pattern-helper-format"), getPatternFromSelect(select));
    preview.value = select.options[select.selectedIndex].value;
  });
  dojo.connect(button, "onclick", button, function() {
    defaultPatternFor(getPatternToApply(select));
    dialog.hide();
  });
  
  return dialog;
}

var getPatternHelpText = function() {
	return "<p>Values for the <code>pattern</code> attribute are made up of literal "
         + "text strings, combined with pattern identifiers prefixed by the \"%\" "
         + "character to cause replacement by the corresponding variable value from "
         + "the current request and response.  The following pattern codes are "
         + "supported:</p>"
         + "<ul>"
         + "<li><b>%a</b> - Remote IP address</li>"
         + "<li><b>%A</b> - Local IP address</li>"
         + "<li><b>%b</b> - Bytes sent, excluding HTTP headers, or '-' if zero</li>"
         + "<li><b>%B</b> - Bytes sent, excluding HTTP headers</li>"
         + "<li><b>%h</b> - Remote host name (or IP address if <code>resolveHosts</code> is false)</li>"
         + "<li><b>%H</b> - Request protocol</li>"
         + "<li><b>%l</b> - Remote logical username from identd (always returns '-')</li>"
         + "<li><b>%m</b> - Request method (GET, POST, etc.)</li>"
         + "<li><b>%p</b> - Local port on which this request was received</li>"
         + "<li><b>%q</b> - Query string (prepended with a '?' if it exists)</li>"
         + "<li><b>%r</b> - First line of the request (method and request URI)</li>"
         + "<li><b>%s</b> - HTTP status code of the response</li>"
         + "<li><b>%S</b> - User session ID</li>"
         + "<li><b>%t</b> - Date and time, in Common Log Format</li>"
         + "<li><b>%u</b> - Remote user that was authenticated (if any), else '-'</li>"
         + "<li><b>%U</b> - Requested URL path</li>"
         + "<li><b>%v</b> - Local server name</li>"
         + "<li><b>%D</b> - Time taken to process the request, in millis</li>"
         + "<li><b>%T</b> - Time taken to process the request, in seconds</li>"
         + "<li><b>%I</b> - current request thread name (can compare later with stacktraces)</li>"
         + "</ul>"
         + "<p>"
         + "There is also support to write information from the cookie, incoming "
         + "header, the Session or something else in the ServletRequest. "
         + "It is modeled after the apache syntax: "
         + "<ul>"
         + "<li><b><code>%{xxx}i</code></b> for incoming headers</li>"
         + "<li><b><code>%{xxx}o</code></b> for outgoing response headers</li>"
         + "<li><b><code>%{xxx}c</code></b> for a specific cookie</li>"
         + "<li><b><code>%{xxx}r</code></b> xxx is an attribute in the ServletRequest</li>"
         + "<li><b><code>%{xxx}s</code></b> xxx is an attribute in the HttpSession</li>"
         + "</ul>"
         + "</p>";
}

var getPatternFromSelect = function(select) {
  var value = select.options[select.selectedIndex].value;
  if (value == "[input]") {
    value = "<input type='text' id='pattern-helper-other-pattern' value='' />";
  }
  else if (value == "common") {
    value = "%h %l %u %t \"%r\" %s %b";
  }
  else if (value == "combined") {
    value = "%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\"";
  }
  else {
    value = ams.entityEncode(value);
  }
  return value;
}

var getPatternToApply = function(select) {
  var value = select.options[select.selectedIndex].value;
  if (value == "[input]") {
    value = dojo.byId("pattern-helper-other-pattern").value;
  }
  return value;
}

var createPatternSelect = function() {
  var select = document.createElement("select");
  var option = document.createElement("option");
  select.appendChild(option);
  
  option = document.createElement("option");
  option.setAttribute("value", "common");
  option.appendChild(document.createTextNode("common"));
  select.appendChild(option);
  
  option = document.createElement("option");
  option.setAttribute("value", "combined");
  option.appendChild(document.createTextNode("combined"));
  select.appendChild(option);

  option = document.createElement("option");
  option.setAttribute("value", "[input]");
  option.appendChild(document.createTextNode("other..."));
  select.appendChild(option);
  
  return select;
};

var defaultPatternFor = function(value) {
  ams.getWidgetByKey("id.logging.pattern").attr("value", value);
};

dojo.addOnLoad(function() {
  buildPatternHelper();
});

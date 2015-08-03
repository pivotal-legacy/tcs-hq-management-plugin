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

var jdbcDrivers = [];

var buildJdbcDriversHelper = function(jdbcDrivers) {
  var dialog = createJdbcDriverDialog(jdbcDrivers);
  dialog.startup();
  dojo.body().appendChild(dialog.domNode);
  dialog.startup();
  
  dojo.place(createJdbcDriverHelpButton(dialog), "widget_" + ams.getId("id.connection.driverClassName"), "after");
}

var createJdbcDriverHelpButton = function(dialog) {
  var helpButton = document.createElement("input");
  helpButton.setAttribute("type", "button");
  helpButton.setAttribute("value", "Guide me...");
  helpButton.setAttribute("class", "guide");
  dojo.connect(helpButton, "onclick", helpButton, function(evt) {
    dialog.show();
    dojo.stopEvent(evt);
  });
  return helpButton;
}

var createJdbcDriverDialog = function(jdbcDrivers) {
  dojo.require("dijit.Dialog");
  
  var dialogContent = document.createElement("div");
  dialogContent.appendChild(document.createTextNode("Prefill settings for: "));
  var select = createJdbcDriversSelect(jdbcDrivers);
  dialogContent.appendChild(select);
  var previewNode;
  var preview = document.createElement("dl");
  previewNode = document.createElement("dt");
  previewNode.appendChild(document.createTextNode("Driver Class Name: "));
  preview.appendChild(previewNode);
  previewNode = document.createElement("dd");
  previewNode.setAttribute("id", "driver-helper-driver-class-name");
  previewNode.appendChild(document.createTextNode("[select a driver]"));
  preview.appendChild(previewNode);
  previewNode = document.createElement("dt");
  previewNode.appendChild(document.createTextNode("URL: "));
  preview.appendChild(previewNode);
  previewNode = document.createElement("dd");
  previewNode.setAttribute("id", "driver-helper-url");
  previewNode.appendChild(document.createTextNode("[select a driver]"));
  preview.appendChild(previewNode);
  dialogContent.appendChild(preview);
  var buttonContainer = document.createElement("div");
  buttonContainer.setAttribute("class", "center");
  var button = document.createElement("button");
  button.setAttribute("id", "driver-helper-apply");
  button.appendChild(document.createTextNode("Apply"));
  buttonContainer.appendChild(button);
  dialogContent.appendChild(buttonContainer);
  
  var dialog = new dijit.Dialog({ title: "Driver Helper", id: "driver-helper-dialog", "class" : "helper-dialog" }, dialogContent);
  
  dojo.connect(select, "onchange", select, function() {
   dojo.html.set(dojo.byId("driver-helper-driver-class-name"), ams.entityEncode(jdbcDrivers[select.selectedIndex].className));
   dojo.html.set(dojo.byId("driver-helper-url"), ams.entityEncode(jdbcDrivers[select.selectedIndex].url));
  });
  dojo.connect(button, "onclick", button, function() {
    defaultDriverSettingsFor(jdbcDrivers[select.selectedIndex]);
    dialog.hide();
  });
  
  return dialog;
}

var createJdbcDriversSelect = function(jdbcDrivers) {
  var select = document.createElement("select");
  select.setAttribute("id", "jdbcDrivers");
  for (var i=0; i<jdbcDrivers.length; i++) {
    var option = document.createElement("option");
    option.setAttribute("value", i);
    option.appendChild(document.createTextNode(jdbcDrivers[i].name));
    select.appendChild(option);
  }
  return select;
};

var defaultDriverSettingsFor = function(jdbcDriver) {
  var driverClassName = ams.getWidgetByKey("id.connection.driverClassName");
  var url = ams.getWidgetByKey("id.connection.url");
  driverClassName.attr("value", jdbcDriver.className);
  url.attr("value", jdbcDriver.url);
};

dojo.addOnLoad(function() {
  dojo.xhrGet({
    url : jdbcDriversDataUrl,
    handleAs : "json",
    load : function(responseObject, ioArgs) {
      jdbcDrivers = responseObject.jdbcDrivers;
      buildJdbcDriversHelper(jdbcDrivers);
    },
    error : function(response, ioArgs){ console.error(response) } 
  });
});

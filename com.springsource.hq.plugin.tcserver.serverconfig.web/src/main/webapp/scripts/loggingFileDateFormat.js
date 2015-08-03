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

var buildFileDateFormatHelper = function() {
  var dialog = createFileDateFormatDialog();
  dialog.startup();
  dojo.body().appendChild(dialog.domNode);
  dialog.startup();
  
  dojo.place(createFileDateFormatHelpButton(dialog), "widget_" + ams.getId("id.logging.fileDateFormat"), "after");
  ams.getWidgetByKey("id.logging.enabled").onClick();
}

var createFileDateFormatHelpButton = function(dialog) {
  var helpButton = document.createElement("input");
  helpButton.setAttribute("type", "button");
  helpButton.setAttribute("value", "Guide me...");
  helpButton.setAttribute("class", "guide");
  helpButton.setAttribute("id", "fileDateFormat-helper-button");
  dojo.connect(helpButton, "onclick", helpButton, function(evt) {
    dialog.show();
    dojo.stopEvent(evt);
  });
  return helpButton;
}

var createFileDateFormatDialog = function() {
  dojo.require("dijit.Dialog");
  
  var dialogContent = document.createElement("div");
  dialogContent.appendChild(document.createTextNode("Prefill settings for: "));
  var select = createFileDateFormatSelect();
  dialogContent.appendChild(select);
  var previewNode;
  var preview = document.createElement("dl");
  previewNode = document.createElement("dt");
  previewNode.appendChild(document.createTextNode("Date Format: "));
  preview.appendChild(previewNode);
  previewNode = document.createElement("dd");
  previewNode.setAttribute("id", "fileDateFormat-helper-format");
  previewNode.appendChild(document.createTextNode("[select a time period]"));
  preview.appendChild(previewNode);
  dialogContent.appendChild(preview);
  var buttonContainer = document.createElement("div");
  buttonContainer.setAttribute("class", "center");
  var button = document.createElement("button");
  button.setAttribute("id", "fileDateFormat-helper-apply");
  button.appendChild(document.createTextNode("Apply"));
  buttonContainer.appendChild(button);
  dialogContent.appendChild(buttonContainer);
  
  var dialog = new dijit.Dialog({ title: "File Date Format Helper", id: "fileDateFormat-helper-dialog", "class" : "helper-dialog" }, dialogContent);
  
  dojo.connect(select, "onchange", select, function() {
    dojo.html.set(dojo.byId("fileDateFormat-helper-format"), ams.entityEncode(select.options[select.selectedIndex].value));
    preview.value = select.options[select.selectedIndex].value;
  });
  dojo.connect(button, "onclick", button, function() {
    defaultFileDateFormatFor(select.options[select.selectedIndex].value);
    dialog.hide();
  });
  
  return dialog;
}

var createFileDateFormatSelect = function() {
  var select = document.createElement("select");
  var option = document.createElement("option");
  select.appendChild(option);
  
  option = document.createElement("option");
  option.setAttribute("value", "yyyy-MM");
  option.appendChild(document.createTextNode("Monthly"));
  select.appendChild(option);
  
  option = document.createElement("option");
  option.setAttribute("value", "yyyy.ww");
  option.appendChild(document.createTextNode("Weekly"));
  select.appendChild(option);

  option = document.createElement("option");
  option.setAttribute("value", "yyyy-MM-dd");
  option.appendChild(document.createTextNode("Daily"));
  select.appendChild(option);

  option = document.createElement("option");
  option.setAttribute("value", "yyyy-MM-dd.HH");
  option.appendChild(document.createTextNode("Hourly"));
  select.appendChild(option);
  
  return select;
};

var defaultFileDateFormatFor = function(value) {
  ams.getWidgetByKey("id.logging.fileDateFormat").attr("value", value);
};

dojo.addOnLoad(function() {
  buildFileDateFormatHelper();
});

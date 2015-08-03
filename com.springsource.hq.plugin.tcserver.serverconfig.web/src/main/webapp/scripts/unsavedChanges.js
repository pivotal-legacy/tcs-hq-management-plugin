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

// block leaving pages with changes forms via links, unless the link is white listed
var changeMade = false;
var targetUrl;

dojo.addOnLoad(function() {
  dojo.require("dijit.Dialog");
  
  var changeMadeOnChange = function() {
    changeMade = true;
  };
  
  ams.getWidgetsByType("form").forEach(function(widget) {
    widget.connect(widget, "onChange", changeMadeOnChange);
  });
  
  var dialogContent = "You have unsaved changes on this page.  Changes will be lost if you leave this page."
                    + "<br /><br />"
                    + "<div class='center'>"
                    + "<input type='button' value='Leave' onclick=\"window.location = targetUrl;\" />"
                    + "&nbsp;&nbsp;&nbsp;"
                    + "<input type='button' value='Stay' onclick=\"dijit.byId('unsaved-changes-dialog').hide();\" />"
                    + "</div>";
  var dialog = new dijit.Dialog({ title: "Unsaved Changes", content: dialogContent, id: "unsaved-changes-dialog" });
  dojo.body().appendChild(dialog.domNode);
  dialog.startup();
  dojo.query("a[href]").forEach(function(link) {
    if (!dojo.hasClass(link, "allow-unsaved-changes")) {
      dojo.connect(link, "onclick", link, function(event) {
        if (changeMade) {
          dojo.stopEvent(event);
          targetUrl = dojo.attr(link, "href");
          dialog.show();
        }
      })
    }
  });
});

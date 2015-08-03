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

dojo.addOnLoad(function() {
  dojo.require("dijit.TitlePane");
  
  var blinds = [];
  
  dojo.query('.spring-titlePane-open > h2').forEach(function(titleElement) {
    blinds.push(titleElement.parentNode.id);
    var className = titleElement.parentNode.className;
    var id = titleElement.parentNode.id;
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : id,
      widgetType : 'dijit.TitlePane',
      widgetAttrs : { 
        title : titleElement.innerHTML, 
        open : true,
        'class' : className
      }
    }));
    ams.registerWidget("blind", dijit.byId(id));
  }).style('display','none');
  
  dojo.query('.spring-titlePane > h2').forEach(function(titleElement) {
    blinds.push(titleElement.parentNode.id);
    var className = titleElement.parentNode.className;
    var id = titleElement.parentNode.id;
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : id,
      widgetType : 'dijit.TitlePane',
      widgetAttrs : { 
        title : titleElement.innerHTML, 
        open : false,
        'class' : className
      }
    }));
    ams.registerWidget("blind", dijit.byId(id));
  }).style('display','none');
  
  dojo.query('.spring-titlePane table.bordered-table, .spring-titlePane-open table.bordered-table').removeClass('bordered-table');
  
  resizeFrame();
  
  dojo.addOnLoad(function() {
    for (var i=0; i<blinds.length; i++) {
      var blind = dijit.byId(blinds[i]);
      blind.connect(blind, "toggle", function() {
        setTimeout(resizeFrame, blind.duration);
      });
    }
  });
});
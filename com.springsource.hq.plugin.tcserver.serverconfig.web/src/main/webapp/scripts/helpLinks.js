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

dojo.addOnLoad( function() {
  dojo.query("a.help-link").forEach(function (link) {
    dojo.connect(link, "onclick", link, function(event){
      dojo.stopEvent(event);
      var helpWindow = window.open(dojo.attr(link, "href"), dojo.attr(link, "target"), "width=800,height=600,status=0,toolbar=0,location=0,menubar=0,directories=0,resizable=1,scrollbars=1");
      if (window.focus) {
        helpWindow.focus();
      }
    });
  });
});

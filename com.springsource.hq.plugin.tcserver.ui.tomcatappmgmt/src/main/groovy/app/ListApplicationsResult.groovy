/*
 * Copyright (C) 2011-2015  Pivotal Software, Inc
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

import org.json.JSONArray
import org.json.JSONObject

final class ListApplicationsResult {

    final JSONObject applications

    final JSONArray errorMessages

    final JSONArray applicationsDetail

    public ListApplicationsResult(JSONObject applications, JSONArray errorMessages, JSONArray applicationsDetail) {
        this.applications = applications;
        this.errorMessages = errorMessages;
        this.applicationsDetail = applicationsDetail
    }
}

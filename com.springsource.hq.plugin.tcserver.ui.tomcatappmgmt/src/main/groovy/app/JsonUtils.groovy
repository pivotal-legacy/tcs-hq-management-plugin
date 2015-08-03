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

import org.json.JSONArray;



private final class JsonUtils {
    
    private static final KEY_RUNNING_COUNT = "runningCount";
    
    private static final KEY_SESSIONS_COUNT = "sessionsCount";
    
    static JSONArray convertApplicationMapToJsonArray(totalMemberCount, isGroup, changesMap){
        def resourceListMap = new JSONArray()
        if (totalMemberCount > 1 || isGroup) {
            
            def runningMapCount = new LinkedHashMap()
            
            for (int i=0; i < changesMap.length(); i++) {
                
                def resourceName = changesMap.get(i).key
                def resourceInfo = changesMap.get(i).value
                
                for (int x=0; x < resourceInfo.length(); x++) {
                    String applicationName = (String)resourceInfo.get(x).key
                    def applicationMap = resourceInfo.get(x).value
                    
                    String applicationVersion = applicationMap.get(2)
                    
                    def runningRevisionsMap = runningMapCount.get(applicationName)
                    if (runningRevisionsMap == null) {
                        runningRevisionsMap = new LinkedHashMap()
                        runningMapCount.put(applicationName, runningRevisionsMap)
                    }
                    
                    def revisionDetails = runningRevisionsMap.get(applicationVersion)
                    if (revisionDetails == null) {
                        revisionDetails = new LinkedHashMap()
                        runningRevisionsMap.put(applicationVersion, revisionDetails);
                    }
                    
                    if (!revisionDetails.containsKey(KEY_RUNNING_COUNT)){
                        revisionDetails.put(KEY_RUNNING_COUNT, 0)
                    }                                        
                    
                    if (!revisionDetails.containsKey(KEY_SESSIONS_COUNT)){
                        revisionDetails.put(KEY_SESSIONS_COUNT, 0)
                    }
 
                    if ("Running" == applicationMap.get(0)) {
                        revisionDetails.put(KEY_RUNNING_COUNT, revisionDetails.get(KEY_RUNNING_COUNT).plus(1))
                    }
                    
                    revisionDetails.put(KEY_SESSIONS_COUNT, revisionDetails.get(KEY_SESSIONS_COUNT).plus(applicationMap.get(1)))
                }
            }
            def tempListMap = new JSONArray()
            
            runningMapCount.each{application -> 
                application.value.each{revision ->
                    def valueArray = new JSONArray()
                    def runningCount = revision.value.get(KEY_RUNNING_COUNT)
                    def sessionCount = revision.value.get(KEY_SESSIONS_COUNT)
                    
                    valueArray.put("${runningCount} of " + totalMemberCount
                            + " servers are running.")
                    valueArray.put(sessionCount)
                    valueArray.put(revision.key)
                    tempListMap.put(key:"${application.key}", value:valueArray)
                }
            }
            resourceListMap.put(key:"placeholder", value:tempListMap)
        } else {
            resourceListMap = changesMap
        }
        return resourceListMap
    }
}

/*
 * Copyright 2009 Summit Management Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.summit.jbeacon.util;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * This is packet of information describing a resource.
 * If <b>name</b> is blank... the resource is almost meaningless.
 * @author justin smith
 * @since 1.0
 */
public class Resource implements Serializable {
    private static final long serialVersionUID = 1;

    /**
     * Resource name, this really should be set.
     */
    private String resourceName;

    private String hostName = null;
    private String ip = null;

    /**
     * Resource metadata. things like services and endpoints.
     */
    private SortedMap<String,String> resourceMetaData;

    public Resource(){
        resourceMetaData = new TreeMap<String, String>();
    }

    /**
     * @return the name
     */
    public final String getResourceName() {
        return resourceName;
    }

    /**
     * @param name the name to set
     */
    public final void setResourceName(final String name) {
        this.resourceName = name;
    }

    /**
     * @return the metaData
     */
    public final Map<String, String> getResourceMetaData() {
        return resourceMetaData;
    }

    /**
     * @param metaData the metaData to set
     */
    public final void setResourceMetaData(
            final Map<String, String> metaData) {
        Set<Map.Entry<String,String>> entries = metaData.entrySet();
        this.resourceMetaData.clear();
        for(Map.Entry<String,String> entry : entries){
            this.resourceMetaData.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @return the hostName
     */
    public final String getHostName() {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public final void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    /**
     * @return the ip
     */
    public String getIp() {
	return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
	this.ip = ip;
    }

}

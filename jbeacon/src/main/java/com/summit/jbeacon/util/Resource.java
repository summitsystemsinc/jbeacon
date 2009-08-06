/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    /**
     * Resource name, this really should be set.
     */
    private String resourceName;

    private String hostName = null;

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

}

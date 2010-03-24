/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.summit.jbeacon.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author justin
 */
public class ResourcePacket implements Serializable, Comparable<ResourcePacket> {
	private static final long serialVersionUID = 1;

	private String defaultHostName;
	private String defaultIp;
	private List<Resource> resources;

	public ResourcePacket() {
		//TODO make this a sorted set
		resources = new ArrayList();
	}

	/**
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(List<Resource> resources) {
		this.resources = resources;
		for(Resource r : this.resources){
		    if(r.getHostName()==null){
			r.setHostName(defaultHostName);
		    }
		    if(r.getIp()==null){
			r.setIp(defaultIp);
		    }
		}
	}

	/**
	 * @return the defaultHostName
	 */
	public String getDefaultHostName() {
		return defaultHostName;
	}

	/**
	 * @param defaultHostName the defaultHostName to set
	 */
	public void setDefaultHostName(String defaultHostName) {
		this.defaultHostName = defaultHostName;
	}

	@Override
	public int compareTo(ResourcePacket o) {
		return getDefaultHostName().compareToIgnoreCase(o.getDefaultHostName());
	}

    /**
     * @return the defaultIp
     */
    public String getDefaultIp() {
	return defaultIp;
    }

    /**
     * @param defaultIp the defaultIp to set
     */
    public void setDefaultIp(String defaultIP) {
	this.defaultIp = defaultIP;
    }
}

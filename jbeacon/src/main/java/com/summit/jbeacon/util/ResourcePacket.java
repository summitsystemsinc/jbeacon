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

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

package com.springsource.hq.plugin.tcserver.util.application;

public final class ApplicationIdentifier implements Comparable<ApplicationIdentifier> {

    private final String name;

    private final int version;

    public ApplicationIdentifier(String applicationIdentifierString) {
        String[] components = applicationIdentifierString.split("##");

        this.name = components[0];

        if (components.length > 1) {
            this.version = Integer.parseInt(components[1]);
        } else {
            this.version = 0;
        }
    }

    public ApplicationIdentifier(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApplicationIdentifier other = (ApplicationIdentifier) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(name);

        if (version > 0) {
            builder.append(" revision ");
            builder.append(this.version);
        }

        return builder.toString();
    }

    public int compareTo(ApplicationIdentifier o) {
        int comparison = this.name.compareTo(o.name);
        if (comparison == 0) {
            comparison = this.version - o.version;
        }
        return comparison;
    }
}

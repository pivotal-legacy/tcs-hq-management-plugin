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

package com.springsource.hq.plugin.tcserver.serverconfig;

/**
 * Allows domain classes to track its containing parent. This enables walking the tree from any node in the tree.
 * 
 * @param <P> the parent's type
 * @since 2.0
 */
public interface Hierarchical<P extends Hierarchical<?>> {

    /**
     * @return this node's parent
     */
    public P parent();

    /**
     * @param parent the node's parent
     */
    public void setParent(P parent);

    /**
     * Apply the parent node to all children that implement {@link Hierarchical}. This method should invoke
     * {@link Hierarchical#setParent(Hierarchical)} and {@link Hierarchical#applyParentToChildren()} for each child node
     * to ensure the tree is properly constructed.
     */
    public void applyParentToChildren();

}

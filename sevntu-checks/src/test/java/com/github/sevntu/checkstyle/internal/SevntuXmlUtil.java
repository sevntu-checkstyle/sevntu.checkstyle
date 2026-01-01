///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.internal;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

public final class SevntuXmlUtil {

    private SevntuXmlUtil() {
    }

    public static Node findElementByTag(Set<Node> nodes, String tag) {
        Node result = null;

        for (Node child : nodes) {
            if (tag.equals(child.getNodeName())) {
                result = child;
                break;
            }
        }

        return result;
    }

    public static Set<Node> findElementsByTag(Set<Node> nodes, String tag) {
        final Set<Node> result = new HashSet<>();

        for (Node child : nodes) {
            if (tag.equals(child.getNodeName())) {
                result.add(child);
            }
        }

        return result;
    }

}

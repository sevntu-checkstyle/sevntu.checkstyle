////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2014  Oliver Burn
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
////////////////////////////////////////////////////////////////////////////////
package com.github.sevntu.checkstyle.checks.regexp;

/**
 * Represents the possible modes of operation for the {@link RegexpOnFilenameCheck}.
 *
 * @author Thomas Jensen
 */
public enum RegexpOnFilenameOption
{
    /** In REQUIRED mode, the regular expression must match the filename, and a violation is
     *  logged if the regexp does <i>not</i> match. */
    REQUIRED,

    /** In ILLEGAL mode, the regular expression must <i>not</i> match the filename, and a violation
     *  is logged if the regexp matched anyway. */
    ILLEGAL;
}

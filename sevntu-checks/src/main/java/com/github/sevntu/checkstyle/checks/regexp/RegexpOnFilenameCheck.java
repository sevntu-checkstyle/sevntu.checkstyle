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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;


/**
 * This check applies a given regular expression to the names of files. Depending on the
 * configuration, a warning is logged if a required match is not found, or if an illegal match
 * is found.
 * <p/>
 * This is useful for situations such as:
 * <ul>
 * <li>Checking that resources in certain directories follow a naming convention</li>
 * <li>Resource file names contain only legal characters</li>
 * <li>Files of certain types are created in the right places, e.g. Java files under
 *     src/&#42;/java</li>
 * <li>Prevent certain files or types of files altogether, by "banning" their names</li>
 * </ul>
 * <p/>
 * By default, this check flags leading and trailing spaces in file names.
 * <p/>
 * The check works like this:
 * <ol>
 * <li>If file extensions are configured, it is checked if the file extension applies.
 *     As with all FileSetChecks, this check only ever does anything if the file
 *     extension matches. Leave out the property to match all file extensions.</li>
 * <li>If configured, the regular expression given in the <tt>selection</tt> property is
 *     applied to the canonical file name. Only files that match this expression
 *     are checked. Leave out the property to match all files.</li>
 * <li>The given <tt>regexp</tt> is matched against the file name. What part of the file
 *     name it is applied to, and how the result is interpreted is governed by the check
 *     properties.</li>
 * </ol>
 *
 * <p style="font-weight:bold;font-size:large;">Properties</p>
 *
 * <table border="1" cellspacing="0">
 * <tr>
 * <th>name</th>
 * <th>description</th>
 * <th>type</th>
 * <th>default value</th>
 * </tr>
 * <tr valign="top">
 * <td>fileExtensions</td>
 * <td>Comma-separated list of file extensions. Leading dots are optional.
 *     Spaces after the commas are allowed. Only files with one of these
 *     extensions are checked against the regular expression.</td>
 * <td><a href="http://checkstyle.sourceforge.net/property_types.html#stringSet">StringSet</a></td>
 * <td>unrestricted</td>
 * </tr>
 * <tr valign="top">
 * <td>selection</td>
 * <td>Limits the check to files whose canonical path name contains the given
 *     pattern. The canonical path is the simplest possible absolute path,
 *     including the file name (no '<tt>..</tt>' elements etc.).</td>
 * <td><a href="http://checkstyle.sourceforge.net/property_types.html#regexp">regular
 *     expression</a></td>
 * <td>unrestricted</td>
 * </tr>
 * <tr valign="top">
 * <td>regexp</td>
 * <td>The regular expression applied to the file name.</td>
 * <td><a href="http://checkstyle.sourceforge.net/property_types.html#regexp">regular
 *     expression</a></td>
 * <td><tt>^(?:\s+.*|.*?\s+)$</tt></td>
 * </tr>
 * <tr valign="top">
 * <td>mode</td>
 * <td>whether <tt>regexp</tt> finds required or illegal matches</td>
 * <td>{@link RegexpOnFilenameOption Mode}</td>
 * <td><code>illegal</code></td>
 * </tr>
 * <tr valign="top">
 * <td>simple</td>
 * <td>If <code>true</code>, only the simple name of the file will be checked
 *     against the pattern specified by <tt>regexp</tt>;
 *     if <code>false</code>, the entire canonical path will be checked.<br/>
 *     Note that this option applies only to the pattern specified by
 * <tt>regexp</tt>; the <tt>selection</tt> property is <i>always</i> treated
 *     as if <tt>simple=false</tt>.</td>
 * <td><a href="http://checkstyle.sourceforge.net/property_types.html#boolean">Boolean</a></td>
 * <td><code>true</code></td>
 * </tr>
 * </table>
 *
 * In addition to the properties, optionally adding a <code>message</code> element
 * may benefit this check to make the warning easier to understand. The message key
 * depends on the value of the <tt>mode</tt> option. If <tt>mode=required</tt>,
 * the message key <tt>regexp.filepath.required</tt> is used.
 * If <tt>mode=illegal</tt>, the message key <tt>regexp.filepath.illegal</tt> is
 * used. The message text can make use of placeholders <tt>{0}</tt> (the file name
 * as used by the matcher) and <tt>{1}</tt> (the regular expression used by the
 * matcher).
 *
 * <p style="font-weight:bold;font-size:large;">Examples</p>
 * <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?lang=xml"></script>
 *
 * To configure the check to detect leading and trailing spaces in file names:
 *
 * <pre class="prettyprint">&lt;module name=&quot;RegexpOnFilename&quot;&gt;
 *  &lt;message key=&quot;regexp.filepath.illegal&quot; value=&quot;Filename ''{0}'' contains leading or trailing spaces.&quot;/&gt;
&lt;/module&gt;</pre>
 *
 * To configure the check to ensure that Java files reside in java folders, not
 * resource folders:
 *
 * <pre class="prettyprint">&lt;module name=&quot;RegexpOnFilename&quot;&gt;
 *  &lt;property name=&quot;fileExtensions&quot; value=&quot;java&quot;/&gt;
 *  &lt;property name=&quot;regexp&quot; value=&quot;[\\/]src[\\/](?:test|main)[\\/]java[\\/]&quot;/&gt;
 *  &lt;property name=&quot;mode&quot; value=&quot;required&quot;/&gt;
 *  &lt;property name=&quot;simple&quot; value=&quot;false&quot;/&gt;
 *  &lt;message key=&quot;regexp.filepath.required&quot; value=&quot;The Java file ''{0}'' must reside in a Java source folder.&quot;/&gt;
&lt;/module&gt;</pre>
 *
 * To configure the check to enforce an HTML file naming convention on files in a
 * certain folder:
 *
 * <pre class="prettyprint">&lt;module name=&quot;RegexpOnFilename&quot;&gt;
 *  &lt;property name=&quot;fileExtensions&quot; value=&quot;html&quot;/&gt;
 *  &lt;property name=&quot;selection&quot; value=&quot;[\\/]src[\\/]main[\\/]resources[\\/]html[\\/]views[\\/]&quot;/&gt;
 *  &lt;property name=&quot;regexp&quot; value=&quot;^view_.*&quot;/&gt;
 *  &lt;property name=&quot;mode&quot; value=&quot;required&quot;/&gt;
 *  &lt;message key=&quot;regexp.filepath.required&quot; value=&quot;Name of ''{0}'' must start with ''view_''.&quot;/&gt;
&lt;/module&gt;</pre>
 *
 * To configure the check to ban GIF files in favor of PNG:
 *
 * <pre class="prettyprint">&lt;module name=&quot;RegexpOnFilename&quot;&gt;
 *  &lt;property name=&quot;fileExtensions&quot; value=&quot;gif&quot;/&gt;
 *  &lt;property name=&quot;regexp&quot; value=&quot;.&quot;/&gt;
 *  &lt;message key=&quot;regexp.filepath.illegal&quot; value=&quot;''{0}'' must be in PNG format, not GIF.&quot;/&gt;
&lt;/module&gt;</pre>
 *
 * <h3>Parent Module</h3>
 * <a href="http://checkstyle.sourceforge.net/config.html#Checker">Checker</a>
 *
 * @author Thomas Jensen
 */
public class RegexpOnFilenameCheck
    extends AbstractFileSetCheck
{
    /**
     * regexp applied to the canonical file name in order to determine if the file is applicable for
     * the check (substring match)
     */
    private Pattern mSelection;

    /** mode of operation (required or illegal) */
    private RegexpOnFilenameOption mMode = RegexpOnFilenameOption.ILLEGAL;

    /**
     * if <code>true</code>, only the simple name of the file will be checked against the
     * regexp;<br/> if <code>false</code>, the entire canonical path will be checked
     */
    private boolean mSimple = true;

    /** the default regexp detects leading and trailing whitespace */
    private static final Pattern REGEXP_DEFAULT = Pattern.compile("^(?:\\s+.*|.*?\\s+)$");

    /** the given regexp */
    private Pattern mRegexp = REGEXP_DEFAULT;



    /**
     * Setter.
     * @param aSelection the new value of {@link #mSelection}
     */
    public void setSelection(final String aSelection)
    {
        if (aSelection != null && aSelection.length() > 0) {
            mSelection = Pattern.compile(aSelection);
        }
    }



    public void setMode(final String aMode)
    {
        mMode = Enum.valueOf(RegexpOnFilenameOption.class,
            aMode.trim().toUpperCase(Locale.ENGLISH));
    }



    public void setSimple(final boolean aSimple)
    {
        mSimple = aSimple;
    }



    /**
     * Setter.
     * @param aRegexp the new value of {@link #mRegexp}
     */
    public void setRegexp(final String aRegexp)
    {
        if (aRegexp != null && aRegexp.length() > 0) {
            mRegexp = Pattern.compile(aRegexp);
        }
    }



    @Override
    protected void processFiltered(final File aFile, final List<String> aLines)
    {
        String filePath = null;
        try {
            filePath = aFile.getCanonicalPath();
        }
        catch (IOException e) {
            filePath = aFile.getAbsolutePath();
        }

        boolean ok = true;
        if (mSelection == null || mSelection.matcher(filePath).find()) {

            if (mSimple) {
                filePath = aFile.getName();
            }

            ok = mRegexp.matcher(filePath).find();
            if (mMode == RegexpOnFilenameOption.ILLEGAL) {
                ok = !ok;
            }
        }

        if (!ok) {
            final String msgKey = "regexp.filepath." + mMode.toString().toLowerCase(Locale.ENGLISH);
            // Log the exact String that the regexp was applied to and the exact regexp that was
            // used. It is important to be accurate here in order to enable people to check results.
            log(0, msgKey, filePath, mRegexp.pattern());
        }
    }
}

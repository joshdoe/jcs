/*
 * TextHandler.java
 *
 * Created on November 7, 2002, 1:28 PM
 */

/*
 * The JCS Conflation Suite (JCS) is a library of Java classes that
 * can be used to build automated or semi-automated conflation solutions.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.vividsolutions.jcs.conflate.roads.attribute;

/**
 *<pre>
 *Provides basic text Regular Expression functionality
 * 
 *  all these functions are static
 *
 * match( in, word)  - returns true if word is a word in 'in'
 * remove(in,word)   - returns a new string with word removed from 'in'
 * trimWhite(in)    -  removes white space from start and end of in and
 *                     converts double spaces to single spaces.
 *</pre>
 */
public class TextHandler {

    /** Creates new TextHandler */
    public TextHandler() {
    }

    /**<pre>
     * match("niagara street","street") returns true
     *     word is only matched if at the start or end of the string or bounded by spaces
     *     ie.  "^word$" , "^word ", " word$", " word "
     *</pre>
     * @param in  text to look at
     * @param word word to match
     */
    
     public static boolean match(String in, String word)
    {
        int i;
      
        
        if (in.equals(word))
        {
            return true; // "^word$"
        }
        
        if (in.startsWith(word +" "))
        {
             return true; // "^word "
        }
        
        if (in.endsWith(" " + word ))
        {
             return true; // " word$"
        }
        
        i = in.indexOf(" " + word + " ");
        if (i != -1)
        {
            return true;  // " word "
        }
        
        return false;
    }
    
    /**
     * remove ("niagara street","street") - > "niagara"
     *
     *you should trimWhite() after this
     *
     * @param in  text to look at
     * @param word word to match
     */
     public static String remove(String in, String word)
    {
        int i;
        
         if (in.equals(word))
        {
            return ""; // "^word$"
        }
        
        if (in.startsWith(word +" "))
        {
            return in.substring(word.length()+1); // "^word "
        }
        
        if (in.endsWith(" " + word ))
        {
             return in.substring(0, in.length()-word.length()-1) ; // " word$"
        }
        
        i = in.indexOf(" " + word + " ");
        if (i != -1)
        {
            return in.substring(0,i) + " " + in.substring(i+word.length()+1);  // " word "
        }
        return in;
    }
    
    /**
     *remove extra spaces
     */
     public static String trimWhite(String in)
    {
        int idx=0; 
        
        in = in.trim();
        // trim interal double+ spaces  ie. "niagara  st"
        // normally this loop is only done once so its not too bad
        idx = in.indexOf("  ");
        while (idx != -1)
        {
            in = in.substring(0,idx) + in.substring(idx+1);
            idx = in.indexOf("  ");
        }
        return in;
    }
    
}

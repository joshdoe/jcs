

/*
 * KeywordMatch.java
 *
 * Created on October 16, 2002, 3:43 PM
 */

/*
 * The Java Conflation Suite (JCS) is a library of Java classes that
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
 *   This is a helper class for the KeywordParser class. <br>
 *   Basically, the KeywordParser finds keywords in a string. These matches<br>
 *   are stored in a KeywordMatch object.<br>
 * <pre>
 *   ie.  "SW Marine Drive"  give one possible match as:
 *     -> KeywordMatch with <br>
 *             text      = "Marine"  (ie. whats not matched)
 *             road type = "Drive"
 *             prefix    = "SW"
 *     NOTE: keyword - actual match in the string 
 *           codes   - Integer code for the keyword
 *           type    - generic class of the match (ie. road type or prefix)
 *</pre>
 *
 */
public class KeywordMatch {
    
     protected String text;
     protected int nkeywords = 0;
    
    //for each match (nkeywords)
   private java.util.ArrayList keywords =new java.util.ArrayList();   //actual keyword (ie. 'rd')
   private java.util.ArrayList codes    =new java.util.ArrayList();  //identifier of this keyword (ie. Integer(15) = 'rd','road',..)
   private java.util.ArrayList type     =new java.util.ArrayList();  // type of match (ie. 'rdtype','rddesc','suffix'
 
    

    /** Creates new KeywordMatch */
    public KeywordMatch() {
    }
    
  
        /** Creates new KeywordMatch with no matches in it<br>
         *@param newText set the text property of this Match (what left over after all the keyword matches)
         */
    public KeywordMatch(String newText) 
    {
           text = newText;
    }
    
    /**
     *  Add a single keyword match to this object - reset the text property
     *
     *   @param key - the actual keyword that was matched 
     *   @param codeNumber - int code to uniquely identify what was matched
     *   @param elementType - what class of keyword this is (ie. road type, directional indicator...)
     *   @param remainingText - whats left over in the string
     */
    public void add(String key, int codeNumber, String elementType,String remainingText)
    {
        keywords.add(key );
        codes.add   ( new Integer(codeNumber));
        type.add    (elementType);
        nkeywords++;
        text = remainingText;
    }
    
    
    /**
     *  Add a single keyword match to this object - reset the text property<br>
     *  Same as the other add() expect this takes an Integer instead of an int for codeNumber
     *
     *   @param key - the actual keyword that was matched 
     *   @param codeNumber - Integer code to uniquely identify what was matched
     *   @param elementType - what class of keyword this is (ie. road type, directional indicator...)
     *   @param remainingText - whats left over in the string
     */
     public void add(String key, Integer codeNumber, String elementType,String remainingText)
    {
        keywords.add(key );
        codes.add   ( codeNumber);
        type.add    (elementType);
        nkeywords++;
        text = remainingText;
    }
    
   
    /**
     *  returns the text property (ie. what not accounted for in the keywords)
     */
   public String getText()
   {
       return text;
   }
   
    /**
     *  updates the text property (ie. what not accounted for in the keywords)
     */
   public void setText(String tx)
   {
       text = tx;
   }
   
   /**
    * Compare two KeywordMatches to see if they are equivelent.  Order of the matches<br>
    *  isnt important.
    *<pre>
    *  ie.  "SW Marine Drive" -> SW + Drive + "Marine"
    *  and  "SW Marine Drive" -> Drive + SW + "Marine"
    *     would return true
    *
    * BUT
    *  ie.  "SW Marine Drive" -> SW + Drive + "Marine"
    *    and "SW Marine Drive" -> SW + "Marine Drive"
    *  would return false
    *</pre>
    *
    * @param other the other KeywordMatch to compare against
    */
    public boolean isSame(KeywordMatch other)
    {
        int t,u;
        String key,type;
        Integer code;
        boolean found;
        
        if (  (this.text.equals(other.text)) && (this.nkeywords == other.nkeywords) )
        {
            boolean[] already_found = new boolean[this.nkeywords]; // true if this sub-match has already been used
            for (t=0;t<this.nkeywords;t++)
            {
                already_found[t] = false;
            }
            
            for (t=0;t<this.nkeywords;t++)
            {
                key  = (String)  this.keywords.get(t);
                type = (String) this.type.get(t);
                code = (Integer) this.codes.get(t);
                
                found = false;
                //search for this entry
                for (u=0;u<this.nkeywords;u++)
                {
                    if (!(already_found[u])) //hasnt already been matched (only match once)
                    {
                        if (
                                (key.equals((String) other.keywords.get(u)) )  &&
                                (type.equals((String) other.type.get(u)) ) &&
                                (code.longValue() ==( (Integer) other.codes.get(t)).longValue())
                                ) //key, code,type the same
                        {
                            already_found[u] = true;
                            found = true;
                            break; //abort loop
                        }
                    }
                }
                if (!(found))  //didnt find a corresponding keyword
                {
                    return true;
                }
            }
            return true; //everything checked out!
        }
        else
        {
            return false; //not the same text -> not equal or not same # of sub-matches 
        }
    }
    
  
    
    /**
     *  Debug function to give a readable version of this object
     */
    public void print()
    {
        int u;
        
            System.out.println("streetname: "+this.getText());
            
            for(u=0;u<this.nkeywords;u++)
            {
                System.out.println("code: keyword="+this.keywords.get(u) + "  type="+this.type.get(u) );
            }
        
    }
    
    /**
     *  return a new KeywordMatch that has the same information in it as this one.<br>
     *  This is a "Deepish" copy.
     */
    public KeywordMatch copy()
    {
        int t;
        KeywordMatch result = new KeywordMatch();
        
        result.text = new String(this.text);
        result.nkeywords = this.nkeywords;
        
        for(t=0;t<nkeywords;t++)
        {
            result.keywords.add(  (String) this.keywords.get(t) );
            result.codes.add(  (Integer) this.codes.get(t));
            result.type.add( (String)  this.type.get(t));
        }
        
        return result;
    }
    
}

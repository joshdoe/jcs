

/*
 * KeywordParser.java
 *
 * Created on October 16, 2002, 2:55 PM
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

//import org.apache.oro.text.perl.Perl5Util;
//import org.apache.oro.text.*;

/**
 *<pre>
 *   Main Attribute matching class
 *   Usage : 
 *      1. create new KeywordParser
 *      2. Add keyword information
 *      3. Parse a string (creates a KeywordMatches structure)
 *      4. use the KeywordMatches structure to compare two matches
 *
 *      Typically #2 is done by sub classing the KeywordParser class (cf. RoadKeywordParser)
 *</pre>
 */
public class KeywordParser {
    
    // These each have one element for each keyword entered
    
  private java.util.ArrayList keywords =new java.util.ArrayList();   //actual keyword (ie. 'rd')
  private java.util.ArrayList codes    =new java.util.ArrayList();  //identifier of this keyword (ie. Integer(15) = 'rd','road',..)
 private  java.util.ArrayList type     =new java.util.ArrayList();  // type of match (ie. 'rdtype','rddesc','suffix')
    
 private   int numberElements = 0;
    
     //org.apache.oro.text.perl.Perl5Util REutil = new Perl5Util(new PatternCacheLRU(500));

    /** Creates new KeywordParser */
    public KeywordParser() {
    }
    
   
    /**
     *  Add a new keyword to search for <br>
     *  ie. you want to find "rd", and label it as 'rd_type' 1.<br>
     *    .add("rd",1, "rd_type");<br>
     *
     *  @param key actual text to search for
     *  @param codeNumber unique id for this class of match
     *  @param elementType the class of this keyword
     */
    public void add(String key, int codeNumber, String elementType)
    {
        keywords.add(key );
        codes.add   ( new Integer(codeNumber));
        type.add    (elementType);
        numberElements++;
    }
    
    
    /**
     *  Main parser function - given a string parse it.<br>
     *  Returns a list of possible matches - you'll probably<br>
     *  want to .unique() the result.<br>
     *
     * @param name string to search for keywords in.
     */
    public KeywordMatches matchAll(String name)
    {
          KeywordMatch initialmatch = new KeywordMatch(name); 
          
          return matchAll(initialmatch);
    }
    
    
    /**
     *<pre>
     *  Main work horse of the parser - this is recursive.
     *  
     *  Lets look at an example:
     *   "SW Marine Drive" + empty oldmatch
     *   match is done on "SW Marine Drive" and returns:
     *        SW + "Marine Drive"
     *        Drive + "SW Marine"
     *   These are added to the result
     *   Each of the results of the match is recursed
     *   
     *   -->  SW + "Marine Drive"
     *        match is done on "Marine Drive" and returns:
     *         {SW}  + Drive + "Marine"
     *
     *   and so on
     *</pre>
     * @param oldmatch - any matches already found in the string
     */
    public KeywordMatches matchAll(KeywordMatch oldmatch)
    {
        KeywordMatches newmatches;
        KeywordMatches result = new KeywordMatches();
        int t;
        
        newmatches = match(oldmatch);
            //recurse on all these
        result.add(oldmatch);  // add original
        result.addAll(newmatches); // add all zero-level matches
        for (t=0;t<newmatches.size();t++)
        {
            result.addAll( matchAll((KeywordMatch)newmatches.get(t)) );
        }
        return result;
    }
    
    
    
    /**
     *  does a non-recurive match on the string
     */
    public KeywordMatches match(String name)
    {
        KeywordMatch initialmatch = new KeywordMatch(name);    
        
        return this.match(initialmatch);      
    }
    
   
   /**
    *  Actual matching is done with this.<br>
    *  finds all the keywords in oldmatch.getText() and returns a list of new matches<br>
    *
    *  NOTE: the new matches have the historical (ie. earlier matches) information<br>
    *         from oldmatch in them.<br>
    *
    * @param oldmatch old KeywordMatch object to base the new matches on
    */
    public KeywordMatches match(KeywordMatch oldmatch)
    {
        KeywordMatches result = new KeywordMatches();
        int t;
        String text = oldmatch.getText();
        
        //search for match.  If there is one, clone the oldmatch, add the new match, re-set the streetname, then look
        // for more matches.
        
        for (t=0;t<numberElements;t++)
        {
            //does this element match the string?
            // -- can we find this keyword in the oldmatch.text?
            String key;
            String pattern;
            KeywordMatch newKeywordMatch;
        
               
            key = (String) keywords.get(t);
            pattern = key;
                
            if (TextHandler.match(text,pattern))
            {
                //we have a match here - add it to the results list.
                String newText = TextHandler.remove(text,pattern) ;
                newText =  TextHandler.trimWhite(newText);;
                newKeywordMatch = oldmatch.copy();
               // newKeywordMatch.print();
                newKeywordMatch.add(key,(Integer)codes.get(t), (String) type.get(t) ,  newText.trim());
               // oldmatch.print();
               // newKeywordMatch.print();
                result.add(newKeywordMatch);
            }
            
        }
        
        return result;
    }
    
    
    /**
     *  helper function - obliterate all the keywords in a string<br>
     *
     *  BUG: will only remove 1st occurance of keyword in a string<br>
     *     ie. "st peters st" will return "peters st"<br>
     *
     *  @param full text to look for keywords in
     */
    public  KeywordMatches removeMatches(String full)
    {
        int t;
        KeywordMatches result = new KeywordMatches();
        
        for (t=0;t<numberElements;t++)
        {
                String key;
                String pattern;
        
               
                key = (String) keywords.get(t);
                pattern =  key ;
                full =  TextHandler.remove(full,pattern);
        }
        full = TextHandler.trimWhite(full);
        result.add(new KeywordMatch(full) );  // return a single result
        return result; 
    }
    

   
    
}

/*
 * KeywordMatches.java
 *
 * Created on October 28, 2002, 2:27 PM
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
 *<pre>
 *  This is a utility class for the KeywordParser that keeps a list
 *   of KeywordMatches.  For example, "SW Marine Drive" has several different
 *   interpretations:
 *    "SW Marine Drive"
 *    SW + Drive + "Marine"
 *    SW + "Marine Drive"
 *    Drive + "SW Marine"
 *
 *    The KeywordMatches class would store these 4 KeywordMatches.
 *
 *  THis class behaves much like a list with several extra features.
 * </pre>
 */
public class KeywordMatches {

    private java.util.ArrayList kms =new java.util.ArrayList() ;
    
    
    /**
     *  Add a new KeywordMatch to this list of KeywordMatches
     *
     * @parm km KeywordMatch to add
     */
    public void add (KeywordMatch km)
    {
        kms.add(km);
    }
    
    /**
     *  Add a set of KeywordMatches to this list
     *
     * @param newKms set of KeywordMatches to add
     */
    public void addAll (KeywordMatches newKms)
    {
        kms.addAll(newKms.kms);
    }
    
    
    /**
     * Return how many matches are in this list
     */
    public int size()
    {
        return kms.size();
    }
    
    /**
     * Get a particular KeywordMatch
     * 
     * @parm i index to retrieve
     */
    public KeywordMatch get(int i)
    {
        return (KeywordMatch) kms.get(i);
    }
    
    
   /**
    *  DEBUG function - print out the matches in this list.
    */
   public void print()
   {
       int t;
       int u;
       KeywordMatch km;
       
       for (t=0;t<kms.size(); t++)
       {
            System.out.println("------------------ match "+t+":");
            
            km = (KeywordMatch) kms.get(t);
            km.print();
           
            System.out.println("------------------");
       }
    }
    
    
    /**
     *<pre>
     *  Removes duplicate KeywordMatches from the list
     *  ie.
     *   "SW Marine Drive"
     *   SW + Drive + "Marine"   ** 
     *   Drive +SW + "Marine"    ** same as above
     *   SW + "Marine Drive"
     *   Drive + "SW Marine"
     *</pre>
     */
    public KeywordMatches unique()
    {
        int t,u;
        KeywordMatches result = new KeywordMatches();
        boolean[] delete = new boolean[kms.size()];
        KeywordMatch kwm1, kwm2;
        
        for(t=0;t<kms.size();t++) //for each match
        {
            delete[t] =false;
        }
        
        for(t=0;t<kms.size();t++) //for each match
        {
            //scan ahead
            kwm1 = (KeywordMatch) kms.get(t);
            for (u=(t+1);u<kms.size();u++)
            {
                kwm2 = (KeywordMatch) kms.get(u);
                //if this is the same as the scaned one, we can delete it
                if (!(delete[u])) //not already deleted
                {
                    if (kwm2.isSame(kwm1))
                    {
                        delete[u] =true;
                    }
                }
            }
        }
        
        for(t=0;t<kms.size();t++) //for each match
        {
            if (!(delete[t]))
            {
                result.add ( (KeywordMatch) kms.get(t)   );
            }
        }
        
        return result;
        
    }
 
    /*
     *<pre>
     * find the best guess at the road name (or whatever you're matching for).
    *   This guess is the smallest Text property in the matchlist
    *  I.e. "Niagara St" -> "Niagara St" and "Niagara" + "St"
    *       so the best RoadName is "Niagara" since its shorter than "Niagara St"
    *  This could give misleading results
     *<pre>
     */
    public String getBestText()
    {
            int t;
            String result = "";
            int minlen = 100000;
            KeywordMatch km;
            
            for(t=0;t<kms.size();t++)
            {
                km = (KeywordMatch)kms.get(t);
                if (km.text.length() < minlen)
                {
                    if (km.text.length() != 0)
                    {
                        result = km.text;
                        minlen = km.text.length();
                    }
                }
            }
            return result;               
    }
    
    /**
     * compare two matches (simple compare)<bR>
     * returns 1 if getBestText() of the two keywordmatches are the same, otherwise<br>
     * if they are off by 1 letter (levenshtein distance 1), it returns 0.5.<br>
     *  otherwise (levenshtein distance >1) it returns 0<br>
     */
    public double compareText(KeywordMatches other)
    {
        String t1 = this.getBestText();
        String t2 = other.getBestText();
        
        if (t1.equalsIgnoreCase(t2))
        {
            return 1.0;
        }
        
        if (Math.abs(t1.length()-t2.length()) > 1)
        {
            return 0.0; // off by >1 letter -> levenshtein distance >1
        }
        // we need to compute the levenshtein distance between t1 and t2
        if (levenshtein(t1,t2) == 1)
            return 0.5;
        else
            return 0.0;
    }
    
    
   /**
    * help function for levenshtein
    * return the minimum of 3 numbers
    */
  private static int min (int a, int b, int c) 
  {
  int minimum;

    minimum = a;
    if (b < minimum) {
      minimum = b;
    }
    if (c < minimum) {
      minimum = c;
    }
    return minimum;
  }

  /** calculate the levenshtein distance <br>
   * based on description at http://www.merriampark.com/ld.htm  <br>
   * case sensitive<br>
   *<br>
   * Calculate the number of insert, replace and delete operations needed to transform string s into string t. 
   */
   public static int levenshtein(String s, String t)
   {
       int n,m;
       int matrix[][];
       int tt,u,i,j;
       int cost;
       char s_char, t_char;
       
       n = s.length();
       m = t.length();
       
       
       if (n ==0) 
           return m;
       if (m==0)
           return n;
      matrix =  new int[n+1][m+1];
      
    for (tt = 0; tt <= n; tt++) 
    {
        matrix[tt][0] = tt;
    }

    for (u = 0; u <= m; u++) 
    {
        matrix[0][u] = u;
    }

    for (i=1;i<=n;i++)
    {
        s_char = s.charAt(i-1);
        for(j=1;j<=m;j++)
        {
            t_char = t.charAt(j-1);
            if (s_char == t_char)
                cost = 0;
            else
                cost = 1;
            matrix[i][j] = min(matrix[i-1][j]+1,
                                matrix[i][j-1] +1,
                                matrix[i-1][j-1] + cost);
            
        }
    }
      
      return matrix[n][m];  
   }
    
    /** Creates new KeywordMatches */
    public KeywordMatches() {
    }

    
}

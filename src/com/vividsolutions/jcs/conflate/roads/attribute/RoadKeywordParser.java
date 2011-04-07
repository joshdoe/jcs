/*
 * RoadKeywordParser.java
 *
 * Created on November 6, 2002, 3:49 PM
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
 *   KeywordParser class that has a bunch of built in keywords
 * 
 *   parses into these categories:
 *    rd_descript  - ie. 'overpass', 'onramp'
 *    rd_type      - ie. 'rd' , 'st'
 *    suffixes     - ie. 'East' , 'NW'
 *</pre>
 */
public class RoadKeywordParser extends KeywordParser {

    /** Creates new RoadKeywordParser */
    public RoadKeywordParser() {
        this.addRoadKeywords();
    }

     public void addRoadKeywords()
    {
            //description of roads - ie. bypass and overpass
     this.add("bridge",7,"rd_descript");
     this.add("bypass",8,"rd_descript");
     this.add("diversion",19,"rd_descript");
     this.add("divers",19,"rd_descript");
     this.add("offramp",48,"rd_descript");
     this.add("onramp",49,"rd_descript");
     this.add("overpass",50,"rd_descript");
     this.add("rest area",61,"rd_descript");
     this.add("restarea",61,"rd_descript");
     this.add("tunnel",80,"rd_descript");
     this.add("underpass",81,"rd_descript");
     this.add("viaduct",82,"rd_descript");
     this.add("byp",8,"rd_descript");
     this.add("bypa",8,"rd_descript");
     this.add("bypas",8,"rd_descript");
     this.add("extended",22,"rd_descript");
     this.add("extn",22,"rd_descript");
     this.add("extnsn",22,"rd_descript");
     this.add("exts",22,"rd_descript");
     this.add("extension",22,"rd_descript");
     this.add("ext",22,"rd_descript");
     this.add("overps",50,"rd_descript");
     this.add("ovps",50,"rd_descript");
     this.add("ovrps",50,"rd_descript");
     this.add("vdct",82,"rd_descript");
     this.add("via",82,"rd_descript");
     this.add("viadct",82,"rd_descript");
     
        //type of roads
     this.add("airport",0,"rd_type");
     this.add("airstrip",1,"rd_type");
     this.add("alley",2,"rd_type");
     this.add("avenue",3,"rd_type");
     this.add("ave",3,"rd_type");
     this.add("bay",4,"rd_type");
     this.add("bcfsrs",5,"rd_type");
     this.add("boulevard",6,"rd_type");
     this.add("blvd",6,"rd_type");
   
     this.add("campground",9,"rd_type");
     this.add("campgrnd",9,"rd_type");
     this.add("causeway",10,"rd_type");
     this.add("circle",11,"rd_type");
     this.add("cir",11,"rd_type");
     this.add("close",12,"rd_type");
     this.add("connector",13,"rd_type");
     this.add("conn",13,"rd_type");
     this.add("court",14,"rd_type");
     this.add("crt",14,"rd_type");
     this.add("cove",15,"rd_type");
     this.add("crescent",16,"rd_type");
     this.add("cres",16,"rd_type");
     this.add("culvert",17,"rd_type");
     this.add("dale",18,"rd_type");

     this.add("drive",20,"rd_type");
     this.add("dr",20,"rd_type");
     this.add("esplanade",21,"rd_type");
     this.add("espl",21,"rd_type");
 
     this.add("ferry",23,"rd_type");
     this.add("forest service road",24,"rd_type");
     this.add("fsr",24,"rd_type");
     this.add("freeway",25,"rd_type");
     this.add("fwy",25,"rd_type");
     this.add("frontage rd",26,"rd_type");
     this.add("frtg",26,"rd_type");
     this.add("garden",27,"rd_type");
     this.add("gdn",27,"rd_type");
     this.add("gardens",28,"rd_type");
     this.add("gdns",28,"rd_type");
     this.add("gate",29,"rd_type");
     this.add("glen",30,"rd_type");
     this.add("green",31,"rd_type");
     this.add("grove",32,"rd_type");
     this.add("height",33,"rd_type");
     this.add("ht",33,"rd_type");
     this.add("heights",34,"rd_type");
     this.add("hts",34,"rd_type");
     this.add("highway",35,"rd_type");
     this.add("hwy",35,"rd_type");
     this.add("hill",36,"rd_type");
     this.add("hospital",37,"rd_type");
     this.add("landing",38,"rd_type");
     this.add("lane",39,"rd_type");
     this.add("lookout",40,"rd_type");
     this.add("lkout",40,"rd_type");
     this.add("loop",41,"rd_type");
     this.add("mainline",42,"rd_type");
     this.add("mall",43,"rd_type");
     this.add("marina",44,"rd_type");
     this.add("mews",45,"rd_type");
     this.add("mhp",46,"rd_type");
  
     this.add("park",51,"rd_type");
     this.add("pk",51,"rd_type");
     this.add("parkway",52,"rd_type");
     this.add("pky",52,"rd_type");
     this.add("passage",53,"rd_type");
     this.add("pass",53,"rd_type");
     this.add("place",54,"rd_type");
     this.add("pl",54,"rd_type");
     this.add("plaza",55,"rd_type");
     this.add("point",56,"rd_type");
     this.add("pt",56,"rd_type");
     this.add("promenade",57,"rd_type");
     this.add("prom",57,"rd_type");
     this.add("quay",58,"rd_type");
     this.add("ramp",59,"rd_type");
     this.add("recreation site",60,"rd_type");
     this.add("recsite",60,"rd_type");

     this.add("ridge",62,"rd_type");
     this.add("rise",63,"rd_type");
     this.add("road",64,"rd_type");
     this.add("rd",64,"rd_type");
     this.add("route",65,"rd_type");
     this.add("rte",65,"rd_type");
     this.add("row",66,"rd_type");
     this.add("rue",67,"rd_type");
     this.add("school",68,"rd_type");
     this.add("snowshed",69,"rd_type");
     this.add("square",70,"rd_type");
     this.add("sq",70,"rd_type");
     this.add("street",71,"rd_type");
     this.add("st",71,"rd_type");
     this.add("subdivision",72,"rd_type");
     this.add("subdiv",72,"rd_type");
     this.add("terminal",73,"rd_type");
     this.add("terminus",74,"rd_type");
     this.add("terrace",75,"rd_type");
     this.add("terr",75,"rd_type");
     this.add("thruway",76,"rd_type");
     this.add("trail",77,"rd_type");
     this.add("trail head",78,"rd_type");
     this.add("trailhead",78,"rd_type");
     this.add("trailer court",79,"rd_type");
     this.add("trailercrt",79,"rd_type");

     this.add("view",83,"rd_type");
     this.add("walk",84,"rd_type");
     this.add("way",85,"rd_type");
     this.add("weigh scale",86,"rd_type");
     this.add("weighscale",86,"rd_type");
     this.add("wharf",87,"rd_type");
     this.add("wynd",88,"rd_type");
   
     this.add("al",2,"rd_type");
     this.add("allee",2,"rd_type");
     this.add("ally",2,"rd_type");
     this.add("aly",2,"rd_type");
     this.add("av",3,"rd_type");
     this.add("avd",3,"rd_type");
     this.add("aveflr",3,"rd_type");
     this.add("aven",3,"rd_type");
     this.add("avenida",3,"rd_type");
     this.add("avnue",3,"rd_type");
     this.add("bd",6,"rd_type");
     this.add("bl",6,"rd_type");
     this.add("blv",6,"rd_type");
     this.add("boul",6,"rd_type");
     this.add("bvd",6,"rd_type");
     this.add("bvld",6,"rd_type");

     this.add("ci",11,"rd_type");
     this.add("circ",11,"rd_type");
     this.add("corcle",11,"rd_type");
     this.add("cr",11,"rd_type");
     this.add("crcl",11,"rd_type");
     this.add("crcle",11,"rd_type");
     this.add("crl",11,"rd_type");
     this.add("crescnt",16,"rd_type");
     this.add("cresent",16,"rd_type");
     this.add("crscnt",16,"rd_type");
     this.add("cswy",10,"rd_type");
     this.add("ct",14,"rd_type");
     this.add("driv",20,"rd_type");
     this.add("drives",20,"rd_type");
     this.add("drunit",20,"rd_type");
     this.add("drv",20,"rd_type");
     this.add("drve",20,"rd_type");

     this.add("freewy",25,"rd_type");
     this.add("frw",25,"rd_type");
     this.add("frwy",25,"rd_type");
     this.add("fw",25,"rd_type");
     this.add("gln",30,"rd_type");
     this.add("hghwy",35,"rd_type");
     this.add("hghway",35,"rd_type");
     this.add("hgwy",35,"rd_type");
     this.add("highw",35,"rd_type");
     this.add("hiway",35,"rd_type");
     this.add("hiwy",35,"rd_type");
     this.add("hw",35,"rd_type");
     this.add("hway",35,"rd_type");
     this.add("hy",35,"rd_type");
     this.add("la",39,"rd_type");
     this.add("ln",39,"rd_type");
     this.add("lne",39,"rd_type");
     this.add("loops",41,"rd_type");
     this.add("lp",41,"rd_type");
   
     this.add("parks",51,"rd_type");
     this.add("prk",51,"rd_type");
     this.add("pike",51,"rd_type");
     this.add("pikes",51,"rd_type");
     this.add("pke",51,"rd_type");
     this.add("parkwy",52,"rd_type");
     this.add("pkw",52,"rd_type");
     this.add("pkwy",52,"rd_type");
     this.add("prkway",52,"rd_type");
     this.add("prkwy",52,"rd_type");
     this.add("pw",52,"rd_type");
     this.add("pwy",52,"rd_type");
     this.add("py",52,"rd_type");
     this.add("pla",54,"rd_type");
     this.add("plac",54,"rd_type");
     this.add("plc",54,"rd_type");
     this.add("plce",54,"rd_type");
     this.add("plaz",55,"rd_type");
     this.add("plz",55,"rd_type");
     this.add("plza",55,"rd_type");
     this.add("pz",55,"rd_type");
     this.add("pnt",56,"rd_type");
     this.add("rdbox",64,"rd_type");
     this.add("rdrr",64,"rd_type");
     this.add("rds",64,"rd_type");
     this.add("sqr",70,"rd_type");
     this.add("sqre",70,"rd_type");
     this.add("squ",70,"rd_type");
     this.add("squares",70,"rd_type");
     this.add("suqares",70,"rd_type");
     this.add("stapt",71,"rd_type");
     this.add("stbox",71,"rd_type");
     this.add("stbx",71,"rd_type");
     this.add("steet",71,"rd_type");
     this.add("stlot",71,"rd_type");
     this.add("stpobox",71,"rd_type");
     this.add("str",71,"rd_type");
     this.add("strd",71,"rd_type");
     this.add("streetbox",71,"rd_type");
     this.add("streets",71,"rd_type");
     this.add("strfd",71,"rd_type");
     this.add("strr",71,"rd_type");
     this.add("strt",71,"rd_type");
     this.add("te",75,"rd_type");
     this.add("ter",75,"rd_type");
     this.add("terrce",75,"rd_type");
     this.add("throughway",76,"rd_type");
     this.add("thruwy",76,"rd_type");
     this.add("thwy",76,"rd_type");
     this.add("tl",77,"rd_type");
     this.add("tr",77,"rd_type");
     this.add("trl",77,"rd_type");
     this.add("unp",81,"rd_type");

     this.add("walks",84,"rd_type");
     this.add("wk",84,"rd_type");
     this.add("wy",85,"rd_type");
     this.add("tce",75,"rd_type");
     
     
            //directions
     this.add("east",1,"suffixes");
     this.add("e",1,"suffixes");
     this.add("north",2,"suffixes");
     this.add("n",2,"suffixes");
     this.add("northeast",3,"suffixes");
     this.add("ne",3,"suffixes");
     this.add("northwest",4,"suffixes");
     this.add("nw",4,"suffixes");
     this.add("south",5,"suffixes");
     this.add("s",5,"suffixes");
     this.add("southeast",6,"suffixes");
     this.add("se",6,"suffixes");
     this.add("southwest",7,"suffixes");
     this.add("sw",7,"suffixes");
     this.add("west",8,"suffixes");
     this.add("w",8,"suffixes");
    }
    
}

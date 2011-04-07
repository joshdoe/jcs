

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

package com.vividsolutions.jcs.conflate.roads;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.geom.Angle;
import com.vividsolutions.jump.feature.*;

import com.vividsolutions.jump.task.*;
import com.vividsolutions.jcs.debug.DebugFeature;
import com.vividsolutions.jcs.algorithm.linearreference.LocatePoint;
/**
 * Conflates two road networks
 */
public class RoadMatcher {

  public static final int REFERENCE = 0;
  public static final int SUBJECT = 1;

  private static final GeometryFactory fact = new GeometryFactory();
  //private static final EdgeMatchIndFactory edgeMatchIndFact = new EdgeMatchIndFactory();

  private TaskMonitor monitor;
  private FeatureCollection[] inputFC = new FeatureCollection[2];
  private RoadNetwork[] network = new RoadNetwork[2];
  private RoadMatches edgeMatches = new RoadMatches();

  public RoadMatcher(FeatureCollection ref, FeatureCollection subject, TaskMonitor monitor)
  {
    this.monitor = monitor;
    inputFC[0] = ref;
    inputFC[1] = subject;
    network[0] = new RoadNetwork(ref);
    network[1] = new RoadNetwork(subject);
  }

  public RoadNetwork getNetwork(int i) { return network[i]; }
/* NOT USED?

  public FeatureCollection getMatchesFC()
  {
    match();
    return matchesFC;
  }
*/
  public void match()
  {
    monitor.report("Merging Edges");
    //EdgeMerger merger = new EdgeMerger(network[0].graph);
    RoadEdgeMerger merger = new RoadEdgeMerger();
    merger.merge(network[0]);
    merger.merge(network[1]);

    monitor.report("Matching Nodes");
    RoadNodeMatcher nodeMatcher = new RoadNodeMatcher();
    nodeMatcher.match(this);

    NodesAngleDistanceMatcher nodeAngleDistMatcher = new NodesAngleDistanceMatcher();
    nodeAngleDistMatcher.match(this);
//saveNodeMatches();

    clearNonMutualNodeMatches();

//    monitor.report("Matching Edges using node matches");
//    EdgesMatcher edgesMatcher = new EdgesMatcher();
//    edgesMatcher.match(this);

    monitor.report("Matching Edges using edge distance");
    computeMinDistEdgeMatches();
    //clearNodeInconsistentEdgeMatches();

    monitor.report("Matching Edges using node matches");
    EdgesMatcher edgesMatcher = new EdgesMatcher();
    edgesMatcher.match(this);

    createEdgeMatches();
  }


  public FeatureCollection getBestNodeMatches()
  {
    BestNodeMatchInd indFactory = new BestNodeMatchInd();
    indFactory.add(network[0].graph.nodeIterator());
    indFactory.add(network[1].graph.nodeIterator());
    return indFactory.getFC();
  }

  public FeatureCollection getTopNodeMatches()
  {
    TopNodeMatchInd indFactory = new TopNodeMatchInd();
    indFactory.add(network[0].graph.nodeIterator());
    indFactory.add(network[1].graph.nodeIterator());
    return indFactory.getFC();
  }

  public FeatureCollection getAllEdgeMatches()
  {
    AllEdgeMatchInd indFactory = new AllEdgeMatchInd();
    indFactory.add(network[0].graph.edgeIterator());
    indFactory.add(network[1].graph.edgeIterator());
    return indFactory.getFC();
  }
  public FeatureCollection getBestEdgeMatches()
  {
    BestEdgeMatchInd indFactory = new BestEdgeMatchInd();
    indFactory.add(network[0].graph.edgeIterator());
    indFactory.add(network[1].graph.edgeIterator());
    return indFactory.getFC();
  }
  public FeatureCollection getTopEdgeMatches()
  {
    TopEdgeMatchInd indFactory = new TopEdgeMatchInd();
    indFactory.add(network[0].graph.edgeIterator());
    indFactory.add(network[1].graph.edgeIterator());
    return indFactory.getFC();
  }
/*
  public FeatureCollection getBestEdgeMatches()
  {
    FeatureSchema fs = DebugFeature.createGeometryMsgFeatureSchema();
    FeatureDataset fc = new FeatureDataset(fs);

    getBestEdgeMatches(network[0].graph.edgeIterator(), fc);
    getBestEdgeMatches(network[1].graph.edgeIterator(), fc);
    return fc;
  }
  private void getBestEdgeMatches(Iterator i, FeatureCollection fc)
  {
    while (i.hasNext()) {
      RoadEdge edge = (RoadEdge) i.next();
      Matches matchList = edge.getMatches();
      MatchValue mv = matchList.getBestMatch();
      if (mv == null) return;
      fc.add(AllEdgeMatchInd.getIndicator(fc.getFeatureSchema(), edge, mv));
    }
  }
*/

  private void computeMinDistEdgeMatches()
  {
    /*
    MinDistanceEdgeMatcher minDistEdgeMatcher
        = new MinDistanceEdgeMatcher(network[0].getUnmatchedEdges(), network[1].getUnmatchedEdges());
    minDistEdgeMatcher.match();
    */

    MinDistanceAllEdgesMatcher minDistAllEdgesMatcher
        = new MinDistanceAllEdgesMatcher(
        network[0].getEdges(),
        network[1].getEdges());
    minDistAllEdgesMatcher.match();
    minDistAllEdgesMatcher.findMutualBestMatches();
  }

  private void createEdgeMatches()
  {
    for (Iterator i = network[1].edgeIterator(); i.hasNext(); )
    {
      RoadEdge edge = (RoadEdge) i.next();
      RoadEdge matchEdge = edge.getMatch();
      if (matchEdge != null) {
        // order is important here - <ref, sub>
        edgeMatches.add(new RoadEdgeMatch(matchEdge, edge));
      }
    }
  }

  public RoadMatches getEdgeMatchIndicators()
  {
    return edgeMatches;
  }
  /*
  public FeatureCollection OLDgetEdgeMatchIndicators()
  {
    FeatureSchema fs = DebugFeature.createGeometryMsgFeatureSchema();
    FeatureDataset fc = new FeatureDataset(fs);

    for (Iterator i = network[1].edgeIterator(); i.hasNext(); )
    {
      RoadEdge edge = (RoadEdge) i.next();
      RoadEdge match = edge.getMatch();
      if (match != null) {
        Geometry g = edgeMatchIndFact.getIndicator(
            (LineString) edge.getGeometry(),
            (LineString) match.getGeometry());
        Feature f = new BasicFeature(fc.getFeatureSchema());
        f.setGeometry(g);
        f.setAttribute(DebugFeature.MESG_ATTR_NAME, "val=" + (int) (1000.0 * edge.getMatchDistance()));
        fc.add(f);
      }
    }
    return fc;
  }
  */
  private void clearNonMutualNodeMatches()
  {
    clearNonMutualNodeMatches(network[0].graph.nodeIterator());
    clearNonMutualNodeMatches(network[0].graph.nodeIterator());
  }

  private void clearNonMutualNodeMatches(Iterator nodeIt)
    {
    // clear any node matches that are not mutual
    while (nodeIt.hasNext()) {
      RoadNode node = (RoadNode) nodeIt.next();
      RoadNode matchNode = node.getMatch();
      if (matchNode != null && matchNode.getMatch() != node) {
        node.clearMatch();
      }
    }
  }

  private void clearNodeInconsistentEdgeMatches()
  {
    clearNodeInconsistentEdgeMatches(network[0].graph.edgeIterator());
    clearNodeInconsistentEdgeMatches(network[0].graph.edgeIterator());
  }

  private void clearNodeInconsistentEdgeMatches(Iterator edgeIt)
  {
    int count = 0;
    // clear any node matches that are not mutual
    while (edgeIt.hasNext()) {
      RoadEdge edge = (RoadEdge) edgeIt.next();
      RoadEdge matchEdge = edge.getMatch();

      if (matchEdge != null && ! RoadNode.isNodeSetConsistent(edge.getNodes(), matchEdge.getNodes())) {
        edge.clearMatch();
        matchEdge.clearMatch();
        count++;
      }
    }
    System.out.println(count + " node-inconsistent edge matches cleared");
  }


  /**
   * Get a unique set of all nodes attached to matched edges
   * @return
   */
  private Collection getMatchedEdgeNodes()
  {
    Set nodeSet = new HashSet();
    for (Iterator i = network[1].edgeIterator(); i.hasNext(); )
    {
      RoadEdge edge = (RoadEdge) i.next();
      if (edge.getMatch() != null) {
        RoadNode[] node = edge.getNodes();
        nodeSet.add(node[0]);
        nodeSet.add(node[1]);
      }
    }
    return nodeSet;
  }

  public FeatureCollection getMatchedEdgeNodeVectors()
  {
    return getNodeMatchVectors(getMatchedEdgeNodes().iterator());
  }

  public FeatureCollection getNodeMatchVectors()
  {
    return getNodeMatchVectors(network[1].graph.nodeIterator());
  }

  private FeatureCollection getNodeMatchVectors(Iterator nodeIt)
  {
    String matchValueCol = "MATCH_VALUE";
    FeatureSchema matchIndSchema = new FeatureSchema();
    matchIndSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    matchIndSchema.addAttribute(matchValueCol, AttributeType.DOUBLE);
    FeatureDataset matchIndFC = new FeatureDataset(matchIndSchema);

    //List matchInd = new ArrayList();
    for (; nodeIt.hasNext(); ) {
      RoadNode subNode = (RoadNode) nodeIt.next();
      RoadNode matchNode = subNode.getMatch();
      if (matchNode != null) {
        Coordinate[] coord = { subNode.getCoordinate(), matchNode.getCoordinate() };
        Geometry matchLine = fact.createLineString(coord);

        Feature feature = new BasicFeature(matchIndFC.getFeatureSchema());
        feature.setGeometry(matchLine);
        feature.setAttribute(matchValueCol, new Double(subNode.getMatchValue()));
        matchIndFC.add(feature);

        /*
        System.out.println("LINESTRING("
                           + node.getCoordinate().x
                           + " "
                           + node.getCoordinate().y
                           + ","
                           + matchNode.getCoordinate().x
                           + " "
                           + matchNode.getCoordinate().y
                           + ")"
                           );
        */
      }
    }
    return matchIndFC;
    //return FeatureDatasetFactory.createFromGeometryWithLength(matchInd, "length");
  }

  public FeatureCollection getEdgeMatchReportFC()
  {
    EdgeMatchReportBuilder reportBuilder
        = new EdgeMatchReportBuilder(inputFC[0], inputFC[1], edgeMatches);
    return reportBuilder.createReportFC();
  }
/*
  private void matchClosestNodes(RoadNetwork network1, RoadNetwork network2)
  {
    for (Iterator i = network1.graph.nodeIterator(); i.hasNext(); ) {
      RoadNode node = (RoadNode) i.next();
      double queryDist = node.getMaxAdjacentNodeDistance();
      List candidateSet = network2.nodesWithinDistance(node.getCoordinate(), queryDist);
      RoadNode matchNode = findClosestNode(node, candidateSet);
      node.setMatch(matchNode);
    }
  }

  private static RoadNode findClosestNode(RoadNode node, List candidateSet)
  {
    RoadNode match = null;
    double maxMatchValue = 0.0;
    Coordinate pt = node.getCoordinate();

    for (Iterator i = candidateSet.iterator(); i.hasNext(); ) {
      RoadNode candidateNode = (RoadNode) i.next();
      double dist = candidateNode.getCoordinate().distance(pt);
      // match function is 1 if position is identical, falling to 0 at infinity
      double matchValue = (dist == 0.0 ? 1.0 : 1 / dist);
      if (match == null || matchValue > maxMatchValue) {
        match = candidateNode;
        maxMatchValue = matchValue;
      }
    }
    return match;
  }
*/
}

abstract class BaseNodeMatchInd
{
  private FeatureCollection fc;

  public BaseNodeMatchInd()
  {
    FeatureSchema fs = DebugFeature.createGeometryMsgFeatureSchema();
    fc = new FeatureDataset(fs);
  }

  public FeatureCollection getFC() { return fc; }

  abstract Iterator getMatchIterator(Matches matchList);

  public void add(Iterator i)
  {
    while (i.hasNext()) {
      RoadNode node = (RoadNode) i.next();
      Matches matchList = node.getMatches();
      for (Iterator j = getMatchIterator(matchList); j.hasNext(); ) {
        MatchValue mv = (MatchValue) j.next();
        if (mv != null) {
          fc.add(getIndicator(fc.getFeatureSchema(), node, mv));
        }
      }
    }
  }
  public static Feature getIndicator(FeatureSchema fs, RoadNode node, MatchValue mv)
  {
    Feature f = DebugFeature.createLineSegmentFeature(fs,
        node.getCoordinate(),
        ((RoadNode) mv.getMatch()).getCoordinate(),
        "val=" + (int) (1000.0 * mv.getValue()));
    return f;
  }
}
class BestNodeMatchInd extends BaseNodeMatchInd
{
  private List dummyList = new ArrayList();

  BestNodeMatchInd()
  {
    super();
    // add a dummy entry to force list to have one element
    dummyList.add(new Double(0.0));
  }
  Iterator getMatchIterator(Matches matchList)
  {
    dummyList.set(0, matchList.getBestMatch());
    return dummyList.iterator();
  }
}
class TopNodeMatchInd extends BaseNodeMatchInd
{
  Iterator getMatchIterator(Matches matchList)
  {
    return matchList.getBestMatches().iterator();
  }
}

abstract class BaseEdgeMatchInd
{
  private FeatureCollection fc;

  public BaseEdgeMatchInd()
  {
    FeatureSchema fs = DebugFeature.createGeometryMsgFeatureSchema();
    fc = new FeatureDataset(fs);
  }
  public FeatureCollection getFC() { return fc; }
  abstract Iterator getMatchIterator(Matches matchList);

  public void add(Iterator i)
  {
    while (i.hasNext()) {
      RoadEdge edge = (RoadEdge) i.next();
      Matches matches = edge.getMatches();
      for (Iterator j = getMatchIterator(matches); j.hasNext(); ) {
        MatchValue mv = (MatchValue) j.next();
        if (mv != null) {
          fc.add(getIndicator(fc.getFeatureSchema(), edge, mv));
        }
      }
    }
  }
  public static Coordinate lineCentrePoint(LineString line)
  {
    double len = line.getLength();
    LocatePoint locPt = new LocatePoint(line, len / 2.0);
    return locPt.getPoint();

  }

  public static Feature getIndicator(FeatureSchema fs, RoadEdge edge, MatchValue mv)
  {
    Coordinate mid0 = lineCentrePoint((LineString) edge.getGeometry());
    Coordinate mid1 = lineCentrePoint((LineString) ((RoadEdge) mv.getMatch()).getGeometry());
    Feature f = DebugFeature.createLineSegmentFeature(fs,
        mid0, mid1,
        "val=" + (int) (1000.0 * mv.getValue()));
    return f;
  }
}
class BestEdgeMatchInd extends BaseEdgeMatchInd
{
  private List dummyList = new ArrayList();

  BestEdgeMatchInd()
  {
    super();
    // add a dummy entry to force list to have one element
    dummyList.add(new Double(0.0));
  }
  Iterator getMatchIterator(Matches matchList)
  {
    dummyList.set(0, matchList.getBestMatch());
    return dummyList.iterator();
  }
}
class TopEdgeMatchInd extends BaseEdgeMatchInd
{
  Iterator getMatchIterator(Matches matchList)
  {
    return matchList.getBestMatches().iterator();
  }
}
class AllEdgeMatchInd extends BaseEdgeMatchInd
{
  Iterator getMatchIterator(Matches matchList)
  {
    return matchList.getMatches().iterator();
  }
}

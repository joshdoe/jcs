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

package com.vividsolutions.jcs.geom;

import java.util.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.Assert;
import com.vividsolutions.jump.util.CoordinateArrays;


/**
 * Geometry objects are unmodifiable; this class allows you to "modify" a Geometry
 * in a sense -- the modified Geometry is returned as a new Geometry.
 * The new Geometry's #isValid should be checked.
 */
public class GeometryEditor
{
    private GeometryFactory factory;

    public GeometryEditor()
    {
      factory = new GeometryFactory();
    }

    public GeometryEditor(GeometryFactory factory)
    {
      this.factory = factory;
    }

    public GeometryFactory getFactory() { return factory; }

    public Geometry edit(Geometry geometry, GeometryEditorOperation operation) {
        if (geometry instanceof GeometryCollection) {
            return editGeometryCollection((GeometryCollection) geometry, operation);
        }

        if (geometry instanceof Polygon) {
            return editPolygon((Polygon) geometry, operation);
        }

        if (geometry instanceof Point) {
            return operation.edit(geometry, this);
        }

        if (geometry instanceof LineString) {
            return operation.edit(geometry, this);
        }

        Assert.shouldNeverReachHere(
            "Unsupported Geometry classes should be caught in the GeometryEditorOperation.");

        return null;
    }

    private Polygon editPolygon(Polygon polygon, GeometryEditorOperation operation) {
        Polygon newPolygon = (Polygon) operation.edit(polygon, this);
        if (newPolygon.isEmpty()) {
            //RemoveSelectedPlugIn relies on this behaviour. [Jon Aquino]
            return newPolygon;
        }
        LinearRing shell = (LinearRing) edit(newPolygon.getExteriorRing(), operation);
        if (shell.isEmpty()) {
            //RemoveSelectedPlugIn relies on this behaviour. [Jon Aquino]
            return factory.createPolygon(null, null);
        }
        ArrayList holes = new ArrayList();

        for (int i = 0; i < newPolygon.getNumInteriorRing(); i++) {
            LinearRing hole = (LinearRing) edit(newPolygon.getInteriorRingN(i), operation);

            if (hole.isEmpty()) {
                continue;
            }

            holes.add(hole);
        }

        return factory.createPolygon(shell, (LinearRing[]) holes.toArray(new LinearRing[] {}));
    }

    private GeometryCollection editGeometryCollection(
        GeometryCollection collection,
        GeometryEditorOperation operation) {
        GeometryCollection newCollection = (GeometryCollection) operation.edit(collection, this);
        ArrayList geometries = new ArrayList();

        for (int i = 0; i < newCollection.getNumGeometries(); i++) {
            Geometry geometry = edit(newCollection.getGeometryN(i), operation);

            if (geometry.isEmpty()) {
                continue;
            }

            geometries.add(geometry);
        }

        if (newCollection.getClass() == MultiPoint.class) {
            return factory.createMultiPoint((Point[]) geometries.toArray(new Point[] {}));
        }
        if (newCollection.getClass() == MultiLineString.class) {
            return factory.createMultiLineString((LineString[]) geometries.toArray(new LineString[] {}));
        }
        if (newCollection.getClass() == MultiPolygon.class) {
            return factory.createMultiPolygon((Polygon[]) geometries.toArray(new Polygon[] {}));
        }
        return factory.createGeometryCollection((Geometry[]) geometries.toArray(new Geometry[] {}));
    }

    public interface GeometryEditorOperation {
        /**
         * "Modifies" a Geometry by returning a new Geometry with a modification.
         * The returned Geometry might be the same as the Geometry passed in.
         */
        public Geometry edit(Geometry geometry, GeometryEditor editor);
    }

    /**
     * Modifies the coordinates of a Geometry which contains a single coordinate list.
     */
    public abstract static class CoordinateOperation implements GeometryEditorOperation {
        public Geometry edit(Geometry geometry, GeometryEditor editor) {
            if (geometry instanceof LinearRing) {
                return editor.factory.createLinearRing(edit(geometry.getCoordinates(), geometry, editor));
            }

            if (geometry instanceof LineString) {
                return editor.factory.createLineString(edit(geometry.getCoordinates(), geometry, editor));
            }

            if (geometry instanceof Point) {
                Coordinate[] newCoordinates = edit(geometry.getCoordinates(), geometry, editor);
                return editor.factory.createPoint((newCoordinates.length > 0) ? newCoordinates[0] : null);
            }

            return geometry;
        }

        /**
         * Edits the coordinate list from a geometry.
         *
         * @param coordinates the coordinate list to operate on
         * @param geometry the geometry containing the coordinate list
         * @return an edited coordinate list (which may be the same as the input)
         */
        public abstract Coordinate[] edit(Coordinate[] coordinates, Geometry geometry, GeometryEditor editor);
    }
}

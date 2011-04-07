package com.vividsolutions.jcs.geom;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.util.AssertionFailedException;
import com.vividsolutions.jts.precision.EnhancedPrecisionOp;

/**
 * Improves the robustness of buffer computation by using small
 * perturbations of the buffer distance.  Also used enhanced precision.
 */
public class BufferDistancePerturber {

  public static Geometry safeBuffer(Geometry geom, double distance)
  {
    Geometry buffer = null;
    try {
      buffer = EnhancedPrecisionOp.buffer(geom, distance);
    }
    catch (AssertionFailedException ex) {
      // eat the exception
    }
    return buffer;
  }

  private double distance;
  private double maximumPerturbation;

  public BufferDistancePerturber(double distance, double maximumPerturbation)
  {
    this.distance = distance;
    this.maximumPerturbation = maximumPerturbation;
  }

  /**
   * Attempts to compute a buffer using small perturbations of the buffer distance
   * if necessary.  If this routine is unable to perform the buffer computation correctly
   * the orginal buffer exception will be propagated.
   *
   * @param geom the Geometry to compute the buffer for
   * @return the buffer of the input Geometry
   */
  public Geometry buffer(Geometry geom)
  {
    Geometry buffer = safeBuffer(geom, distance);
    if (isBufferComputedCorrectly(geom, buffer))
      return buffer;
    else {
System.out.println("buffer robustness error found");
System.out.println(geom);
    }
    buffer = safeBuffer(geom, distance + maximumPerturbation);
    if (isBufferComputedCorrectly(geom, buffer)) return buffer;

    return geom.buffer(distance - maximumPerturbation);
  }

  /**
   * Check various assertions about the geometry and the buffer to
   * try to determine whether the JTS buffer function failed to compute
   * the buffer correctly.  These are heuristics only - this may not catch all errors
   *
   * @param geom the geometry
   * @param buffer the buffer computed by JTS
   * @return <code>true</code> if the buffer seems to be correct
   */
  private boolean isBufferComputedCorrectly(Geometry geom, Geometry buffer)
  {
    if (buffer == null) return false;
    // sometimes buffer() computes empty geometrys
    if (! geom.isEmpty() && buffer.isEmpty()) return false;
    // sometimes buffer() computes a very small geometry as the buffer
    if (! buffer.getEnvelopeInternal().contains(geom.getEnvelopeInternal())) return false;
    return true;
  }
}

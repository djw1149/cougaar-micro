/*
 * <copyright>
 *  Copyright 1999-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.asset;

import java.util.*;
import java.io.IOException;
import org.cougaar.microedition.util.*;
import org.cougaar.microedition.ldm.*;

/**
 * create a Thermometer Resource.
 */
public abstract class ThermometerResource extends SensorResource {

  public ThermometerResource() {
    setName("Temperature");
  }

}
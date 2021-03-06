/*
 *  This file is part of the Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */
package org.mmtk.plan.tutorial;

import org.mmtk.plan.TraceLocal;
import org.mmtk.plan.Trace;
import org.mmtk.policy.Space;

import org.vmmagic.pragma.*;
import org.vmmagic.unboxed.*;

/**
 * This class implements the thread-local core functionality for a transitive
 * closure over the heap graph.
 */
@Uninterruptible
public final class TutorialTraceLocal extends TraceLocal {

  /**
   * Constructor
   */
  public TutorialTraceLocal(Trace trace) {
      super(Tutorial.SCAN_MARK, trace);
  }


  /****************************************************************************
   * Externally visible Object processing and tracing
   */

  /**
   * Is the specified object live?
   *
   * @param object The object.
   * @return <code>true</code> if the object is live.
   */
  @Override
  public boolean isLive(ObjectReference object) {
    if (object.isNull()) return false;
    if (Space.isInSpace(Tutorial.MARK_SWEEP, object)) {
	return Tutorial.nurserySpace.isLive(object);
	//return Tutorial.msSpace.isLive(object);
    }
    return super.isLive(object);
  }

  /**
   * This method is the core method during the trace of the object graph.
   * The role of this method is to:
   *
   * 1. Ensure the traced object is not collected.
   * 2. If this is the first visit to the object enqueue it to be scanned.
   * 3. Return the forwarded reference to the object.
   *
   * @param object The object to be traced.
   * @return The new reference to the same object instance.
   */
  @Inline
  @Override
  public ObjectReference traceObject(ObjectReference object) {
    if (object.isNull()) return object;
    if (Space.isInSpace(Tutorial.NURSERY, object))
	return Tutorial.nurserySpace.traceObject(this, object, Tutorial.ALLOC_DEFAULT);
    if (Space.isInSpace(Tutorial.MARK_SWEEP, object))
      return Tutorial.msSpace.traceObject(this, object);
    return super.traceObject(object);
  }

    /*
    @Override
	public ObjectReference precopyObject(ObjectReference object) {
	if (object.isNull()) return object;
	else if (Space.isInSpace(Tutorial.NURSERY, object))
	    return Tutorial.nurserySpace.traceObject(this, object, Tutorial.ALLOC_DEFAULT);
	else
	    return object;
    }
    */
    @Override
	public boolean willNotMoveInCurrentCollection(ObjectReference object) {
	return !Space.isInSpace(Tutorial.NURSERY, object);
    }
}

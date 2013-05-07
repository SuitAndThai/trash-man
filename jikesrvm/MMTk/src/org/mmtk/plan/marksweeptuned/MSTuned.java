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
package org.mmtk.plan.marksweeptuned;

import org.mmtk.plan.*;
import org.mmtk.policy.MarkSweepTunedSpace;
import org.mmtk.policy.Space;
import org.mmtk.utility.Log;
import org.mmtk.utility.heap.VMRequest;

import org.vmmagic.pragma.*;
import org.vmmagic.unboxed.*;

/**
 * This class implements the global state of a simple mark-sweep collector.
 * 
 * All plans make a clear distinction between <i>global</i> and
 * <i>thread-local</i> activities, and divides global and local state into
 * separate class hierarchies. Global activities must be synchronized, whereas
 * no synchronization is required for thread-local activities. There is a single
 * instance of Plan (or the appropriate sub-class), and a 1:1 mapping of
 * PlanLocal to "kernel threads" (aka CPUs). Thus instance methods of PlanLocal
 * allow fast, unsychronized access to functions such as allocation and
 * collection.
 * 
 * The global instance defines and manages static resources (such as memory and
 * virtual memory resources). This mapping of threads to instances is crucial to
 * understanding the correctness and performance properties of MMTk plans.
 */
@Uninterruptible
public class MSTuned extends StopTheWorld {

	/****************************************************************************
	 * Class variables
	 */
	public static final MarkSweepTunedSpace msSpace = new MarkSweepTunedSpace("ms",
			VMRequest.create());
	public static final int MARK_SWEEP = msSpace.getDescriptor();

	public static final int SCAN_MARK = 0;

	/****************************************************************************
	 * Instance variables
	 */
	public final Trace msTrace = new Trace(metaDataSpace);

	/*****************************************************************************
	 * Collection
	 */

	/**
	 * Perform a (global) collection phase.
	 * 
	 * @param phaseId
	 *            Collection phase to execute.
	 */
	@Inline
	@Override
	public void collectionPhase(short phaseId) {
		Log.writeln("MSTuned: In the collectionPhase method");

		// prepare for a global collection phase
		if (phaseId == PREPARE) {
			Log.writeln("MSTuned: In the PREPARE phase");

			super.collectionPhase(phaseId);
			msTrace.prepare();
			msSpace.prepare(true);
			return;
		}

		// prepare for local collection (parallel threads)
		if (phaseId == CLOSURE) {
			Log.writeln("MSTuned: In the CLOSURE phase");

			msTrace.prepare();
			return;
		}
		
		// post local collection 
		if (phaseId == RELEASE) {
			Log.writeln("MSTuned: In the RELEASE phase");

			msTrace.release(); // release resources after the pass
			msSpace.release(); // we're releasing for the sweep phase
			super.collectionPhase(phaseId);
			return;
		}

		super.collectionPhase(phaseId);
	}

	/*****************************************************************************
	 * Accounting
	 */

	/**
	 * Return the number of pages reserved for use given the pending allocation.
	 * The superclass accounts for its spaces, we just augment this with the
	 * mark-sweep space's contribution.
	 * 
	 * @return The number of pages reserved given the pending allocation,
	 *         excluding space reserved for copying.
	 */
	@Override
	public int getPagesUsed() {
		Log.writeln("MSTuned: In the getPagesUsed method");

		return (msSpace.reservedPages() + super.getPagesUsed());
	}

	/*****************************************************************************
	 * Miscellaneous
	 */

	/**
	 * @see org.mmtk.plan.Plan#willNeverMove
	 * 
	 * @param object
	 *            Object in question
	 * @return True if the object will never move
	 */
	@Override
	public boolean willNeverMove(ObjectReference object) {
		Log.writeln("MSTuned: In the willNeverMove method");

		if (Space.isInSpace(MARK_SWEEP, object)) {
			Log.writeln("MSTuned: The object is in the space.");
			
			return true;
		}
		return super.willNeverMove(object);
	}

	/**
	 * Register specialized methods.
	 */
	@Interruptible
	@Override
	protected void registerSpecializedMethods() {
		Log.writeln("MSTuned: In the registerSpecializedMethods method");

		TransitiveClosure.registerSpecializedScan(SCAN_MARK,
				MSTunedTraceLocal.class);
		super.registerSpecializedMethods();
	}
}

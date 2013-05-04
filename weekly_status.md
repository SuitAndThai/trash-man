Status Report
=============

Week 3 (5/7)
------------
In progress.

Week 2 (4/30)
-------------
We've been reaching out to the community in order to better understand how garbage collectors are implemented in JikesRVM.

A specific question that we raised concerns how bitmap marking works in the garbage collector. We've noticed within the properties files a specific attribute that seems to control bitmap marking (config.mmtk.headerMarkBit=true) in the BaseBaseMarkSweepTuned.properties file.

The response we received was that this flag switches between using mark bits in the header of an object and mark bits in a 'side' dense bitmap (he default is to use the header). We were also referred to a white paper that better explains the mark bit mechanism (section 4 in particular): http://cs.anu.edu.au/~Robin.Garner/pf-ismm-2007.pdf.

We were also referred to a reference that explained memory allocation in jikes (http://jikesrvm.org/Memory+Allocation+in+JikesRVM), though this was not helpful.

We rephrased the question to the mailing list so that we could map our understanding of an existing algorithm (mark sweep) to the implementation in jikes. One response to this question mentioned that garbage collectors in Jikes RVM work in phases. There are phases for mutator and collector. This meant that there were no explicit calls to invoke mark and sweep phases.

For mark sweep collector, marking is done by tracing the heap. Tracing starts from the roots (see STACK_ROOTS and ROOTS phases in SimpleCollector.java) and finishes with a transitive closure (see CLOSURE phase in MSCollector.java). Sweeping is done by walking through the heap and sweep objects that are not marked (see msSpace.release method of RELEASE phase in MS.java).

We'll be looking at processPhaseStack method in Phase.java in order to better understand how the phases are executed.

We are also trying to explore the code with the gdb debugger. Getting this working will allow us to watch the execution of of code and see the various calls. We are currently having problems setting breakpoints correctly. To set a breakpoint the associated part of memory must be loaded. Furthermore, one must examine the memory map to find the address in memory. We contacted the mailing list about this and recieved some valuable feedback. They indicated some places where breakpoints can be set that allow access to more parts of the code. We now can set breakpoints in some parts of the code but other sections are currently inaccessable. It should just be a matter of finding memory accessable addresses to break at and using those breakpoints to access areas we are interested in.



Week 1 (4/23)
-------------

We have explored the Mark Sweep code and developed a somewhat rudimentary understanding of it's features and associations. We have created our own version of the mark sweep collector (using these instructions: http://jikesrvm.org/Adding+a+New+GC) and enabled logging (located here: results/buildit/localhost-[date]/tests/local/BaseBaseMyGC/gctest/ReferenceTest.default-output.txt

example: results/buildit/localhost-2013-04-21-Sun-17-40-16/tests/local/BaseBaseMarkSweepTuned/gctest/ReferenceTest.default-output.txt )

to figure out program flow. This will allow us to determine how our new code is being used and whether our tuning adjustments are being used.

Concerning the instructions to make a new GC, there is one issue: in step 1, the directory is MMTk/src/org/mmtk/plan/marksweep (src level was excluded if you're doing it by hand).

Viewing the log file, the following occurred:

1. MSTunedMutator.alloc and MSTunedMutator.postAlloc methods were called repeatedly

2. MSTuned.getPagesUsed method was called once

3. MSTunedCollector.global called

4. Steps 1 and 2 occurred repeatedly with the amount of alloc/postAlloc method calls varying.

5. MSTuned.willNeverMove method is called, and the object is in the space. This occurs twice in a row.

6. Steps 1 and 2 occurred repeatedly with the amount of alloc/postAlloc method calls varying.

7. Steps 4 and 5 repeat a couple of times.

8. MarkSweepspace.TraceObject is called.

9. Steps 1 through 8 repeat.

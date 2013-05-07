trash-man
=========
Garbage Collector Readme

Installing Jikes RVM

The first step to running the garbage collector is installing the required dependencies. If you run into any issues during this install process, or later on during the use of it, refer to the troubleshooting section at the end of this guide. To install the dependencies, run the following command (this guide was written for Ubuntu 12.10, as of March 2013):

    $ sudo apt-get install mercurial ant gcc g++ gcc-multilib g++-multilib openjdk-6-jdk bison -y

After the dependencies are installed, make sure that you are using the correct version of Java. To do so, run the following command:

    $ sudo update-alternatives --config java

This will bring up a menu with options for versions of java installed on your machine. JikesRVM requires Java 6.

Once all of the dependencies have been satisfied, you can download the virtual machine itself. Follow the guide on Jikes’ website: http://jikesrvm.org/Get+The+Source. Next, go to the folder where you downloaded the virtual machine and create a file named .ant.properties, and add a line in it referencing the type of system you are building on; for example, in our system, the line was:
host.name=ia32-linux
You can find the other options for host name and many more things to specify from this guide: http://jikesrvm.org/Building+the+RVM

Finally, you should build and test the rvm. To build the rvm, run the following command from Jikes’ root directory:

    $ ./bin/buildit localhost -t gctest BaseBaseMarkSweep

This should build and test a simple mark sweep collector. To build other collectors, simply change the “BaseBaseMarkSweep” to a different plan (to be explained later). To run a different test, just change “gctest”. To build without testing, remove the -t flag and the test name.

If the build and tests run successfully, you should have a newly built virtual machine, contained in a directory under the dist directory. Simply use the rvm binary in that folder in the same way you would use Java to run programs (i.e., ./dist/{plan_name}_{os_name}/rvm foo, assuming foo is a compiled Java program in Jikes’ root directory).


Exploring Jikes RVM

Each garbage collector in Jikes is separated into several components. Most of the important classes are located in the org.mmtk package. A high-level overview shows that the organization is divided into the following sections (examples follow in brackets from the example Mark-Sweep collector):

Each garbage collector has both global and thread-local versions of its collector. The global collector is usually named after the type of collector [MS.java], and the local collector usually adds TraceLocal after the name [MSTraceLocal.java]. These are both located in a package specific to that collector [org.mmtk.plan.marksweep].

That package also contains three other classes. Those implement the mutator methods, e.g. allocation, that the plan requires [MSMutator.java], the required things for the collector [MSCollector.java], and any constraints that the collector has that the VM needs to know about [MSConstraints.java].

Each plan also defines its own space. Spaces are an interface between the heap itself and the rest of the virtual machine. They are used to segment the heap into certain areas, and do low-level tasks like determining whether an object is in the space, giving ObjectReferences based on Addresses, and the like. These are located in org.mmtk.policy.

Another interesting class to look at is Phase.java. This class defines all of the phases that the garbage collector goes through when a call is given to collect, and sets up the order in which all of the phases are called.

To get an idea of how the roots are determined for a simple collector, look at the ROOTS and STACK_ROOTS phases inside the SimpleCollector. For transitive closures over the heap, look at the CLOSURE phase in MSCollector.

Troubleshooting the RVM

The following troubleshooting steps are resolutions to problems we encountered during our use of JikesRVM. They are intended as possible solutions to some problems that may arise during installation and use of Jikes, and while we do suggest looking through here first in case we ran into your problem and have done the needed research to fix it, be prepared to google and ask the mailing list (https://lists.sourceforge.net/lists/listinfo/jikesrvm-researchers) for help for problems you run into.

Running a Program Issues

If you get an error how java cannot be located and that it's located somewhere in "usr/bin/java/bin/java", then go to your .bashrc or .bash_profile file and add the line "export JAVA_HOME=/usr/" at the end and delete any other export command.

If something says that a java thing, "java blah", wasn't found, and you're pretty sure that you have java, then there's some config file somewhere pointing to the wrong location. If you want to know the location of your java thing, here's a good way to find where it may be located:

    $ whereis java

though you may need something more specific depending on what is missing. Once you figure out where the correct location is, run this command from your current directory to figure out where this incorrect directory for "java blah" is hardcoded:

    $ find . -exec grep -l "java blah" {} \;

You'll get mostly directories, but you should find a file or a couple that defined this (most likely in an obscure properties file). Go to it, and change that line to the proper java location.

When running a program you may get an error something similar to: Exception in thread "main" java.lang.UnsupportedClassVersionError: Main : Unsupported major.minor version 51.0. Key thing to notice is the error: java.lang.UnsupportedClassVersionError. What this means is that there's a higher JDK during compile time and lower JDK during runtime. update-alternative affected the runtime JDK, but javac is still working with a newer version of java. To remedy this, you must specify source and target versions of java when compiling like so (if Java 6 is being used):

    $ javac -source 1.6 -target 1.6 foo.java

Tutorial Issues

Most of these issues are in the main part of the tutorial, Building the Mark-Sweep Collector.

Free-List Allocation

In the first step: in TutorialConstraints the imports "import org.mmtk.policy.SegregatedFreeListSpace;" and "import org.mmtk.policy.MarkSweepSpace;" must be added. If there are still other import errors, copy over more imports from the marksweep collector (plan/marksweep)

In the second step, DEFAULT_POLL_FREQUENCY is never defined and should be omitted from the constructor.

Mark-Sweep Collection

In the second step, there is no need to add the getPagesRequired method.

Hybrid Collector

There is no need to add the precopyObject method.

Miscellaneous Issues

Eclipse has a bug when trying to use a remote connection to use the rvm. To run Eclipse over a remote connection, run the following command (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=386955 for details):

    $ eclipse -vmargs -Dorg.eclipse.swt.internal.gtk.cairoGraphics=false &


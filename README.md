trash-man
=========

garbage collecting fun

First, JikesRVM must be installed. To install it, follow the directions on their website: http://jikesrvm.org/Get+The+Source. After it is installed, make sure that it is working by making sure that all required dependencies are installed from this page: http://jikesrvm.org/Building+the+RVM, and creating a .ant.properties file with the correct host environment (for example, ia32-linux). Afterwards, run bin/buildit localhost BaseBaseNoGC to build a sample virtual machine that has no garbage collector. Run the built machine (located under the dist folder) against a sample program. Any problems that may occur may be solved by the following notes:


I don't believe my exact series of steps will be particularly helpful. You ought to consider following the Quick Start Guide here: http://jikesrvm.org/Quick+Start+Guide


And when you hit an issue, pinpoint the error and google for a solution. The following symptoms and erros that I've experienced were typical of a machine without the proper version of Java, here they are and my approaches to solving them.
***
Packages needed:
$ sudo apt-get install mercurial ant gcc g++ gcc-multilib g++-multilib openjdk-6-jdk -y
***
To ensure that you're using the right version of java:
$ sudo update-alternatives --config java
This will give you a series of options for a version of java. At the time of writing (11 Mar 2013), Jikes RVM requires version 6.
***
If you get an error how java cannot be located and that it's located somewhere in  "use/bin/java/bin/java", then go to your .bashrc or .bash_profile file and add the line
"export JAVA_HOME=/usr/" at the end and delete any other export command.
***
If something says that a java thing, "java blah", wasn't found, and you're pretty sure that you have java, then there's some config file somewhere pointing to the wrong location. If you want to know the location of your java thing, here's a good way to find where it may be located:
$ whereis java
though you may need something more specific depending on what is missing. Once you figure out where the correct location is, run this command from your current directory to figure out where this incorrect directory for "java blah" is hardcoded:
$ find . -exec grep -l "java blah" {} \;
You'll get mostly directories, but you should find a file or a couple that defined this (most likely in an obscure properties file). Go to it, and change that line to the proper java location.






you must be able to run + compile a java program. since you'll likely need to switch java versions (update alternative), then your compile + run for a test program should go along the lines of
$ javac foo.java
$ java foo
of which you may receive something similar to
Exception in thread "main" java.lang.UnsupportedClassVersionError: Main : Unsupported major.minor version 51.0


Key thing to notice is the error: java.lang.UnsupportedClassVersionError
What this means is that there's a higher JDK during compile time and lower JDK during runtime. update-alternative affected the runtime JDK, but javac is still working with a newer version of java. To remedy this, you must specify source and target versions of java when compiling like so
$ javac -source 1.6 -target 1.6 foo.java
assuming java version 1.6 is being used.


I addition to the packages listed in Richards notes I also needed Bison
sudo apt-get install bison


I wasn't sure about the run configurations for host and config but the following worked:
ant -Dhost.name=x86_64_m64-linux -Dconfig.name=production_Opt_0


I haven't been able to build the configuration the tutorial requires. I've tried other configurations and changing system settings but so far nothing has worked.
I can build other configurations (the production congirations) but I have been able to run them. When I test them with HelloWorld they give me seg faults or frame address errors.


I had intial trouble building the rvm on the garby linux machine. I discovered this was due to the name change of the directory. Using buildit fixed theses issues.
Previously we had difficulty getting eclipse to find the project we were supposed to import. I think this was because we didn't get the code from Mercurial. When I did this I was able to get the project. However I still can't open files in eclipse. Also I can't run it from eclipse (run as gives me no options).
Building the Mark-sweep collector
There are some errors in the Tutorial for this step. In TutorialConstraints the imports "import org.mmtk.policy.SegregatedFreeListSpace;" and "import org.mmtk.policy.MarkSweepSpace;" must be added. There are other import error in the tutorial. If you don;t know what to import copy the imports of the marksweep collector (plan/marksweep)
Also DEFAULT_POLL_FREQUENCY is never defined and should be omitted from the constructor.
Step 4 of mark sweep colection has MS where it should have Tutorial in the code
Step 2 has you add:
@Override
public int getPagesRequired() {
  return super.getPagesRequired() + msSpace.requiredPages();
}
This is wrong. Ignore it. The functionality is covered by getPagesUsed().
See http://old.nabble.com/-rvm-research--Building-a-Mark-sweep-Collector-Tutorial-tt34795967.html#a34795967
In the Hybrid Collector section theysay to add the following th TraceLocal:
@Override
    public ObjectReference precopyObject(ObjectReference object) {
    if (object.isNull()) return object;
    else if (Space.isInSpace(Tutorial.NURSERY, object))
        return Tutorial.nurserySpace.traceObject(this, object, Tutorial.ALLOC_DEFAULT);
    else
        return object;
}
However, the superclass method does not exist. After checking with the super class and other collectors (the generational collector example) I couldn't find anywhere to put this code, so I left it out.


The resulting collector built, tested and ran successfully.


I installed on Ubuntu 12.10


First I ran sudo apt-get install mercurial ant gcc g++ gcc-multilib g++-multilib openjdk-6-jdk -y to get all the necessary additional packages and used mercurial to download the repository located at http://hg.code.sourceforge.net/p/jikesrvm/code
I then switched directories into the "jikesrvm" directory, and created a .ant.properties file, which I added host.name=x86_64-linux and config.name=development to, and then ran ant to build the VM. Afterwords, I added the executable (located in jikesrvm/dist/development_x86_64-linux) to my path, so I could run rvm anywhere.


I managed to get Eclipse working; the problem is a bug in Cairo (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=386955 for details); to run eclipse you must run eclipse -vmargs -Dorg.eclipse.swt.internal.gtk.cairoGraphics=false


I still have not found a way to use run configuration in Eclipse, but that does not seem necessary to running it (just use the manual commands).


When making the tutorial package, copy/pasting the NoGC package in Eclipse will prompt with a rename, to allow for easy creation.


When changing the ImmortalSpace to the MarkSweepSpace in Tutorial.java, there is no need to pass in the DEFAULT_POLL_FREQUENCY anymore; the constructor only takes two parameters.


When the tutorial asks to add a new accounting method (getPagesRequired), that has since been deprecated (I think), and the getPagesUsed method already implemented should work.

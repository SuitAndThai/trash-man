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
package org.jikesrvm;

import org.vmmagic.unboxed.WordArray;

import org.jikesrvm.classloader.RVMMethod;
import org.jikesrvm.compilers.opt.lir2mir.BURS;
import org.jikesrvm.compilers.opt.depgraph.DepGraphNode;
import org.jikesrvm.compilers.opt.ir.IR;

public class ArchitectureSpecificOpt {
  public static final class AssemblerOpt extends org.jikesrvm.compilers.opt.mir2mc.ia32.AssemblerOpt {
  
    public AssemblerOpt(int bcSize, boolean print, IR ir) {
      super(bcSize, print, ir);
    }
  //*/
  }
  public static final class BURS_Debug extends org.jikesrvm.compilers.opt.lir2mir.ia32.BURS_Debug {}
  public static final class BURS_STATE extends org.jikesrvm.compilers.opt.lir2mir.ia32.BURS_STATE {
    public BURS_STATE(BURS b) {
      super(b);
    }}
  public static class BURS_TreeNode extends org.jikesrvm.compilers.opt.lir2mir.ia32.BURS_TreeNode {
    public BURS_TreeNode(DepGraphNode node) {
      super(node);
    }
    public BURS_TreeNode(char int_constant_opcode) {
      super(int_constant_opcode);
    }}
  public static final class CallingConvention extends org.jikesrvm.compilers.opt.regalloc.ia32.CallingConvention {}
  public static final class ComplexLIR2MIRExpansion extends org.jikesrvm.compilers.opt.lir2mir.ia32.ComplexLIR2MIRExpansion {}
  public static final class ConvertALUOperators extends org.jikesrvm.compilers.opt.lir2mir.ia32.ConvertALUOperators {}
  public static final class FinalMIRExpansion extends org.jikesrvm.compilers.opt.mir2mc.ia32.FinalMIRExpansion {}
  public static final class MIROptimizationPlanner extends org.jikesrvm.compilers.opt.driver.ia32.MIROptimizationPlanner {}
  public static final class NormalizeConstants extends org.jikesrvm.compilers.opt.lir2mir.ia32.NormalizeConstants {}
  public interface PhysicalRegisterConstants extends org.jikesrvm.compilers.opt.regalloc.ia32.PhysicalRegisterConstants {}
  public abstract static class PhysicalRegisterTools extends org.jikesrvm.compilers.opt.ir.ia32.PhysicalRegisterTools {}
  public static final class RegisterPreferences extends org.jikesrvm.compilers.opt.regalloc.ia32.RegisterPreferences {}
  public static final class RegisterRestrictions extends org.jikesrvm.compilers.opt.regalloc.ia32.RegisterRestrictions {
    public RegisterRestrictions(PhysicalRegisterSet phys) {
      super(phys);
    }}
  public static final class StackManager extends org.jikesrvm.compilers.opt.regalloc.ia32.StackManager {}
  public static final class OptExceptionDeliverer extends org.jikesrvm.compilers.opt.runtimesupport.ia32.OptExceptionDeliverer {}
  public static final class OptGCMapIterator extends org.jikesrvm.compilers.opt.runtimesupport.ia32.OptGCMapIterator {
    public OptGCMapIterator(WordArray registerLocations) {
      super(registerLocations);
    }}
  public interface OptGCMapIteratorConstants extends org.jikesrvm.compilers.opt.runtimesupport.ia32.OptGCMapIteratorConstants {}
  public static final class GenerateMachineSpecificMagic extends org.jikesrvm.compilers.opt.bc2ir.ia32.GenerateMachineSpecificMagic {}
  public static final class PhysicalDefUse extends org.jikesrvm.compilers.opt.ir.ia32.PhysicalDefUse {}
  public static final class PhysicalRegisterSet extends org.jikesrvm.compilers.opt.ir.ia32.PhysicalRegisterSet {}
  public static final class RegisterPool extends org.jikesrvm.compilers.opt.ir.ia32.RegisterPool {
    public RegisterPool(RVMMethod meth) {
      super(meth);
    }}
  public static final class BaselineExecutionStateExtractor extends org.jikesrvm.osr.ia32.BaselineExecutionStateExtractor {}
  public static final class CodeInstaller extends org.jikesrvm.osr.ia32.CodeInstaller {}
  public static final class OptExecutionStateExtractor extends org.jikesrvm.osr.ia32.OptExecutionStateExtractor {}
  public static final class PostThreadSwitch extends org.jikesrvm.osr.ia32.PostThreadSwitch {}
}

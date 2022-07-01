package mop;
import java.util.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import java.lang.ref.*;
import org.aspectj.lang.*;

public aspect ServiceLoaderIterator_RemoveMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ServiceLoaderIterator_RemoveMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ServiceLoaderIterator_Remove_MOPLock = new ReentrantLock();
	static Condition ServiceLoaderIterator_Remove_MOPLock_cond = ServiceLoaderIterator_Remove_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() ;
	pointcut ServiceLoaderIterator_Remove_remove(Iterator i) : (call(* Iterator+.remove(..)) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : ServiceLoaderIterator_Remove_remove(i) {
	}

	pointcut ServiceLoaderIterator_Remove_create(ServiceLoader s) : (call(Iterator ServiceLoader.iterator()) && target(s)) && MOP_CommonPointCut();
	after (ServiceLoader s) returning (Iterator i) : ServiceLoaderIterator_Remove_create(s) {
	}

}

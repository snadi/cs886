package ca.uwaterloo.bhp.cfg;

import java.util.Iterator;
import java.util.List;

import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.AnyNewExpr;
import soot.jimple.AssignStmt;
import soot.jimple.EqExpr;
import soot.jimple.FieldRef;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Ref;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.toolkits.graph.Block;

public class FeatureExtractor {
	
	public static void extractFeatures(ExecutionPath path) {
		for(Block block : path.blocks()) {
			Iterator<Unit> iterator = block.iterator();
			
			while(iterator.hasNext()) {
				Unit unit = iterator.next();
				
				// Equal expressions
				path.features().get(FeatureName.EQUAL).incrementCount(countEqual(unit));
				
				// New expressions
				path.features().get(FeatureName.NEW).incrementCount(countNew(unit));
				
				// Assignment statements
				path.features().get(FeatureName.ASSIGNMENTS).incrementCount(countAssignment(unit));
				
				// Dereferences
				path.features().get(FeatureName.DEREFERNCES).incrementCount(countDereference(unit));
				
				// Fields
				path.features().get(FeatureName.FIELDS).incrementCount(countField(unit));
				
				// Field writes
				path.features().get(FeatureName.FIELDS_WRITTEN).incrementCount(countWriteToField(unit));
				
				// Invoke expressions
				path.features().get(FeatureName.INVOCATIONS).incrementCount(countInvoke(unit));
				
				// Goto statements
				path.features().get(FeatureName.GOTO).incrementCount(countGoto(unit));
				
				// If statements
				path.features().get(FeatureName.IF).incrementCount(countIf(unit));
				
				// If statements
				path.features().get(FeatureName.LOCAL_VARIABLES).incrementCount(countLocal(unit));
				
				// Statements
				path.features().get(FeatureName.STATEMENTS).incrementCount();
				
				// Throw statements
				path.features().get(FeatureName.THROWS).incrementCount(countThrow(unit));
			}
		}
	}
	
	private static int countEqual(Unit unit) {
		return (unit instanceof IfStmt && ((IfStmt)unit).getCondition() instanceof EqExpr ? 1 : 0);
	}
	
	private static int countNew(Unit unit) {
		return (unit instanceof AssignStmt && ((AssignStmt)unit).getRightOp() instanceof AnyNewExpr ? 1 : 0);
	}
	
	private static int countAssignment(Unit unit) {
		return (unit instanceof AssignStmt ? 1 : 0);
	}
	
	private static int countDereference(Unit unit) {
		int counter = 0;
		for(ValueBox box : getUseDefBoxes(unit)) {
			if(box.getValue() instanceof Ref) {
				counter++;
			}
		}
		return counter;
	}
	
	private static int countField(Unit unit) {
		int counter = 0;
		for(ValueBox box : getUseDefBoxes(unit)) {
			if(box.getValue() instanceof FieldRef) {
				counter++;
			}
		}
		return counter;
	}
	
	private static int countWriteToField(Unit unit) {
		return (unit instanceof AssignStmt && ((AssignStmt)unit).getLeftOp() instanceof FieldRef ? 1 : 0);
	}
	
	private static int countInvoke(Unit unit) {
		if(unit instanceof InvokeStmt) {
			return 1;
		} else {
			return (unit instanceof AssignStmt && ((AssignStmt)unit).getRightOp() instanceof InvokeExpr ? 1 : 0);
		}
	}
	
	private static int countGoto(Unit unit) {
		if(unit instanceof GotoStmt || unit instanceof IfStmt) {
			return 1;
		} else if(unit instanceof LookupSwitchStmt) {
			return ((LookupSwitchStmt)unit).getTargetCount();
		} else if(unit instanceof TableSwitchStmt) {
			return ((TableSwitchStmt)unit).getTargets().size();
		}
		
		return 0;
	}
	
	private static int countIf(Unit unit) {
		return (unit instanceof IfStmt ? 1 : 0);
	}
	
	private static int countLocal(Unit unit) {
		int counter = 0;
		for(ValueBox box : getUseDefBoxes(unit)) {
			if(box.getValue() instanceof Local) {
				counter++;
			}
		}
		return counter;
	}
	
	private static int countThrow(Unit unit) {
		return (unit instanceof ThrowStmt ? 1 : 0);
	}
	
	@SuppressWarnings("unchecked")
	private static List<ValueBox> getUseDefBoxes(Unit unit) {
		return unit.getUseAndDefBoxes();
	}
}

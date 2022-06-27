package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;

public class CounterVisitor extends VisitorAdaptor {
	protected int count;
	
	public int getCount() {
		return count;
	}
	
	public static class FormParamCounter extends CounterVisitor{
		public void visit(FormParsNoBrackets f) {
			count++;
		}
		public void visit(FormParsListNoBrackets f) {
			count++;
		}
		
		
	}
	
	
	public static class VarCounter extends CounterVisitor{
		public void visit(VarDeclListNormal f) {
			count++;
		}
		
		public void visit(VarDeclMore f) {
			count++;
		}
	}
	
}

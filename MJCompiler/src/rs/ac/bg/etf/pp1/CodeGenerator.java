package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.locks.Condition;

import rs.ac.bg.etf.pp1.CounterVisitor.FormParamCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.CondFactOne;
import rs.ac.bg.etf.pp1.ast.CondTerm;
import rs.ac.bg.etf.pp1.ast.CondTermList;
import rs.ac.bg.etf.pp1.ast.CondTermListMore;
import rs.ac.bg.etf.pp1.ast.ConditionListMore;
import rs.ac.bg.etf.pp1.ast.ContFactTwo;
import rs.ac.bg.etf.pp1.ast.Designator;
import rs.ac.bg.etf.pp1.ast.DesignatorIDENT;
import rs.ac.bg.etf.pp1.ast.DesignatorList;
import rs.ac.bg.etf.pp1.ast.DesignatorStatementAssign;
import rs.ac.bg.etf.pp1.ast.DesignatorStatementDMINUS;
import rs.ac.bg.etf.pp1.ast.DesignatorStatementDPLUS;
import rs.ac.bg.etf.pp1.ast.DesignatorStatementParsNo;
import rs.ac.bg.etf.pp1.ast.DesignatorStatementParsYes;
import rs.ac.bg.etf.pp1.ast.Do;
import rs.ac.bg.etf.pp1.ast.DotDesiList;
import rs.ac.bg.etf.pp1.ast.ElseBegin;
import rs.ac.bg.etf.pp1.ast.ElseNo;
import rs.ac.bg.etf.pp1.ast.ElseNoNo;
import rs.ac.bg.etf.pp1.ast.ElseYes;
import rs.ac.bg.etf.pp1.ast.Expr;
import rs.ac.bg.etf.pp1.ast.ExprDesiList;
import rs.ac.bg.etf.pp1.ast.ExprListMore;
import rs.ac.bg.etf.pp1.ast.ExprMinus;
import rs.ac.bg.etf.pp1.ast.ExprMinusTerm;
import rs.ac.bg.etf.pp1.ast.ExprNoMinus;
import rs.ac.bg.etf.pp1.ast.Factor;
import rs.ac.bg.etf.pp1.ast.FactorBool;
import rs.ac.bg.etf.pp1.ast.FactorChar;
import rs.ac.bg.etf.pp1.ast.FactorDesignator;
import rs.ac.bg.etf.pp1.ast.FactorDesignatorNoPars;
import rs.ac.bg.etf.pp1.ast.FactorDesignatorPars;
import rs.ac.bg.etf.pp1.ast.FactorNewType;
import rs.ac.bg.etf.pp1.ast.FactorNewTypeExpr;
import rs.ac.bg.etf.pp1.ast.FactorNum;
import rs.ac.bg.etf.pp1.ast.IfBegin;
import rs.ac.bg.etf.pp1.ast.IfEnd;
import rs.ac.bg.etf.pp1.ast.LAndBegin;
import rs.ac.bg.etf.pp1.ast.LOrBegin;
import rs.ac.bg.etf.pp1.ast.MethodDecl;
import rs.ac.bg.etf.pp1.ast.MethodDeclaration;
import rs.ac.bg.etf.pp1.ast.MethodTypeName;
import rs.ac.bg.etf.pp1.ast.MulDiv;
import rs.ac.bg.etf.pp1.ast.MulStar;
import rs.ac.bg.etf.pp1.ast.OpPlus;
import rs.ac.bg.etf.pp1.ast.Relop;
import rs.ac.bg.etf.pp1.ast.RelopDEqual;
import rs.ac.bg.etf.pp1.ast.RelopGE;
import rs.ac.bg.etf.pp1.ast.RelopGR;
import rs.ac.bg.etf.pp1.ast.RelopLE;
import rs.ac.bg.etf.pp1.ast.RelopLS;
import rs.ac.bg.etf.pp1.ast.RelopNEqual;
import rs.ac.bg.etf.pp1.ast.SingleStatementDOState;
import rs.ac.bg.etf.pp1.ast.SingleStatementDesignatorBREAK;
import rs.ac.bg.etf.pp1.ast.SingleStatementDesignatorCONTINUE;
import rs.ac.bg.etf.pp1.ast.SingleStatementDesignatorPRINT;
import rs.ac.bg.etf.pp1.ast.SingleStatementDesignatorPRINTConst;
import rs.ac.bg.etf.pp1.ast.SingleStatementDesignatorREAD;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.ast.Term;
import rs.ac.bg.etf.pp1.ast.TermListMore;
import rs.ac.bg.etf.pp1.ast.Type;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {
		
	private int mainPc;
	
	Stack<Integer> Do_begin=new Stack<Integer>();
	Stack<Integer> and_stack=new Stack<Integer>();
	Stack<Integer> or_stack=new Stack<Integer>();
	Stack<Integer> cond_term_stack=new Stack<Integer>();
	Stack<Integer> if_stack = new Stack<Integer>();
	Stack<Integer> break_stack = new Stack<Integer>();
	ArrayList<MethodTypeName> method_type_list =new ArrayList<MethodTypeName>();
	
	
	public int getMainPc() {
		return mainPc;
	}
	ArrayList<Obj> list_recorda;
	
	
	public static final Struct BoolType = new Struct(Struct.Bool);

	public Struct get_expr_type(Expr expr) {
		Term t=null;
		if(expr instanceof ExprNoMinus) 
			t=((ExprNoMinus)expr).getTerm();
		else
			t=((ExprMinus)expr).getTerm();
			Factor f= t.getFactor();
			
			if(f instanceof FactorNum) 
				return Tab.intType;
			else if(f instanceof FactorChar)
				return Tab.charType;
			else if(f instanceof FactorBool)
				return BoolType;
			else if(f instanceof FactorDesignatorPars)
				return  ((FactorDesignatorPars)f).getDesignator().obj.getType();
			else if(f instanceof FactorDesignatorNoPars)
				return  ((FactorDesignatorNoPars)f).getDesignator().obj.getType();
			
			else if(f instanceof FactorDesignator) {
				
				DesignatorIDENT desi_ide= (DesignatorIDENT)(((FactorDesignator)f).getDesignator());
				DesignatorList desi_list = desi_ide.getDesignatorList();
				if(desi_list instanceof ExprDesiList) { //niz je
					return ((FactorDesignator)f).getDesignator().obj.getType().getElemType();
				}else if(desi_list instanceof DotDesiList) { //rekord je
					
					String field_name = ((DotDesiList)desi_list).getDesigName();
					Collection<Obj> col=desi_ide.obj.getType().getMembers();
					ArrayList<Obj> list=new ArrayList<Obj>(col);
					for(int i =0 ; i< list.size() ;i++)
						if(list.get(i).getName().equals(field_name)) { 
							if(list.get(i).getType().getKind()==Struct.Array) //niz u rekordu
								return list.get(i).getType().getElemType();
								else
									return list.get(i).getType();
						}
					
					
				}
				
				
				return ((FactorDesignator)f).getDesignator().obj.getType();				
			}
		
		return null;
		
	}
	
	
	
	boolean isArrayInit=false;
	boolean isRecordInit=false;
	//READ
	
	public void visit(SingleStatementDesignatorREAD single_read_stm) {
		
		
		Designator desi=single_read_stm.getDesignator();
		
		if(desi.obj.getType().getKind()==Struct.Array) {
			Code.put(Code.read);
			if(desi.obj.getType().getElemType().getKind()==Struct.Int)
			Code.put(Code.astore);
			else
			Code.put(Code.bastore);	
		}
		else if(desi.obj.getType().getKind()==Struct.Class) { //rekord
			
			
			if(((DesignatorIDENT)desi).getDesignatorList() instanceof DotDesiList) {
			
			DesignatorIDENT desi_ident = ((DesignatorIDENT)desi);
			DesignatorList list=desi_ident.getDesignatorList();
			
			Collection <Obj> coll=desi.obj.getType().getMembers();
			ArrayList<Obj> lista=new ArrayList<Obj>(coll);
			Obj field= null;
			int i;
			for( i=0;i<lista.size();i++) {
				
				if(((DotDesiList)list).getDesigName().equals(lista.get(i).getName())) {
					
					field=lista.get(i);
					break;
				}
				
			}
			if(field.getType().getKind()==Struct.Array) { // polje je niz
				
				
				Code.put(Code.getfield);
				Code.put2(i);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				Code.put(Code.read);
				
				if(field.getType().getElemType()==Tab.intType)
				Code.put(Code.astore);
				else
				Code.put(Code.bastore);
	
				
			}else {
				Code.put(Code.read);

				Code.put(Code.putfield);
				Code.put2(i);
			}
			
			
			}
			
			
		}else { //prom
		Code.put(Code.read);

		Code.store(desi.obj);
		}
	}
	
	//PRINT
	public void visit(SingleStatementDesignatorPRINT print_stm) {
		
		
		

		if( get_expr_type(print_stm.getExpr()) == Tab.intType) { 
			Code.loadConst(3);
			
			Code.put(Code.print);
		}else if(get_expr_type(print_stm.getExpr()) == Tab.charType) {
			Code.loadConst(3);
			Code.put(Code.bprint);

		}else if(get_expr_type(print_stm.getExpr()) == BoolType) {
			Code.loadConst(3);
			Code.put(Code.print);
		}
		
		
	}
	public void visit(SingleStatementDesignatorPRINTConst print_stm_const) {
		
		int printwidth=print_stm_const.getN2();
		
		Code.loadConst(printwidth);
		if( get_expr_type(print_stm_const.getExpr()) == Tab.intType) { 			
			Code.put(Code.print);
		}else if(get_expr_type(print_stm_const.getExpr()) == Tab.charType) {
			Code.put(Code.bprint);

		}
		
	}
	
	public void visit(FactorNum fac_num) {
		Obj num_node=Tab.insert(Obj.Con,"$",new Struct(Struct.Int));
		
		num_node.setLevel(0); //globalno
		num_node.setAdr(fac_num.getN1());
		Code.load(num_node);
	}
	
	public void visit(FactorChar fac_char) {
		Obj num_node=Tab.insert(Obj.Con,"$",new Struct(Struct.Char));
		
		num_node.setLevel(0); //globalno
		String s=fac_char.getC1();
		s=s.substring(1,s.length()-1);
		num_node.setAdr(s.charAt(0));
		Code.load(num_node);
	}
	
	public void visit(FactorBool fac_bool) {
		Obj num_node=Tab.insert(Obj.Con,"$",new Struct(Struct.Bool));
		
		num_node.setLevel(0); //globalno
		
		String val=fac_bool.getB1();
		if(val.equals("true"))
		num_node.setAdr(1);
		else
		num_node.setAdr(0);
			
		Code.load(num_node);
	}
	boolean minus=false;
	public void visit(ExprMinusTerm expr_minus) {
		minus=true;
	}
	public void visit(Term term) {
		if(minus) {
		Code.put(Code.neg);
		minus=false;
		}
	}
	
	public void visit(MethodTypeName methodTypeName){
		
		if("main".equalsIgnoreCase(methodTypeName.getFuncName())){
			mainPc = Code.pc;
		}
		methodTypeName.obj.setAdr(Code.pc);
		
		method_type_list.add(methodTypeName);
		
		// Collect arguments and local variables
		SyntaxNode methodNode = methodTypeName.getParent();
	
		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);
		
		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);
		
		// Generate the entry
		Code.put(Code.enter);
		Code.put(fpCnt.getCount());
		Code.put(fpCnt.getCount() + varCnt.getCount());
	
	}
	//S
	/*
	Code.loadConst(0);
			Code.load(fac_sum.getDesignator().obj); 
			
			Code.put(Code.arraylength);
			
			Code.loadConst(0);
			stack_sum.add(Code.pc);
			Code.put(Code.dup2);
			Code.putFalseJump(Code.gt,0);
			stack_sum.add(Code.pc-2);
			Code.put(Code.dup);
			Code.load(fac_sum.getDesignator().obj); 
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.put(Code.aload);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.put(Code.pop);
			
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			
			Code.put(Code.add);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.load(fac_sum.getDesignator().obj);
			Code.put(Code.arraylength);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			
			Code.putJump(stack_sum.get(0));
			Code.fixup(stack_sum.get(1));
			
			Code.put(Code.pop);
			Code.put(Code.pop);
	*/
	
	//M
	/*
		ArrayList<Integer> stack_max=new ArrayList<Integer>();

		public void visit(FactorMAX fac_max) {
			//lazni max
			Code.load(fac_max.getDesignator().obj); 
			Code.loadConst(0);
			Code.put(Code.aload);
			//lazni max
			
			Code.load(fac_max.getDesignator().obj); 
			Code.put(Code.arraylength);
			Code.loadConst(1);
			
			stack_max.add(Code.pc);	// get(0)-->sledeca iteracija	
			Code.put(Code.dup2);
			Code.putFalseJump(Code.gt,0); // N <= brojaca skok van svega
			stack_max.add(Code.pc-2); //get(1)

			Code.put(Code.dup);
			Code.load(fac_max.getDesignator().obj);
			
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.put(Code.aload);
			
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.put(Code.pop);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.put(Code.dup2);
			//brojac max val max val do ovde
			
			Code.putFalseJump(Code.lt,0); // ako max >= val skok
			stack_max.add(Code.pc-2);
			
			Code.put(Code.dup_x1); // ako je max < val zamena! brojac val max
			Code.put(Code.pop);
			
			Code.fixup(stack_max.get(2));
			//ovde se skace
			Code.put(Code.pop); // brojac novi_max
			
			Code.put(Code.dup_x1);
			Code.put(Code.pop); //novi_max brojac
			
			Code.loadConst(1);
			Code.put(Code.add); // novi_max brojac+1
			
			Code.load(fac_max.getDesignator().obj); //
			Code.put(Code.arraylength); //novi_max brojac+1 N
			
			Code.put(Code.dup_x1); //novi_max N brojac+1
			Code.put(Code.pop);
			Code.putJump(stack_max.get(0));
			Code.fixup(stack_max.get(1));
			
			Code.put(Code.pop);
			Code.put(Code.pop);

		}
		
	*/
	
	
	
	
	
	
	
	
	
	public void visit(MethodDeclaration methodDecl){
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	//sabiranje i oduzimanje
	public void visit(ExprListMore expr_list_more) {
		
		if(expr_list_more.getAddop() instanceof OpPlus)
			Code.put(Code.add);
		else //onda je oduzimanje
			Code.put(Code.sub);
	}
	//mnozenje i deljenje
	public void visit(TermListMore term_list_more) {
		if(term_list_more.getMulop() instanceof MulStar)
			Code.put(Code.mul);
		else if(term_list_more.getMulop() instanceof MulDiv)
			Code.put(Code.div);
		else // %
			Code.put(Code.rem);

		
	}
	//ADD ++
	public void visit(DesignatorStatementDPLUS desi_state_dplus) {
		
		
//		if(desi_state_dplus.getDesignator().obj.getLevel()!=0) { //lokalna
//		Code.put(Code.inc);
//		
//		Code.put(0);
//		Code.put(1);
//		}else { //lokalna
			if(desi_state_dplus.getDesignator().obj.getType().getKind()==Struct.Array) {
				
				Code.put(Code.dup2);
				Code.put(Code.aload);
				
				Code.put(Code.const_1);
				Code.put(Code.add);
				
				Code.put(Code.astore);
				
			}else if(desi_state_dplus.getDesignator().obj.getType().getKind()==Struct.Class){ //za rekorde
				
				DesignatorIDENT desi_ident = ((DesignatorIDENT)desi_state_dplus.getDesignator());
				DesignatorList list=desi_ident.getDesignatorList();
				
				Collection <Obj> coll=desi_state_dplus.getDesignator().obj.getType().getMembers();
				ArrayList<Obj> lista=new ArrayList<Obj>(coll);
				Obj field= null;
				int i;
				for( i=0;i<lista.size();i++) {
					
					if(((DotDesiList)list).getDesigName().equals(lista.get(i).getName())) {
						
						field=lista.get(i);
						break;
					}
					
				}
				if(field.getType().getKind()!=Struct.Array) { //nije niz
					Code.put(Code.dup);
					Code.put(Code.getfield);
					Code.put2(i);
					Code.put(Code.const_1);
					Code.put(Code.add);
					Code.put(Code.putfield);
					Code.put2(i);
					
				}else { //niz u rekordu
					Code.put(Code.getfield);
					Code.put2(i);
					Code.put(Code.dup_x1);
					Code.put(Code.pop);
					Code.put(Code.dup2);
					Code.put(Code.aload);
					Code.put(Code.const_1);
					Code.put(Code.add);
					Code.put(Code.astore);
					
				}
				
				
				
				
			} 
			else{
			Code.load(desi_state_dplus.getDesignator().obj);
			Code.put(Code.const_1);
			Code.put(Code.add);
			Code.store(desi_state_dplus.getDesignator().obj);
			}
		//}


	}
	//SUB --
	public void visit(DesignatorStatementDMINUS desi_state_dminus) {
//		if(desi_state_dminus.getDesignator().obj.getLevel()!=0) { //lokalna
//			Code.put(Code.inc);
//			
//			Code.put(0);
//			Code.put(255);
//			}else { //lokalna
		if(desi_state_dminus.getDesignator().obj.getType().getKind()==Struct.Array) {
			
			Code.put(Code.dup2); 
			Code.put(Code.aload);
			
			Code.put(Code.const_1);
			Code.put(Code.sub);
			
			Code.put(Code.astore);
			
		}else if(desi_state_dminus.getDesignator().obj.getType().getKind()==Struct.Class){
			
			DesignatorIDENT desi_ident = ((DesignatorIDENT)desi_state_dminus.getDesignator());
			DesignatorList list=desi_ident.getDesignatorList();
			
			Collection <Obj> coll=desi_state_dminus.getDesignator().obj.getType().getMembers();
			ArrayList<Obj> lista=new ArrayList<Obj>(coll);
			Obj field= null;
			int i;
			for( i=0;i<lista.size();i++) {
				
				if(((DotDesiList)list).getDesigName().equals(lista.get(i).getName())) {
					
					field=lista.get(i);
					break;
				}
				
			}
			if(field.getType().getKind()!=Struct.Array) { //nije niz
				Code.put(Code.dup);
				Code.put(Code.getfield);
				Code.put2(i);
				Code.put(Code.const_1);
				Code.put(Code.sub);
				Code.put(Code.putfield);
				Code.put2(i);
				
			}else { //niz u rekordu
				Code.put(Code.getfield);
				Code.put2(i);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				Code.put(Code.dup2);
				Code.put(Code.aload);
				Code.put(Code.const_1);
				Code.put(Code.sub);
				Code.put(Code.astore);
				
			}
			
			
			
			
		}
		else {
			Code.load(desi_state_dminus.getDesignator().obj);
			Code.put(Code.const_1);
			Code.put(Code.sub);
			Code.store(desi_state_dminus.getDesignator().obj);
		}
				
		
//				Code.load(desi_state_dminus.getDesignator().obj);
//				Code.put(Code.const_1);
//				Code.put(Code.sub);
//				Code.store(desi_state_dminus.getDesignator().obj);
			//}
	}
	
	//DESIGNATOR
	public void visit(DesignatorIDENT desig) {
			
		System.out.println("KIND"+desig.obj.getType().getKind());
		
		DesignatorList desi_list=desig.getDesignatorList();
		//astore adr,index,val
		if(desi_list instanceof ExprDesiList) {
			Code.load(desig.obj); //EXPR ADR
			Code.put(Code.dup_x1); //ADR EXPR ADR
			Code.put(Code.pop);
			
		}else if(desi_list instanceof DotDesiList) {
//			if(desig.obj.getKind()==Obj.Con)
//			System.out.println("CON!");
//			else if(desig.obj.getKind()==Obj.Var)
//				System.out.println("VAR!");
			
			Code.load(desig.obj);
			
			
		}
			
		
	}

	
	
	//ASSIGN
	public void visit(DesignatorStatementAssign assign_op) {
		
		DesignatorIDENT desi = (DesignatorIDENT)assign_op.getDesignator();
		
		Collection<Obj> col=desi.obj.getType().getMembers();
		ArrayList<Obj> list=new ArrayList<Obj>(col);
		
		list_recorda=list;
		
		DesignatorList desi_list=desi.getDesignatorList();
		
		if(desi_list instanceof DotDesiList ) {
			
//			if(isArrayInit)
//			Code.store(assign_op.getDesignator().obj);
			
			String field_name=((DotDesiList)desi_list).getDesigName();
			int num=0;
			boolean niz=false;
			
			int array_type=Struct.Int;
			
			for(int i=0;i<list_recorda.size();i++) {
				if(list_recorda.get(i).getName().equals(field_name)) { 
					
					if(list_recorda.get(i).getType().getKind()==Struct.Array) {
						
						if(!isArrayInit) {
						
							Code.put(Code.dup_x2);
							Code.put(Code.pop);
							
						Code.put(Code.getfield);
						Code.put2(num);
						niz=true;
						
						if(list_recorda.get(i).getType().getElemType()==Tab.charType)
							array_type=Struct.Char;
						
						}
						
						
					}
					
					
					break;
				}
				num++;
			}
				
			if(!niz) {
				
			Code.put(Code.putfield);
			Code.put2(num);
			}else {
				//Code.put(Code.getfield);
			//	Code.put2(num+1);
				
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				
				if(array_type== Struct.Int)
				Code.put(Code.astore);
				else
				Code.put(Code.bastore);	
			}
			
			
		}else { 
		
		if(isArrayInit  || desi.obj.getType().getKind()!=Struct.Array)
		Code.store(assign_op.getDesignator().obj);
//		else if(desi.obj.getType().getKind()==Struct.Class) { //za Rekorde
//		
//			
//			
//		}
		else {
			
			if(desi.obj.getType().getKind()==Struct.Array && desi.obj.getType().getElemType().getKind() == Struct.Int )
			Code.put(Code.astore);
			else if(desi.obj.getType().getKind()==Struct.Array && desi.obj.getType().getElemType().getKind() == Struct.Char)
			Code.put(Code.bastore);

		}
		
		}
		isArrayInit=false;
		isRecordInit=false;
		
		
	}
	
	//FACTOR DESI
	public void visit(FactorDesignator fact_desig) {
		DesignatorIDENT desi= (DesignatorIDENT)fact_desig.getDesignator();
		
		Collection<Obj> col=desi.obj.getType().getMembers();
		ArrayList<Obj> list=new ArrayList<Obj>(col);
		
		list_recorda=list;
		
		DesignatorList desi_list=desi.getDesignatorList();
		if(desi_list instanceof ExprDesiList) {
			if(fact_desig.getDesignator().obj.getType().getElemType().getKind()==Struct.Int)
			Code.put(Code.aload);
			else if(fact_desig.getDesignator().obj.getType().getElemType().getKind()==Struct.Char)
				Code.put(Code.baload);

		}
		else if (desi_list instanceof DotDesiList) {
			

			String field_name=((DotDesiList)desi_list).getDesigName();
			int num=0;
			boolean niz=false;
			
			
			for(int i=0;i<list_recorda.size();i++) {
				if(list_recorda.get(i).getName().equals(field_name)) { 
					
					if(list_recorda.get(i).getType().getKind()==Struct.Array) {
						
					//	if(!isArrayInit) {
						
							
							
						Code.put(Code.getfield);
						Code.put2(num);
						Code.put(Code.dup_x1);
						Code.put(Code.pop);
						if(list_recorda.get(i).getType().getElemType()==Tab.intType)
						Code.put(Code.aload);
						else
						Code.put(Code.baload);	
						
						niz=true;
						//}
						
						
					}
					
					
					break;
				}
				num++;
			}
				
					
			//int adr=assign_op.getDesignator().obj.getAdr();
			if(!niz) {
			Code.put(Code.getfield);
			Code.put2(num);
			}
			
			
		}else
		Code.load(fact_desig.getDesignator().obj);
	}
	
	//NEW ARRAY
	public void visit(FactorNewTypeExpr factor_new_type) {
		
		isArrayInit=true;
		if(factor_new_type.getType().struct == Tab.charType) {
			Code.put(Code.newarray);
			Code.put(0);
			
		}else if(factor_new_type.getType().struct == Tab.intType) {
			Code.put(Code.newarray);
			Code.put(1);
			
		}
		
		
	}
	//NEW RECORD
	public void visit(FactorNewType factor_new) {
		isRecordInit=true;
		Collection<Obj> col=factor_new.getType().struct.getMembers();
		ArrayList<Obj> list=new ArrayList<Obj>(col);
		list_recorda=list;
		int num_fields=list.size();
		int bajtovi=num_fields*4;
		Code.put(Code.new_);
		Code.put2(bajtovi);
	//	Code.put(Code.dup);
//		Code.loadConst(0);
//		Code.put(Code.putfield);
//	    Code.put2(0);
		
	}

	//FUNCKIJE
	
	public void visit(FactorDesignatorPars fact_pars) {
		MethodTypeName method=null;
		
		if(fact_pars.getDesignator().obj.getName().equals("ord") || fact_pars.getDesignator().obj.getName().equals("chr")) {
			
		}else if(fact_pars.getDesignator().obj.getName().equals("len") ) {
			
			Code.put(Code.arraylength);
			
		}else {
			for(int i=0;i<method_type_list.size();i++) {
				if(method_type_list.get(i).obj.getName().equals(fact_pars.getDesignator().obj.getName())) {
					method=method_type_list.get(i);
					break;
				}
			}
			
			Code.put(Code.call);
			//Obj meth = Tab.find(fact_pars.getDesignator().obj.getName());
			
			Code.put2( -(Code.pc-1 - method.obj.getAdr()));
			
		}
		
		
		
	}
	public void visit(FactorDesignatorNoPars fact_no_pars) {
		MethodTypeName method=null;
		
		for(int i=0;i<method_type_list.size();i++) {
			if(method_type_list.get(i).obj.getName().equals(fact_no_pars.getDesignator().obj.getName())) {
				method=method_type_list.get(i);
				break;
			}
		}
		
		Code.put(Code.call);
		//Obj meth = Tab.find(fact_pars.getDesignator().obj.getName());
		
		Code.put2( -(Code.pc-1 - method.obj.getAdr()));;
	}
	
	public void visit(DesignatorStatementParsYes pars_yes) {
		
		MethodTypeName method=null;
		
		if(pars_yes.getDesignator().obj.getName().equals("ord") || pars_yes.getDesignator().obj.getName().equals("chr")) {
			
		}else if(pars_yes.getDesignator().obj.getName().equals("len") ) {
			
			Code.put(Code.arraylength);
			
		}else {
			for(int i=0;i<method_type_list.size();i++) {
				if(method_type_list.get(i).obj.getName().equals(pars_yes.getDesignator().obj.getName())) {
					method=method_type_list.get(i);
					break;
				}
			}
			
			Code.put(Code.call);
			//Obj meth = Tab.find(fact_pars.getDesignator().obj.getName());
			
			Code.put2( -(Code.pc-1 - method.obj.getAdr()));
			
		}
		
		
	}
	public void visit(DesignatorStatementParsNo pars_no) {
		
		MethodTypeName method=null;
		
		for(int i=0;i<method_type_list.size();i++) {
			if(method_type_list.get(i).obj.getName().equals(pars_no.getDesignator().obj.getName())) {
				method=method_type_list.get(i);
				break;
			}
		}
		
		Code.put(Code.call);
		//Obj meth = Tab.find(fact_pars.getDesignator().obj.getName());
		
		Code.put2( -(Code.pc-1 - method.obj.getAdr()));;
		
	}
		
	//DO-WHILE
	
	boolean land=false;
	
	boolean lor=false;
	
	public void visit(LOrBegin lor_begin) {

		while(!and_stack.empty())
			Code.fixup(and_stack.pop());
	}
	
	public void visit(Do do_begin) {
		Do_begin.push(Code.pc);
	}
	
	boolean globalna=false;
	
	public void visit(ContFactTwo fc) {
		Relop relop=fc.getRelop();
		
			if(relop instanceof RelopDEqual) {//==
				Code.putFalseJump(Code.eq,0);
	
			}else if(relop instanceof RelopNEqual) { //!=
				Code.putFalseJump(Code.ne,0);

			}else if(relop instanceof RelopGR) { // >
				Code.putFalseJump(Code.gt,0);
	
			}else if(relop instanceof RelopGE) { // >=
				Code.putFalseJump(Code.ge,0);
	
			}else if(relop instanceof RelopLS) { // <
				Code.putFalseJump(Code.lt,0);
	
			}else if(relop instanceof RelopLE) { // <=
				Code.putFalseJump(Code.le,0);
			}
			
			//System.out.println("PC: "+ Code.pc);
			
			and_stack.push(Code.pc-2);
		
		if(globalna) {
			if(fc.getParent().getClass()== CondTermListMore.class) { //znaci da je &&
				if(relop instanceof RelopDEqual) {//==
				Code.putFalseJump(Code.eq,0);

			}else if(relop instanceof RelopNEqual) { //!=
				Code.putFalseJump(Code.ne,0);

			}else if(relop instanceof RelopGR) { // >
				Code.putFalseJump(Code.gt,0);

			}else if(relop instanceof RelopGE) { // >=
				Code.putFalseJump(Code.ge,0);

			}else if(relop instanceof RelopLS) { // <
				Code.putFalseJump(Code.lt,0);

			}else if(relop instanceof RelopLE) { // <=
				Code.putFalseJump(Code.le,0);
			}
			and_stack.push(Code.pc - 2);
			
			
			
			}else if(fc.getParent().getParent().getClass()==ConditionListMore.class) {
				
				
				if(relop instanceof RelopDEqual) {//==
					Code.putFalseJump(Code.inverse[Code.eq],0);

				}else if(relop instanceof RelopNEqual) { //!=
					Code.putFalseJump(Code.inverse[Code.ne],0);

				}else if(relop instanceof RelopGR) { // >
					Code.putFalseJump(Code.inverse[Code.gt],0);

				}else if(relop instanceof RelopGE) { // >=
					Code.putFalseJump(Code.inverse[Code.eq],0);

				}else if(relop instanceof RelopLS) { // <
					Code.putFalseJump(Code.inverse[Code.lt],0);

				}else if(relop instanceof RelopLE) { // <=
					Code.putFalseJump(Code.inverse[Code.le],0);
				}
				or_stack.push(Code.pc - 2);	
				while(!and_stack.empty())
					Code.fixup(and_stack.pop());
				
			}else { // Cond?
				if(relop instanceof RelopDEqual) {//==
					Code.putFalseJump(Code.eq,0);

				}else if(relop instanceof RelopNEqual) { //!=
					Code.putFalseJump(Code.ne,0);

				}else if(relop instanceof RelopGR) { // >
					Code.putFalseJump(Code.gt,0);

				}else if(relop instanceof RelopGE) { // >=
					Code.putFalseJump(Code.ge,0);

				}else if(relop instanceof RelopLS) { // <
					Code.putFalseJump(Code.lt,0);

				}else if(relop instanceof RelopLE) { // <=
					Code.putFalseJump(Code.le,0);
				}
				cond_term_stack.push(Code.pc - 2);

			
				
				

				
			}
			
		}
//		

		
		
		

	}
	
	public void visit(CondFactOne fc_one) {
		int adr=Code.pc +2;
		//System.out.println("PC: " + adr);
		and_stack.push(adr);
		Code.loadConst(1);
		Code.putFalseJump(Code.eq,0);
		
		
	}
	public void visit(CondTerm cond_term) {
		
		CondTermList term_list=cond_term.getCondTermList();
		
		
		if(term_list instanceof CondTermListMore) { // &&
			
			
			
			
			
			
		}else { // bez toga
			
		}
		
		
			
		if(!Do_begin.empty() && !if_begin_bool) {
		int adr=Do_begin.pop();
		Do_begin.push(adr);
		Code.putJump(adr);
		}
		
		
	}
	public void visit(LAndBegin land_begin) {
		land=true;
	}
	
	
	public void visit(SingleStatementDOState stm_do_while) {
//		int cond_term_adr=cond_term_stack.pop();
//		Code.putJump(Do_begin.pop());
//		Code.fixup(cond_term_adr);
		
		while(!and_stack.empty())
			Code.fixup(and_stack.pop());	
		
		if(or_stack.empty()) {
			//?
		}else if(cond_term_stack.empty()) {
			
			//??
		}
		
		
	
		
		while(!break_stack.empty())
			Code.fixup(break_stack.pop());
//		
//		while(!or_stack.empty())
//			Code.fixup(or_stack.pop());
		
	}
	
	boolean if_begin_bool=false;
	
	boolean globalna2=false;
	
	public void visit(Condition con) {
		//Condition
	}
	
	//IF ELSE
	public void visit(IfBegin if_begin) {
		if_begin_bool=true;
	}
	public void visit(IfEnd if_end) {
		int adr=Code.pc+ 1;
		if_stack.push(adr);
		Code.putJump(0); 
	}
	public void visit(ElseBegin else_begin) {
		
		while(!and_stack.empty())
			Code.fixup(and_stack.pop());
		
		
	}
	public void visit(ElseNoNo else_no_no) {
		
		while(!and_stack.empty())
			Code.fixup(and_stack.pop());
		
		
	}
	
	
	public void visit(ElseYes else_yes) {
		if_begin_bool=false;
		
		while(!if_stack.empty())
			Code.fixup(if_stack.pop());
		
	}
	public void visit(ElseNo else_no) {
		if_begin_bool=false;
		
		while(!if_stack.empty())
			Code.fixup(if_stack.pop());
	}
	
	
	//CONTINUE
	public void visit(SingleStatementDesignatorCONTINUE continue_stm) {
		int adr=Do_begin.pop();
		Do_begin.push(adr);
		Code.putJump(adr);
		
	}
	//BREAK
	public void visit(SingleStatementDesignatorBREAK break_stm) {
		break_stack.push(Code.pc+1);
		Code.putJump(0);
	}
	
}

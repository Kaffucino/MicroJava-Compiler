package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;

import org.apache.log4j.Logger;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import java.util.*;





public class SemanticAnalyzer extends VisitorAdaptor {

	Logger log = Logger.getLogger(getClass());
	boolean errorDetected = false;
	int printCallCount = 0;
	Type currentType = null;
	Obj currentMethod = null;
	boolean returnFound = false;
	
	Struct current_desig_type = null;
	String current_assign_type = null;
	boolean assign_op = false;
	boolean goto_op = false;

	int nVars;

	ArrayList<ConstDeclMore> any_const_list = new ArrayList<ConstDeclMore>();
	ArrayList<VarDeclMore> var_list = new ArrayList<VarDeclMore>();
	ArrayList<VarDeclMoreArray> var_list_array = new ArrayList<VarDeclMoreArray>();

	ArrayList<String> label_goto_list = new ArrayList<String>();
	ArrayList<String> label_def_list = new ArrayList<String>();
	ArrayList<Integer> label_error_line = new ArrayList<Integer>();
	
	
	ArrayList<String> list_decl_arrays = new ArrayList<String>();
	
	int user_arg_num=0;
	ArrayList<String> user_arg_types=new ArrayList<String>();
	ArrayList<String> current_arg_types=new ArrayList<String>();
	
	ArrayList<Obj> members_list=new ArrayList<Obj>();
	
	
	
	
	public static class func_struct{ //za evidenciju stvari u funkciji
		
		public String func_name;
		public int argument_nums;
		public ArrayList<String> argument_types;
		
		public func_struct(String f_name,int ar_nums, ArrayList<String> ar_types) {
			func_name=f_name;
			argument_nums=ar_nums;
			argument_types=ar_types;
		}
		
		
		
	}
	
	
	
	ArrayList<func_struct> func_list=new ArrayList<func_struct>();
	
	public static final Struct BoolType = new Struct(Struct.Bool);
	
	public static final Struct intArrayType = new Struct(Struct.Array,Tab.intType);
	public static final Struct charArrayType = new Struct(Struct.Array,Tab.charType);
	public static final Struct boolArrayType = new Struct(Struct.Array,BoolType);
	
	public boolean check_arg_nums(String f_name,int ar_nums) {
		
		boolean correct=true;
		
		for(int i=0;i<func_list.size();i++) 
			if(func_list.get(i).func_name.equals(f_name) && func_list.get(i).argument_nums != ar_nums) {
				correct=false;
				break;
			}
			
		
		
		return correct;
	}
	
	public int get_func_arg(String f_name) {
		
		int arg=0;
		for(int i=0;i<func_list.size();i++) 
			if(func_list.get(i).func_name.equals(f_name)) {
				arg=func_list.get(i).argument_nums;
				break;
			}
				
		
		return arg;
	}
	
	public boolean check_arg_types(String f_name, ArrayList<String> types) {
		boolean correct=true;
		for(int i=0;i<func_list.size();i++) 
		{	
			if(func_list.get(i).func_name.equals(f_name)) {
				//System.out.println(f_name);
				for(int j=0;j<func_list.get(i).argument_types.size(); j++) {
					//System.out.println("Formalni: "+ func_list.get(i).argument_types.get(j) + " Stvarni: "+ types.get(j));
					if(!func_list.get(i).argument_types.get(j).equals(types.get(j))) {
					correct=false;
					break;
				}
				
				}
					
				
			}
			
			if(!correct)
				break;
		}
		
		

		
		return correct;

	}
	
	
	public SemanticAnalyzer() {
		Tab.insert(Obj.Type, "bool", BoolType);
		Tab.insert(Obj.Type, "intArrayType", intArrayType);
		Tab.insert(Obj.Type, "charArrayType", charArrayType);
		Tab.insert(Obj.Type, "boolArrayType", boolArrayType);

	}
	
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
	

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}

	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars(); // za IV fazu
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}

	public void visit(ProgName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
		Tab.openScope();
	}
	
	public boolean is_record(String name) {
		
		return record_list.contains(name);
	}
	
	
	public void visit(VarDeclListNormal varDeclList) {
		Obj promenljiva = Tab.find(varDeclList.getVarName());
		if (promenljiva != Tab.noObj)
			report_error("Promenljiva " + varDeclList.getVarName() + " je vec deklarisana!", null);
		else {
			report_info("Deklarisana promenljiva " + varDeclList.getVarName(), varDeclList);
			Obj varNode=null; 
			if(!record_define) {
				
					varNode = Tab.insert(Obj.Var, varDeclList.getVarName(), varDeclList.getType().struct);

			}
			else
				varNode = Tab.insert(Obj.Fld, varDeclList.getVarName(),varDeclList.getType().struct);
			
			
			
			
			// current_var_decl_type = varDeclList.getType().struct;
			for (int i = 0; i < var_list.size(); i++) {
				if(!record_define)
				Tab.insert(Obj.Var, var_list.get(i).getVarName(), varDeclList.getType().struct);
				else
				Tab.insert(Obj.Fld, var_list.get(i).getVarName(), varDeclList.getType().struct);	
			}
			var_list.clear();
		}

	}

	public void visit(VarDeclListArray varDeclList) {

		Obj promenljiva = Tab.find(varDeclList.getVarName());
		if (promenljiva != Tab.noObj)
			report_error("Promenljiva " + varDeclList.getVarName() + " je vec deklarisana!", null);
		else {
			report_info("Deklarisana promenljiva " + varDeclList.getVarName(), varDeclList);
			Obj varNode=null;
			if(!record_define)
			varNode = Tab.insert(Obj.Var, varDeclList.getVarName(),new Struct(Struct.Array,varDeclList.getType().struct)); 
			else
			varNode = Tab.insert(Obj.Fld, varDeclList.getVarName(), new Struct(Struct.Array,varDeclList.getType().struct)); 
				
			
			
			list_decl_arrays.add(varDeclList.getVarName());
			
			// current_var_decl_type = varDeclList.getType().struct;
			for (int i = 0; i < var_list_array.size(); i++) {
				if(!record_define)
				Tab.insert(Obj.Var, var_list_array.get(i).getVarName(), new Struct(Struct.Array,varDeclList.getType().struct)); //varDeclList.getType().struct
				else
				Tab.insert(Obj.Fld, var_list_array.get(i).getVarName(), new Struct(Struct.Array,varDeclList.getType().struct));
					
			}
			var_list_array.clear();
		}

	}

	public void visit(VarDeclMore varDecl) {

		Obj promenljiva = Tab.find(varDecl.getVarName());
		if (promenljiva != Tab.noObj)
			report_error("Promenljiva " + varDecl.getVarName() + " je vec deklarisana!", null);
		else {
			report_info("Deklarisana promenljiva " + varDecl.getVarName(), varDecl);
			// Obj varNode = Tab.insert(Obj.Var, varDecl.getVarName(),
			// current_var_decl_type);
			var_list.add(varDecl);
		}
	}

	public void visit(VarDeclMoreArray varDecl) {
		Obj promenljiva = Tab.find(varDecl.getVarName());
		if (promenljiva != Tab.noObj)
			report_error("Promenljiva " + varDecl.getVarName() + " je vec deklarisana!", null);
		else {
			report_info("Deklarisana promenljiva " + varDecl.getVarName(), varDecl);
			// Obj varNode = Tab.insert(Obj.Var, varDecl.getVarName(),
			// current_var_decl_type);
			var_list_array.add(varDecl);
			list_decl_arrays.add(varDecl.getVarName());
			
		}

	}

	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola", null);
			type.struct = Tab.noType;
		} else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
			} else {
				report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip ", type);
				type.struct = Tab.noType;
			}
		}
	}

	public void visit(DesignatorIDENT designator) {
	//	System.out.println("ULAZIM OVDE");
		Obj obj2 = Tab.find(designator.getDesigName());
		Obj obj=obj2;
	//	System.out.println(obj.getType().getKind());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designator.getLine() + " : ime " + designator.getDesigName()
					+ " nije deklarisano! ", null);
		}
		
		DesignatorList desig_list=designator.getDesignatorList(); // za rekorde rekord.a
		if(desig_list instanceof DotDesiList) {
			
			String field_name=((DotDesiList)desig_list).getDesigName();
	//		obj=Tab.find(field_name);
			
//			if(obj==Tab.noObj)
//				report_error("Greska na liniji " + designator.getLine() + " polje: " +field_name + " u rekordu: "
//				+ designator.getDesigName() + " ne postoji! ", null);
			
			Collection<Obj> Mylocals = obj.getType().getMembers();
			boolean found=false;
			members_list=new ArrayList<Obj>(Mylocals);
			for(int i=0;i<members_list.size();i++) {
				
				if(members_list.get(i).getName().equals(field_name)) {
					
					obj=members_list.get(i);
					found=true;
					break;
					
				}
				
			}
			if(!found)
				report_error("Greska na liniji " + designator.getLine() + " polje: " +field_name + " u rekordu: "
						+ designator.getDesigName() + " ne postoji! ", null);	
			
			
			
		}
		
		
		if (!assign_op)
			current_desig_type = obj.getType();
		else {
			if (obj.getType().getKind() == Struct.Int)
				current_assign_type = "int";
			else if (obj.getType().getKind() == Struct.Char)
				current_assign_type = "char";
			else if (obj.getType().getKind() == Struct.Bool)
				current_assign_type = "bool";
			else if(obj.getType().getKind() == Struct.Class) //zapravo rekord
				current_assign_type = "class";
			
		}
		DesignatorList desigList = designator.getDesignatorList(); //Za konteksni uslov da Designator mora biti Array X[5] npr;
		
		if(desigList instanceof ExprDesiList) {
			
			if(!list_decl_arrays.contains(designator.getDesigName())){
				report_error("Semanticka greska na liniji " + designator.getLine() + " : promenljiva " + designator.getDesigName()
				+ " ne predstavlja niz! ", null);
			}
			
		}
		
//		if(record_list.contains(designator.getDesigName()))
//			obj=new Obj(Obj.Var,designator.getDesigName(),new Struct(Struct.Class));
		
	//	System.out.println(obj.getKind());
		
		designator.obj = obj2;
	}




	public void visit(DesignatorStatementParsYes procCall) {
		Obj func = procCall.getDesignator().obj;
		if (Obj.Meth == func.getKind()) {
			report_info("Pronadjen poziv funkcije " + func.getName() + " na liniji " + procCall.getLine(), null);
			// RESULT = func.getType();
			
			ActPars act_pars=procCall.getActPars();
			if(func.getLevel()==1) { // samo 1 argument
				
				if(act_pars.getActParsList() instanceof ActParsListNo) {
					//provera tipa!
					Expr expr=act_pars.getExpr();
					
					Collection<Obj> coll=func.getLocalSymbols();
					ArrayList<Obj> col_list=new ArrayList<Obj>(coll);
					
					if(col_list.get(0).getType() == get_expr_type(expr)) {
						//sve je okej
					}else {
						report_error("Pogresan tip argumenta u funkciji: "+func.getName()  , procCall);

					}
					
					
				}else {
					//vise argumenata nego sto treba!
					report_error("Pogresan broj argumenta u funkciji: "+func.getName()  , procCall);
	
				}
				
			}else {
				//vise argumenata ima funckija
				
				if(act_pars.getActParsList() instanceof ActParsListNo) {
					//nema dovoljno argumenata
					report_error("Pogresan broj argumenta u funkciji: "+func.getName()  , procCall);

				}else {
					
					Expr expr=act_pars.getExpr();				
					Collection<Obj> coll=func.getLocalSymbols();
					ArrayList<Obj> col_list=new ArrayList<Obj>(coll);
					if(col_list.size()>0) {
						if(col_list.get(0).getType()==get_expr_type(expr)) { // sve ok
							//sve je okej
							}	
					}
					
					else {
						report_error("Pogresan tip argumenta u funkciji: "+func.getName()  , procCall);

					}
					
					int brojac=1;
					ActParsList trenutni= act_pars.getActParsList();
//					while(brojac < func.getLevel() || !(trenutni instanceof ActParsListNo)) {
//						
//						if(col_list.get(brojac).getType()==get_expr_type(((ActParsListMore)trenutni).getExpr())) {
//							//sve je okej
//						}else
//							report_error("Pogresan tip argumenta u funkciji: "+func.getName()  , procCall);
//	
//						brojac++;
//						trenutni= ((ActParsListMore)trenutni).getActParsList();
//
//						
//					}
					for(brojac=1 ; brojac < func.getLevel() ; brojac++) {
						if(trenutni instanceof ActParsListNo)
							break;
						if(col_list.get(brojac).getType()==get_expr_type(((ActParsListMore)trenutni).getExpr()) && get_expr_type(((ActParsListMore)trenutni).getExpr()).getKind()!=Struct.Array) {
							//sve je okej
						}else if(get_expr_type(((ActParsListMore)trenutni).getExpr()).getKind()==Struct.Array) {
							if(col_list.get(brojac).getType()==get_expr_type(((ActParsListMore)trenutni).getExpr()).getElemType()) {
								//sve okej
							}else 
								report_error("Pogresan tip argumenta u funkciji: "+func.getName()  , procCall);
							
								
							
						}
						else 
							report_error("Pogresan tip argumenta u funkciji: "+func.getName()  , procCall);
						
						trenutni= ((ActParsListMore)trenutni).getActParsList();

					}
					
					
					if(brojac != func.getLevel() || !(trenutni instanceof ActParsListNo))
						report_error("Pogresan broj argumenta u funkciji: "+func.getName()  , procCall);
					
					
				}
				
				
				
			}
			
			
			
			
			
			
			
			
			
			
			
		} else {
			report_error("Greska na liniji " + procCall.getLine() + " : ime " + func.getName() + " nije funkcija!",
					null);
			// RESULT = Tab.noType;
		}
		
		
		
		
//		if(!check_arg_nums(ime_funkcije,user_arg_num  ))
//			report_error("Greska na liniji " + procCall.getLine() + " pogresan broj argumenata za funkciju: " +user_arg_num   + func.getName(),
//					null);
//		else if(!check_arg_types(ime_funkcije,user_arg_types))
//			report_error("Greska na liniji " + procCall.getLine() + " Stvarni argmenti ne odgovaraju formalnim u funkciji: " +user_arg_num   + func.getName(),
//					null);
		
		
		//System.out.println("Broj arg:" + user_arg_num + " Funckija: "+ ime_funkcije);
		
		//user_arg_num=0;
		//user_arg_types.clear();
		
	}

	public void visit(DesignatorStatementParsNo procCall) {
		Obj func = procCall.getDesignator().obj;
		if (Obj.Meth == func.getKind()) {
			report_info("Pronadjen poziv funkcije " + func.getName() + " na liniji " + procCall.getLine(), null);
			// RESULT = func.getType();
		} else {
			report_error("Greska na liniji " + procCall.getLine() + " : ime " + func.getName() + " nije funkcija!",
					null);
			// RESULT = Tab.noType;
		}
		String ime_funkcije= ((DesignatorIDENT)(procCall.getDesignator())).getDesigName();
		
//		if(!check_arg_nums(ime_funkcije,0))
//			report_error("Greska na liniji " + procCall.getLine() + " pogresan broj argumenata za funkciju: " + func.getName(),
//					null);
		
		user_arg_num=0;
			
		user_arg_types.clear();

		
	}

	// Konteksni uslov const

	public void visit(ConstDeclMore constDeclMore) {
		report_info("Dodata u listu konstanta " + constDeclMore.getConstName(), constDeclMore);
		any_const_list.add(constDeclMore);

	}
	
	
	public void visit(ConstDeclList constDeclList) {
		currentType = constDeclList.getType();
		report_info("Trenutan tip konstante je:  " + currentType.getTypeName() + " na liniji " + currentType.getLine(),
				null);
		if ((constDeclList.getAnyConst() instanceof NumConst && currentType.getTypeName().equals("int"))
				|| (constDeclList.getAnyConst() instanceof CharConst && currentType.getTypeName().equals("char"))
				|| (constDeclList.getAnyConst() instanceof BoolConst && currentType.getTypeName().equals("bool"))) {

			Obj con=Tab.insert(Obj.Con, constDeclList.getConstName(), constDeclList.getType().struct);
			AnyConst any_const = constDeclList.getAnyConst();
			
			if(con.getType().getKind()==Struct.Int) {
				NumConst number=(NumConst)(any_const);
				con.setAdr(number.getN1());
			}else if(con.getType().getKind()==Struct.Char) {
				CharConst char_const=(CharConst)(any_const);
				String c=char_const.getC1();
				con.setAdr(c.charAt(1));
				
			}else {//bool
				BoolConst bool_const=(BoolConst)(any_const);
				String bool_val=bool_const.getB1();
				if(bool_val.equals("true"))
					con.setAdr(1);
				else
					con.setAdr(0);
			}
			
			
			
			for (int i = 0; i < any_const_list.size(); i++) {

				if (any_const_list.get(i).getAnyConst() instanceof NumConst
						&& currentType.getTypeName().equals("int")) {

					Obj elem=Tab.insert(Obj.Con, any_const_list.get(i).getConstName(), constDeclList.getType().struct);
					AnyConst elem_const = any_const_list.get(i).getAnyConst();
					NumConst elem_number=(NumConst)(elem_const);
					elem.setAdr(elem_number.getN1());

				} else if (any_const_list.get(i).getAnyConst() instanceof CharConst
						&& currentType.getTypeName().equals("char")) {
					Obj elem=Tab.insert(Obj.Con, any_const_list.get(i).getConstName(), constDeclList.getType().struct);
					AnyConst elem_const = any_const_list.get(i).getAnyConst();
					CharConst elem_number=(CharConst)(elem_const);
					String c=elem_number.getC1();
					elem.setAdr(c.charAt(1));

				} else if (any_const_list.get(i).getAnyConst() instanceof BoolConst
						&& currentType.getTypeName().equals("bool")) {
					Obj elem=Tab.insert(Obj.Con, any_const_list.get(i).getConstName(), constDeclList.getType().struct);
					AnyConst elem_const = any_const_list.get(i).getAnyConst();
					BoolConst elem_number=(BoolConst)(elem_const);
					if(elem_number.getB1().equals("true"))
						elem.setAdr(1);
					else
						elem.setAdr(0);



				} else {
					report_error("Greska na liniji " + any_const_list.get(i).getLine() + " : Tip "
							+ currentType.getTypeName() + " ne odgovara vrednosti konstante "
							+ any_const_list.get(i).getConstName(), null);

				}

			}

		} else {
			report_error("Greska na liniji " + constDeclList.getLine() + " : Tip " + currentType.getTypeName()
					+ " ne odgovara vrednosti konstante " + constDeclList.getConstName(), null);
		}
		any_const_list.clear();
	}

	// konteksni uslov class

	public ArrayList<String> record_list = new ArrayList<String>();

	public void visit(ClassDeclaration classDecl) {
		Obj klasa = Tab.find(classDecl.getClassName());
		if (klasa == Tab.noObj) {
			report_info("Deklarisana je klasa :  " + classDecl.getClassName() + " na liniji " + classDecl.getLine(),
					null);
			Tab.insert(Obj.Type, classDecl.getClassName(), new Struct(Struct.Class));
		} else
			report_error("Greska na liniji " + classDecl.getLine() + " Klasa: " + classDecl.getClassName()
					+ " je vec deklarisana! ", null);

	}

	public void visit(ClassDeclExtendsYes classExtends) {
		Type class_name = classExtends.getType();
		Obj klasa = Tab.find(class_name.getTypeName());
		if (klasa == Tab.noObj)
			report_error("Greska na liniji " + classExtends.getLine() + " : Klasa " + class_name.getTypeName()
					+ " ne postoji! ", null);
		else if (record_list.contains(class_name.getTypeName())) {
			report_error("Greska na liniji " + classExtends.getLine() + " " + class_name.getTypeName()
					+ " predstavlja rekord! ", null);

		}

	}
	// konteksni uslov record
	
	boolean record_define=false;
	public void visit(RecordName rcName) {
		
		record_define=true;
		
		Obj rekord = Tab.find(rcName.getRName());
		if (rekord == Tab.noObj) {
			report_info("Deklarisana je rekord :  " + rcName.getRName() + " na liniji " + rcName.getLine(),
					null);
			currentMethod=Tab.insert(Obj.Type, rcName.getRName(), new Struct(Struct.Class));
			
			
			record_list.add(rcName.getRName());

		} else
			report_error("Greska na liniji " + rcName.getLine() + " Rekord: " + rcName.getRName()
					+ " je vec deklarisan! ", null);
		Tab.openScope();
	}
	
	public void visit(RecordDecls recordDecl) {
		
//		Obj rekord = Tab.find(recordDecl.getRecordName().getRName());
//		if (rekord == Tab.noObj) {
//			report_info("Deklarisana je rekord :  " + recordDecl.getRecordName().getRName() + " na liniji " + recordDecl.getLine(),
//					null);
//			currentMethod=Tab.insert(Obj.Type, recordDecl.getRecordName().getRName(), new Struct(Struct.Class));
//			record_list.add(recordDecl.getRecordName().getRName());
//
//		} else
//			report_error("Greska na liniji " + recordDecl.getLine() + " Rekord: " + recordDecl.getRecordName().getRName()
//					+ " je vec deklarisan! ", null);
	
		
		//prepravljeno
		
			currentMethod.getType().setMembers(Tab.currentScope.getLocals());
	//	Tab.chainLocalSymbols(currentMethod.getType());
		Tab.closeScope();
		record_define=false;
		currentMethod=null;
		
	}

	// konteksni uslov MethodDecl

	Type current_meth_type = null;
	String current_meth_name=null;
	int current_num_arg=0;
	
	public void visit(MethodTypeName methodTypeName) {
		
		current_meth_name=methodTypeName.getFuncName();
		if (current_meth_type == null) 
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getFuncName(), new Struct(Struct.None)); 

		
		else
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getFuncName(), current_meth_type.struct); 
		methodTypeName.obj = currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodTypeName.getFuncName(), methodTypeName);

	}

	public void visit(MethodDeclTypeOther MethodType) {
		current_meth_type = MethodType.getType();
	}

	public void visit(SingleStatementDesignatorRETURNExpr return_expr) {
		Expr expr = return_expr.getExpr();
		if (expr instanceof ExprNoMinus) {
			Factor fac = ((ExprNoMinus) expr).getTerm().getFactor();
			String fac_type = "";
			if (current_meth_type != null) {

				if (fac instanceof FactorNum)
					fac_type = "int";
				else if (fac instanceof FactorChar)
					fac_type = "char";
				else if (fac instanceof FactorBool)
					fac_type = "bool";
				else if (fac instanceof FactorDesignator) {
						
					Designator desi = ((FactorDesignator) fac).getDesignator();
					
					if (desi instanceof DesignatorIDENT) {
						String desig_name = ((DesignatorIDENT) desi).getDesigName();
						
						DesignatorList desi_list=((DesignatorIDENT) desi).getDesignatorList(); // za rekord
						
						Obj temp = Tab.find(desig_name);
						if (temp == Tab.noObj)
							report_error("Semanticka greska na liniji " + return_expr.getLine() + "  " + desig_name
									+ " ne postoji!", null);
						
						
						if(desi_list instanceof DotDesiList) {
							String field_name= ((DotDesiList)desi_list).getDesigName();
							
							Collection<Obj> Mylocals = temp.getType().getMembers();
							boolean found=false;
							members_list=new ArrayList<Obj>(Mylocals);
							for(int i=0;i<members_list.size();i++) {
								
								if(members_list.get(i).getName().equals(field_name)) {
									
									temp=members_list.get(i);
									found=true;
									break;
									
								}
								
							}
							if(!found)
								report_error("Greska na liniji " + return_expr.getLine() + " polje: " +field_name + " u rekordu: "
										+ desig_name + " ne postoji! ", null);	
							
							
							
							
						}
						 if (temp.getType() == Tab.intType)
								fac_type = "int";
							 if (temp.getType() == Tab.charType)
								fac_type = "char";
							 if (temp.getType().getKind() == 5)
								fac_type = "bool";
						
						
						

					} else
						report_error("Semanticka greska na liniji " + return_expr.getLine()
								+ " nekorektan povratni tip u iskazu!", null);
					
					

				}else if( fac instanceof FactorDesignatorPars) { //povratni tip je funckija sa argumentima
					
					Designator desi= ((FactorDesignatorPars)fac).getDesignator();
					String func_name=((DesignatorIDENT)desi).getDesigName();
					Obj func_obj=Tab.find(func_name);
					
					if(func_obj == Tab.noObj)
						report_error("Semanticka greska na liniji " + return_expr.getLine()
						+ " funckija: "+ func_name + " ne postoji!" , null);
					else if(func_obj.getType() == Tab.intType)
						fac_type="int";
					else if(func_obj.getType() == Tab.charType)	
						fac_type="char";
					else if(func_obj.getType().getKind() == 5)
						fac_type="bool";

						
					
					
					
					
				}else if(fac instanceof FactorDesignatorNoPars) {//povratni tip je funckija bez argumenata
					
					Designator desi= ((FactorDesignatorNoPars)fac).getDesignator();
					String func_name=((DesignatorIDENT)desi).getDesigName();
					Obj func_obj=Tab.find(func_name);
					
					if(func_obj == Tab.noObj)
						report_error("Semanticka greska na liniji " + return_expr.getLine()
						+ " funckija: "+ func_name + " ne postoji!" , null);
					else if(func_obj.getType() == Tab.intType)
						fac_type="int";
					else if(func_obj.getType() == Tab.charType)	
						fac_type="char";
					else if(func_obj.getType().getKind() == 5)
						fac_type="bool";
					
					
					
					
				}
				if (!current_meth_type.getTypeName().equals(fac_type))
					report_error("Semanticka greska na liniji " + return_expr.getLine() + ": funkcija "
							+ currentMethod.getName() + " kao povratni tip prihvata: "
							+ current_meth_type.getTypeName(), null);
			}

		}
		user_arg_num=0;
		user_arg_types=new ArrayList<String>();
		returnFound = true;
	}

	public void visit(MethodDeclaration methodDecl) {
		if (!returnFound && current_meth_type != null) {
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funkcija " + currentMethod.getName()
					+ " nema return iskaz!", null);
		} else if (returnFound && current_meth_type == null)
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funkcija " + currentMethod.getName()
					+ " ne moze imati return iskaz ako je povratni tip void!", null);

		for (int i = 0; i < label_goto_list.size(); i++)
			if (!label_def_list.contains(label_goto_list.get(i)))
				report_error("Semanticka greska na liniji " + label_error_line.get(i) + ": labela "
						+ label_goto_list.get(i) + " nije definisana u funkciji: " + currentMethod.getName(), null);

		
		
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		
		
			
		func_struct fs=new func_struct(current_meth_name,current_num_arg,current_arg_types);
		
		func_list.add(fs);
		
		current_meth_name=null;
		current_num_arg=0;
		current_arg_types = new ArrayList<String>();
		
		returnFound = false;
		currentMethod = null;
		label_def_list.clear();
		label_goto_list.clear();
		label_error_line.clear();
		current_meth_type = null;
	}

//konteksni uslov Designator AssignOp

	
	public void visit(DesignatorStatementAssign DesigState) {
		if (current_desig_type.getKind() == Struct.Int && current_assign_type == "int") {

		} else if (current_desig_type.getKind() == Struct.Char && current_assign_type == "char") {

		} else if (current_desig_type.getKind() == Struct.Bool && current_assign_type == "bool") {
			
		}else if (current_desig_type.getKind() == Struct.Class ) {
			
//			DesignatorIDENT desi= (DesignatorIDENT)DesigState.getDesignator();
//			
//			DesignatorList desi_list= desi.getDesignatorList();
//			if(desi_list instanceof DotDesiList) {
//				DotDesiList desi_dot = (DotDesiList)desi_list;
//				
//				String field_name=desi_dot.getDesigName();
//				
//				Obj designator_real=Tab.find(desi.getDesigName());
//				
//				Collection<Obj> coll=designator_real.getType().getMembers();
//
//				ArrayList<Obj> col_list=new ArrayList<Obj>(coll);
//				System.out.println(col_list.size());
//
//				for(int i=0;i<col_list.size();i++)
//					if(col_list.get(i).getName().equals(field_name)) {
//						current_desig_type=col_list.get(i).getType();
//						break;
//					}
//				System.out.println(current_desig_type.getKind());
//				
//				
//				
//				if (current_desig_type.getKind() == Struct.Int && current_assign_type == "int") {
//
//				} else if (current_desig_type.getKind() == Struct.Char && current_assign_type == "char") {
//
//				} else if (current_desig_type.getKind() == Struct.Bool && current_assign_type == "bool") {
//					
//				}else
//					report_error(
//							"Semanticka greska na liniji " + DesigState.getLine() + " nekompatibilni tipovi prilikom dodele!",
//							null);
//					
//				
//				
//				
//				
//			}
//			
			

			
		}else if(current_desig_type.getKind()==Struct.Array) {
			
			if(current_desig_type.getElemType().getKind() == Struct.Int && current_assign_type == "int") {
				
			}else if(current_desig_type.getElemType().getKind() == Struct.Bool && current_assign_type == "bool") {
				
			}else if(current_desig_type.getElemType().getKind() == Struct.Char && current_assign_type == "char") {
				
			}else
				report_error(
						"Semanticka greska na liniji " + DesigState.getLine() + " nekompatibilni tipovi prilikom dodele!",
						null);
			
		} 
		else
			report_error(
					"Semanticka greska na liniji " + DesigState.getLine() + " nekompatibilni tipovi prilikom dodele!",
					null);
		current_desig_type = null;
		current_assign_type = null;
		assign_op = false;
	}

	public void visit(Term term) {

		if (term.getFactor() instanceof FactorNum)
			current_assign_type = "int";
		else if (term.getFactor() instanceof FactorChar)
			current_assign_type = "char";
		else if (term.getFactor() instanceof FactorBool)
			current_assign_type = "bool";
		else if (term.getFactor() instanceof FactorDesignator) {

		}else if(term.getFactor() instanceof FactorNewTypeExpr) {//za niz
			
			FactorNewTypeExpr f=((FactorNewTypeExpr)term.getFactor());
			Type t=f.getType();
			if(t.getTypeName().equals("int"))
			current_assign_type="int";
			else if(t.getTypeName().equals("char"))
			current_assign_type="char";
			else if(t.getTypeName().equals("bool"))
			current_assign_type="bool";

		}else if(term.getFactor() instanceof FactorNewType) { // za new Record
			FactorNewType new_type=((FactorNewType)term.getFactor());
			
			if(!record_list.contains(new_type.getType().getTypeName()))
			{
				report_error(
						"Semanticka greska na liniji " + term.getLine() + " Navedeni rekord ne postoji!",
						null);
			}else
				current_assign_type="class";	
					
				
					
		}
		
	}

	public void visit(Assignop assignOp) {
		assign_op = true;
	}

//konteksni uslov Designator ++ | --

	public void visit(DesignatorStatementDPLUS DesigDPlus) {

		if (current_desig_type.getKind() != Struct.Int && current_desig_type.getKind()!=Struct.Array)
			report_error("Semanticka greska na liniji " + DesigDPlus.getLine() + " ++ operator zahteva tip Int!", null);
		else if(current_desig_type.getKind()==Struct.Array && current_desig_type.getElemType().getKind()!=Struct.Int)
			report_error("Semanticka greska na liniji " + DesigDPlus.getLine() + " ++ operator zahteva tip Int!", null);

		current_desig_type = null;
		
	}

	public void visit(DesignatorStatementDMINUS DesigDMinus) {
		if (current_desig_type.getKind() != Struct.Int && current_desig_type.getKind()!=Struct.Array)
			report_error("Semanticka greska na liniji " + DesigDMinus.getLine() + " ++ operator zahteva tip Int!", null);
		else if(current_desig_type.getKind()==Struct.Array && current_desig_type.getElemType().getKind()!=Struct.Int)
			report_error("Semanticka greska na liniji " + DesigDMinus.getLine() + " ++ operator zahteva tip Int!", null);

		current_desig_type = null;

	}

//konteksni uslov Goto Label

	public void visit(StatementLabel Statementlabel) {

	}

	public void visit(Label label) {
		if (goto_op) {
			label_goto_list.add(label.getLabelName()); // dodavanje labele u listu goto labela
			label_error_line.add(label.getLine());
		} else
			label_def_list.add(label.getLabelName()); // dodvanje labele u listu definisanih labela
	}

	public void visit(SingleStatementDesignatorPRINTGOTO gotoLabel) {
		goto_op = false;
	}

	public void visit(Goto GOTO) {
		goto_op = true;
	}

//konteksni uslov break
	boolean do_statement = false;

	public void visit(SingleStatementDesignatorBREAK StatementBreak) {

		if (!do_statement)
			report_error("Semanticka greska! "
					+ " break moze da se javi samo u okviru do-while petlje! ", StatementBreak);

	}

	public void visit(SingleStatementDOState StatementDoWhile) {
		do_statement = false;
	}

	public void visit(Do DoStm) {
		do_statement = true;
	}
//konteksni uslov continue 

	public void visit(SingleStatementDesignatorCONTINUE StatementContinue) {

		if (!do_statement)
			report_error("Semanticka greska na liniji " + StatementContinue.getLine()
					+ " continue moze da se javi samo u okviru do-while petlje! ", null);

	}

//konteksni uslov read	

	public void visit(SingleStatementDesignatorREAD read_statement) {
		Designator desi = read_statement.getDesignator();

		if (desi instanceof DesignatorIDENT) {
			String desig_name = ((DesignatorIDENT) desi).getDesigName();
			Obj temp = Tab.find(desig_name);
			if (temp == Tab.noObj)
				report_error(
						"Semanticka greska na liniji " + read_statement.getLine() + "  " + desig_name + " ne postoji!",
						null);
			DesignatorList desig_list= ((DesignatorIDENT) desi).getDesignatorList();
			
			if(desig_list instanceof DotDesiList) { // za rekord
				
				String field_name=((DotDesiList)desig_list).getDesigName();
				Collection<Obj> Mylocals = temp.getType().getMembers();
				boolean found=false;
				members_list=new ArrayList<Obj>(Mylocals);
				for(int i=0;i<members_list.size();i++) {
					
					if(members_list.get(i).getName().equals(field_name)) {
						
						temp=members_list.get(i);
						found=true;
						break;
						
					}
					
				}
				if(!found)
					report_error("Greska na liniji " + read_statement.getLine() + " polje: " +field_name + " u rekordu: "
							+ desig_name + " ne postoji! ", null);	
				
				
				
			}
			
			
			
			
			 if (temp.getType() != Tab.intType && temp.getType() != Tab.charType && (temp.getType().getKind() != 5) && temp.getType().getKind()!=Struct.Array)
				report_error("Semanticka greska na liniji " + read_statement.getLine() + "  " + desig_name
						+ " mora biti int, char ili bool!", null);
			 else if(temp.getType().getKind()==Struct.Array && temp.getType().getElemType() != Tab.intType && temp.getType().getElemType() != Tab.charType
					 && temp.getType().getElemType().getKind() != 5)
				 report_error("Semanticka greska na liniji " + read_statement.getLine() + "  " + desig_name
							+ " mora biti int, char ili bool!", null);
		}
	}

//konteksni uslov print

	public void visit(SingleStatementDesignatorPRINT print_statement) {

		Expr expr = print_statement.getExpr();

		if (expr instanceof ExprNoMinus) {
			Factor factor = ((ExprNoMinus) expr).getTerm().getFactor();
			if (!(factor instanceof FactorNum || factor instanceof FactorChar || factor instanceof FactorBool
					|| factor instanceof FactorDesignator || factor instanceof FactorDesignatorPars || factor instanceof FactorDesignatorNoPars ))
				report_error("Semanticka greska na liniji " + print_statement.getLine()
						+ " print prima samo num, char ili bool!", null);

			if (factor instanceof FactorDesignator) {

				Designator desi = ((FactorDesignator) factor).getDesignator();
				if (desi instanceof DesignatorIDENT) {
					String desig_name = ((DesignatorIDENT) desi).getDesigName();
					Obj temp = Tab.find(desig_name);
					if (temp == Tab.noObj)
						report_error("Semanticka greska na liniji " + print_statement.getLine() + "  " + desig_name
								+ " ne postoji!", null);
					
					DesignatorList desig_list= ((DesignatorIDENT) desi).getDesignatorList();
					
					if(desig_list instanceof DotDesiList) { // za rekord
						
						String field_name=((DotDesiList)desig_list).getDesigName();
						Collection<Obj> Mylocals = temp.getType().getMembers();
						boolean found=false;
						members_list=new ArrayList<Obj>(Mylocals);
						for(int i=0;i<members_list.size();i++) {
							
							if(members_list.get(i).getName().equals(field_name)) {
								
								temp=members_list.get(i);
								found=true;
								break;
								
							}
							
						}
						if(!found)
							report_error("Greska na liniji " + print_statement.getLine() + " polje: " +field_name + " u rekordu: "
									+ desig_name + " ne postoji! ", null);	
						
						
						
					}else if(desig_list instanceof ExprDesiList) { // za niz
						
					}
					
					
					 if (temp.getType() != Tab.intType && temp.getType() != Tab.charType
							&& (temp.getType().getKind() != 5) &&  temp.getType().getKind()!=Struct.Array)
						report_error("Semanticka greska na liniji " + print_statement.getLine() + "  " + desig_name
								+ " mora biti int, char ili bool!", null);

				}

			}else if (factor instanceof FactorDesignatorPars) { //funckija sa argumentima
				Designator desi=((FactorDesignatorPars)factor).getDesignator();
				String func_name=((DesignatorIDENT)desi).getDesigName();
				Obj func_obj=Tab.find(func_name);
				if(func_obj==Tab.noObj)
					report_error("Semanticka greska na liniji " + print_statement.getLine() + "  " + func_name
							+ " nije funkcija!", null);	
				else {
					
					
					
					if(func_obj.getType()!=Tab.intType && func_obj.getType()!=Tab.charType && (func_obj.getType().getKind() != 5))
						report_error("SEMAnticka greska na liniji " + print_statement.getLine() + "  " + func_name
								+ " mora biti int, char ili bool!", null);
				}
				
				
				
			}else if(factor instanceof FactorDesignatorNoPars) { //funkcija bez argumenata
				
				Designator desi=((FactorDesignatorNoPars)factor).getDesignator();
				
				String func_name=((DesignatorIDENT)desi).getDesigName();
				Obj func_obj=Tab.find(func_name);
				if(func_obj==Tab.noObj)
					report_error("Semanticka greska na liniji " + print_statement.getLine() + "  " + func_name
							+ " nije funkcija!", null);	
				else {
					
					if(func_obj.getType()!=Tab.intType && func_obj.getType()!=Tab.charType && (func_obj.getType().getKind() != 5))
						report_error("Semanticka greska na liniji " + print_statement.getLine() + "  " + func_name
								+ " mora biti int, char ili bool!", null);
				}
				
				
				
			}

		}

	}
	
//konteksni uslov za If condition
	
	
//	public void visit(IfCondGood if_cond) {
	public void visit(ContFactTwo cond_fact_two) {
		
		Expr expr1 = cond_fact_two.getExpr1();
		Expr expr2 = cond_fact_two.getExpr();

		if(expr1 instanceof  ExprNoMinus ) {
			Term term=((ExprNoMinus)expr1).getTerm();
			Factor fac= term.getFactor();
			
			if( !(fac instanceof FactorNum || fac instanceof FactorDesignator || fac instanceof FactorDesignatorPars || fac instanceof FactorDesignatorNoPars ))
				report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
						+ " poredjenje se moze vrsiti samo sa integerima!", null);	
			
			if(fac instanceof FactorDesignator) {
				Designator desi=((FactorDesignator)fac).getDesignator();
				
				String desi_name=((DesignatorIDENT)desi).getDesigName();
				Obj desi_obj = Tab.find(desi_name);
				if(desi_obj == Tab.noObj)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " promenljiva: "+desi_name + " ne postoji!" , null);
				
				DesignatorList desi_list=((DesignatorIDENT)desi).getDesignatorList();
				
				if(desi_list instanceof DotDesiList) { // rekord
					String field_name = ((DotDesiList)(desi_list)).getDesigName();
					
					Collection<Obj> Mylocals = desi_obj.getType().getMembers();
					boolean found=false;
					members_list=new ArrayList<Obj>(Mylocals);
					for(int i=0;i<members_list.size();i++) {
						
						if(members_list.get(i).getName().equals(field_name)) {
							
							desi_obj=members_list.get(i);
							found=true;
							break;
							
						}
						
					}
					if(!found)
						report_error("Greska na liniji " + cond_fact_two.getLine() + " polje: " +field_name + " u rekordu: "
								+ desi_name + " ne postoji! ", null);	
					
					
					
					
				}
				
				
				if(desi_obj.getType()!=Tab.intType &&  !(desi_list instanceof ExprDesiList))
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " promenljiva: "+ desi_name + " mora biti tipa int!", null);	
				else if(desi_list instanceof ExprDesiList && desi_obj.getType().getElemType().getKind()!=Struct.Int)	
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " promenljiva: "+ desi_name + " mora biti tipa int!", null);	
			}else if(fac instanceof FactorDesignatorPars) { // funkcija koja vraca int
				
				Designator desi=((FactorDesignatorPars)fac).getDesignator();
				
				String desi_name=((DesignatorIDENT)desi).getDesigName();
				Obj desi_obj = Tab.find(desi_name);
				if(desi_obj == Tab.noObj)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " funckija: "+desi_name + " ne postoji!" , null);
				else if(desi_obj.getType()!=Tab.intType)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " povratni tip funkcije: " + " mora biti tipa int!", null);	
				
			}else if(fac instanceof FactorDesignatorNoPars) {
				Designator desi=((FactorDesignatorNoPars)fac).getDesignator();
				
				String desi_name=((DesignatorIDENT)desi).getDesigName();
				Obj desi_obj = Tab.find(desi_name);
				if(desi_obj == Tab.noObj)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " funckija: "+desi_name + " ne postoji!" , null);
				else if(desi_obj.getType()!=Tab.intType)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " povratni tip funkcije: " + " mora biti tipa int!", null);	
				
				
			}
			
			
		}
		
		if(expr2 instanceof  ExprNoMinus ) {
			Term term=((ExprNoMinus)expr2).getTerm();
			Factor fac= term.getFactor();
			
			if( !(fac instanceof FactorNum || fac instanceof FactorDesignator || fac instanceof FactorDesignatorPars || fac instanceof FactorDesignatorNoPars ))
				report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
						+ " poredjenje se moze vrsiti samo sa integerima!", null);	
			
			if(fac instanceof FactorDesignator) {
				Designator desi=((FactorDesignator)fac).getDesignator();
				
				String desi_name=((DesignatorIDENT)desi).getDesigName();
				Obj desi_obj = Tab.find(desi_name);
				if(desi_obj == Tab.noObj)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " promenljiva: "+desi_name + " ne postoji!" , null);
				
				DesignatorList desi_list=((DesignatorIDENT)desi).getDesignatorList();
				
				if(desi_list instanceof DotDesiList) { // rekord
					String field_name = ((DotDesiList)(desi_list)).getDesigName();
					
					Collection<Obj> Mylocals = desi_obj.getType().getMembers();
					boolean found=false;
					members_list=new ArrayList<Obj>(Mylocals);
					for(int i=0;i<members_list.size();i++) {
						
						if(members_list.get(i).getName().equals(field_name)) {
							
							desi_obj=members_list.get(i);
							
							
							found=true;
							break;
							
						}
						
					}
					if(!found)
						report_error("Greska na liniji " + cond_fact_two.getLine() + " polje: " +field_name + " u rekordu: "
								+ desi_name + " ne postoji! ", null);	
					
					
					
					
				}
				
				
				if(desi_obj.getType()!=Tab.intType && desi_obj.getType().getKind()!=Struct.Array)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " promenljiva: "+ desi_name + " mora biti tipa int!", null);	
				else if(desi_obj.getType().getKind()==Struct.Array && desi_obj.getType().getElemType()!=Tab.intType)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " promenljiva: "+ desi_name + " mora biti tipa int!", null);
				
			}else if(fac instanceof FactorDesignatorPars) { // funkcija koja vraca int
				
				Designator desi=((FactorDesignatorPars)fac).getDesignator();
				
				String desi_name=((DesignatorIDENT)desi).getDesigName();
				Obj desi_obj = Tab.find(desi_name);
				if(desi_obj == Tab.noObj)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " funckija: "+desi_name + " ne postoji!" , null);
				else if(desi_obj.getType()!=Tab.intType)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " povratni tip funkcije: " + " mora biti tipa int!", null);	
				
			}else if(fac instanceof FactorDesignatorNoPars) {
				Designator desi=((FactorDesignatorNoPars)fac).getDesignator();
				
				String desi_name=((DesignatorIDENT)desi).getDesigName();
				Obj desi_obj = Tab.find(desi_name);
				if(desi_obj == Tab.noObj)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " funckija: "+desi_name + " ne postoji!" , null);
				else if(desi_obj.getType()!=Tab.intType)
					report_error("Semanticka greska na liniji " + cond_fact_two.getLine() 
					+ " povratni tip funkcije: " + " mora biti tipa int!", null);	
				
				
			}
			
			
		}
		
		
		
	}
	
	
	public void visit(CondFactOne cond_fact_one) {
		Expr expr=cond_fact_one.getExpr();
		
		if(expr instanceof  ExprNoMinus) {
		Term term=((ExprNoMinus)expr).getTerm();
		Factor fac= term.getFactor();
		
		if( !(fac instanceof FactorBool || fac instanceof FactorDesignator || fac instanceof FactorDesignatorPars || fac instanceof FactorDesignatorNoPars ))
			report_error("Semanticka greska na liniji " + cond_fact_one.getLine() 
					+ " uslov u If-u mora biti tipa bool!", null);	
		
		if(fac instanceof FactorDesignator) {
			Designator desi=((FactorDesignator)fac).getDesignator();
			
			String desi_name=((DesignatorIDENT)desi).getDesigName();
			Obj desi_obj = Tab.find(desi_name);
			if(desi_obj == Tab.noObj)
				report_error("Semanticka greska na liniji " + cond_fact_one.getLine() 
				+ " promenljiva: "+desi_name + " ne postoji!" , null);
			else if(desi_obj.getType().getKind()!=5)
				report_error("Semanticka greska na liniji " + cond_fact_one.getLine() 
				+ " promenljiva: "+ desi_name + " mora biti tipa bool!", null);	
			
			
		}else if(fac instanceof FactorDesignatorPars) { // funkcija koja vraca boolean
			
			Designator desi=((FactorDesignatorPars)fac).getDesignator();
			
			String desi_name=((DesignatorIDENT)desi).getDesigName();
			Obj desi_obj = Tab.find(desi_name);
			if(desi_obj == Tab.noObj)
				report_error("Semanticka greska na liniji " + cond_fact_one.getLine() 
				+ " funckija: "+desi_name + " ne postoji!" , null);
			else if(desi_obj.getType().getKind()!=5)
				report_error("Semanticka greska na liniji " + cond_fact_one.getLine() 
				+ " povratni tip funkcije: " + " mora biti tipa bool!", null);	
			
		}else if(fac instanceof FactorDesignatorNoPars) {
			Designator desi=((FactorDesignatorNoPars)fac).getDesignator();
			
			String desi_name=((DesignatorIDENT)desi).getDesigName();
			Obj desi_obj = Tab.find(desi_name);
			if(desi_obj == Tab.noObj)
				report_error("Semanticka greska na liniji " + cond_fact_one.getLine() 
				+ " funckija: "+desi_name + " ne postoji!" , null);
			else if(desi_obj.getType().getKind()!=5)
				report_error("Semanticka greska na liniji " + cond_fact_one.getLine() 
				+ " povratni tip funkcije: " + " mora biti tipa bool!", null);	
			
			
		}
		
		
	}
		
	}
	
	// int y[];
	// int x;
	// x=y[5];
	
//konteksni uslov Designator = Designator ʺ[ʺ Expr ʺ]ʺ
		
	public void visit(ExprDesiList expr_desi_list) {
		
		Expr expr_array=expr_desi_list.getExpr();
		
		if(expr_array instanceof ExprNoMinus) {
			Term term=((ExprNoMinus)expr_array).getTerm();
			Factor fact=term.getFactor();
			
			if(!(fact instanceof FactorNum) && !(fact instanceof FactorDesignator) && !(fact instanceof FactorExpr) )
				report_error("Semanticka greska na liniji " + expr_desi_list.getLine() + " za indexiranje je potreban tip int!", null);
			
			else if(fact instanceof FactorDesignator) {
				
				Designator desi= ((FactorDesignator)fact).getDesignator();
				
				if(desi instanceof DesignatorIDENT) {
					String desi_name=((DesignatorIDENT)desi).getDesigName();
					Obj promenljiva=Tab.find(desi_name);
					
					if(promenljiva==Tab.noObj) {
						report_error("Greska na liniji " + expr_desi_list.getLine() + " : ime " + desi_name
						+ " nije deklarisano! ", null);
					}else if(promenljiva.getType().getKind()!=Struct.Int && promenljiva.getType().getKind()!=Struct.Array )	
					report_error("Semanticka greska na liniji " + expr_desi_list.getLine() + " za indexiranje je potreban tip int!", null);
					else if(promenljiva.getType().getKind()==Struct.Array && promenljiva.getType().getElemType().getKind()!=Struct.Int )	
					report_error("Semanticka greska na liniji " + expr_desi_list.getLine() + " za indexiranje je potreban tip int!", null);

						
					
					
					
				}
				
				
			}else if(fact instanceof FactorExpr) {
				
				
				FactorExpr ff=((FactorExpr)fact);
				Expr ff_expr=ff.getExpr();
				
				if(ff_expr instanceof ExprNoMinus) {
					
					Term tt_expr= ((ExprNoMinus)ff_expr).getTerm();
					Factor fac= tt_expr.getFactor();
					
					if(!( fac instanceof FactorNum) && !( fac instanceof FactorDesignator)){
					report_error("Semanticka greska na liniji " + expr_desi_list.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
		
					}else if(fac instanceof FactorDesignator) {
						
						Designator desii= ((FactorDesignator)fac).getDesignator();
						
						if(desii instanceof DesignatorIDENT) {
							String desi_name=((DesignatorIDENT)desii).getDesigName();
							Obj promenljiva=Tab.find(desi_name);
							
							if(promenljiva==Tab.noObj) {
								report_error("Greska na liniji " + expr_desi_list.getLine() + " : ime " + desi_name
								+ " nije deklarisano! ", null);
							}else if(promenljiva.getType().getKind()!=Struct.Int)
							report_error("Semanticka greska na liniji " + expr_desi_list.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
								
									
							
							
						}
						
						
						
					}
					
					
					
				}
				
				
				
				
				
				
				
				
				
			}
			
			
		}
		
		
	}
	
	//konteksni uslov za new array[5]
	
	public void visit(FactorNewTypeExpr factor_new_expr) {
		
		Expr expr_array=factor_new_expr.getExpr();
		if(expr_array instanceof ExprNoMinus) {
			Term term=((ExprNoMinus)expr_array).getTerm();
			Factor fact=term.getFactor();
			
			if(!(fact instanceof FactorNum) && !(fact instanceof FactorDesignator) && !(fact instanceof FactorExpr))
				report_error("Semanticka greska na liniji " + factor_new_expr.getLine() + " za indexiranje je potreban tip int!", null);
			
			else if(fact instanceof FactorDesignator) {
				
				Designator desi= ((FactorDesignator)fact).getDesignator();
				
				if(desi instanceof DesignatorIDENT) {
					String desi_name=((DesignatorIDENT)desi).getDesigName();
					Obj promenljiva=Tab.find(desi_name);
					
					if(promenljiva==Tab.noObj) {
						report_error("Greska na liniji " + factor_new_expr.getLine() + " : ime " + desi_name
						+ " nije deklarisano! ", null);
					}else if(promenljiva.getType().getKind()!=Struct.Int )
					report_error("Semanticka greska na liniji " + factor_new_expr.getLine() + " za indexiranje je potreban tip int!", null);
						
						
						
					
					
					
				}
				
				
			}else if(fact instanceof FactorExpr) {
				
				
				FactorExpr ff=((FactorExpr)fact);
				Expr ff_expr=ff.getExpr();
				
				if(ff_expr instanceof ExprNoMinus) {
					
					Term tt_expr= ((ExprNoMinus)ff_expr).getTerm();
					Factor fac= tt_expr.getFactor();
					
					if(!( fac instanceof FactorNum) && !( fac instanceof FactorDesignator)){
					report_error("Semanticka greska na liniji " + factor_new_expr.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
		
					}else if(fac instanceof FactorDesignator) {
						
						Designator desii= ((FactorDesignator)fac).getDesignator();
						
						if(desii instanceof DesignatorIDENT) {
							String desi_name=((DesignatorIDENT)desii).getDesigName();
							Obj promenljiva=Tab.find(desi_name);
							
							if(promenljiva==Tab.noObj) {
								report_error("Greska na liniji " + factor_new_expr.getLine() + " : ime " + desi_name
								+ " nije deklarisano! ", null);
							}else if(promenljiva.getType().getKind()!=Struct.Int )
							report_error("Semanticka greska na liniji " + factor_new_expr.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
								
									
							
							
						}
						
						
						
					}
					
					
					
				}
				
				
				
				
				
				
				
			}
			
			
		}
		
		
	}
	//konteksni uslov za array[z+5] gde je z integer a i za sabiranje generalno;
	
	public void visit(ExprListMore exprList) {
		Term t=exprList.getTerm();
		Factor f=t.getFactor();
		
		if(!(f instanceof FactorNum) && !(f instanceof FactorDesignator) && !( f instanceof FactorExpr) )
		report_error("Semanticka greska na liniji " + exprList.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
		else if(f instanceof FactorDesignator) {
			
			Designator desi= ((FactorDesignator)f).getDesignator();
			
			if(desi instanceof DesignatorIDENT) {
				String desi_name=((DesignatorIDENT)desi).getDesigName();
				Obj promenljiva=Tab.find(desi_name);
				
				if(promenljiva==Tab.noObj) {
					report_error("Greska na liniji " + exprList.getLine() + " : ime " + desi_name
					+ " nije deklarisano! ", null);
				}else if(promenljiva.getType().getKind()!=Struct.Int && promenljiva.getType().getKind()!=Struct.Class  && promenljiva.getType().getKind()!=Struct.Array )
				report_error("Semanticka greska na liniji " + exprList.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
				else if(promenljiva.getType().getKind()!=Struct.Class) { //provera tipa kod rekorda
					
					DesignatorList desi_list=((DesignatorIDENT)desi).getDesignatorList();
					
					if(desi_list instanceof DotDesiList) {
						String field_name= ((DotDesiList)desi_list).getDesigName();
						Obj field=Tab.find(field_name);
						if(field.getType().getKind()!=Struct.Int)
					report_error("Semanticka greska na liniji " + exprList.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
						
						
					}
					
				}else if(promenljiva.getType().getKind()==Struct.Array && promenljiva.getType().getElemType().getKind()!=Struct.Int)	
					report_error("Semanticka greska na liniji " + exprList.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
				

				
			}
			
		}else if(f instanceof FactorExpr) {
			FactorExpr ff=((FactorExpr)f);
			Expr ff_expr=ff.getExpr();
			
			if(ff_expr instanceof ExprNoMinus) {
				
				Term tt_expr= ((ExprNoMinus)ff_expr).getTerm();
				Factor fac= tt_expr.getFactor();
				
				if(!( fac instanceof FactorNum) && !( fac instanceof FactorDesignator)){
				report_error("Semanticka greska na liniji " + exprList.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
	
				}else if(fac instanceof FactorDesignator) {
					
					Designator desii= ((FactorDesignator)fac).getDesignator();
					
					if(desii instanceof DesignatorIDENT) {
						String desi_name=((DesignatorIDENT)desii).getDesigName();
						Obj promenljiva=Tab.find(desi_name);
						
						if(promenljiva==Tab.noObj) {
							report_error("Greska na liniji " + exprList.getLine() + " : ime " + desi_name
							+ " nije deklarisano! ", null);
						}else if(promenljiva.getType().getKind()!=Struct.Int && promenljiva.getType().getKind()!=Struct.Class && promenljiva.getType().getKind()!=Struct.Array)
						report_error("Semanticka greska na liniji " + exprList.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
						else if(promenljiva.getType().getKind()==Struct.Array && promenljiva.getType().getElemType().getKind()!=Struct.Int) 
						report_error("Semanticka greska na liniji " + exprList.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);

						else if(promenljiva.getType().getKind()!=Struct.Class) { //provera tipa kod rekorda
							
							DesignatorList desi_list=((DesignatorIDENT)desii).getDesignatorList();
							
							if(desi_list instanceof DotDesiList) {
								String field_name= ((DotDesiList)desi_list).getDesigName();
								Obj field=Tab.find(field_name);
								if(field.getType().getKind()!=Struct.Int)
							report_error("Semanticka greska na liniji " + exprList.getLine() + " nekompatibilni tipovi za aritmeticku operaciju!", null);
								
								
							}
							
						}

								
						
						
					}
					
					
					
				}
				
				
				
			}
			
			
			
			
			
			
			
		}
			
		
		
	}
	//konteksni uslov za deklarisanje arg. funckije;
	
	ArrayList<FormParsListNoBrackets> arg_list = new ArrayList<FormParsListNoBrackets>();
	ArrayList<FormParsListBrackets> arg_list_array = new ArrayList<FormParsListBrackets>();
	
	
	public void visit(FormParsNoBrackets form_pars) {
		Obj promenljiva = Tab.find(form_pars.getIdentName());
		if (promenljiva != Tab.noObj &&  promenljiva.getKind()!= Obj.Fld)
			report_error("Greska! Vec postoji argument sa imenom: " + form_pars.getIdentName(), null);
		else {
			report_info("Naveden argument " + form_pars.getIdentName(), form_pars);
			Obj varNode = Tab.insert(Obj.Var, form_pars.getIdentName(), form_pars.getType().struct); 
			currentMethod.setLevel(currentMethod.getLevel()+1);

			current_num_arg++;
			
			String type=form_pars.getType().getTypeName();
			//report_info("TypeNAME: " + type, null);

			current_arg_types.add(type);
			
			
			for (int i = 0; i < arg_list.size(); i++) {
				
				Obj objekat=Tab.insert(Obj.Var, arg_list.get(i).getIdentName(),  arg_list.get(i).getType().struct);
				currentMethod.setLevel(currentMethod.getLevel()+1);
				//report_info("ARGUMENTTT: "+ arg_list.get(i).getIdentName(), form_pars);
			}
			
			for (int i = 0; i < arg_list_array.size(); i++) {

				Obj objekat=Tab.insert(Obj.Var, arg_list_array.get(i).getIdentName(),  arg_list_array.get(i).getType().struct);
				
				currentMethod.setLevel(currentMethod.getLevel()+1);
			}
			
			
			Tab.chainLocalSymbols(currentMethod);

			arg_list.clear();
			arg_list_array.clear();
			
		}
		
		
		
	}
	public void visit(FormParsBrackets form_pars_array) {
		
		Obj promenljiva = Tab.find(form_pars_array.getIdentName());
		if (promenljiva != Tab.noObj &&  promenljiva.getKind()!= Obj.Fld)
			report_error("Greska! Vec postoji argument sa imenom: " + form_pars_array.getIdentName(), null);
		else {
			report_info("Naveden argument " + form_pars_array.getIdentName(), form_pars_array);
			Obj varNode = Tab.insert(Obj.Var, form_pars_array.getIdentName(), form_pars_array.getType().struct); 
			currentMethod.setLevel(currentMethod.getLevel()+1);

			current_num_arg++;
			
			for (int i = 0; i < arg_list_array.size(); i++) {

				Obj objekat=Tab.insert(Obj.Var, arg_list_array.get(i).getIdentName(),  new Struct(Struct.Array,arg_list_array.get(i).getType().struct));
				currentMethod.setLevel(currentMethod.getLevel()+1);

			}
			
			for (int i = 0; i < arg_list.size(); i++) {
				
				Obj objekat=Tab.insert(Obj.Var, arg_list.get(i).getIdentName(),  new Struct(Struct.Array,arg_list.get(i).getType().struct));
				currentMethod.setLevel(currentMethod.getLevel()+1);
			}
			
			Tab.chainLocalSymbols(currentMethod);

			arg_list_array.clear();
			arg_list.clear();
			
		}
		
		
		
		
	
	}
	public void visit(FormParsListNoBrackets list_pars) {
		
		Obj promenljiva = Tab.find(list_pars.getIdentName());
		if (promenljiva != Tab.noObj &&  promenljiva.getKind()!= Obj.Fld)
			report_error("Greska! Vec postoji argument sa imenom: " + list_pars.getIdentName(), null);
		else {
			report_info("Naveden argument " + list_pars.getIdentName(), list_pars);
			// Obj varNode = Tab.insert(Obj.Var, varDecl.getVarName(),
			// current_var_decl_type);
			arg_list.add(list_pars);
			current_num_arg++;
			String type=list_pars.getType().getTypeName();
			//report_info("TypeNAME: " + type, null);

			current_arg_types.add(type);
		}
		
		
	}
	public void visit(FormParsListBrackets list_parsa_array) {
		Obj promenljiva = Tab.find(list_parsa_array.getIdentName());
		if (promenljiva != Tab.noObj)
			report_error("Greska! Vec postoji argument sa imenom: " + list_parsa_array.getIdentName(), null);
		else {
			report_info("Naveden argument " + list_parsa_array.getIdentName(), list_parsa_array);
			// Obj varNode = Tab.insert(Obj.Var, varDecl.getVarName(),
			// current_var_decl_type);
			arg_list_array.add(list_parsa_array);
			current_num_arg++;

		}
		
		
		
		
	}
	//konteksni uslov za stvarne argumente funkcije
	
	public void visit(ActPars actPars) {
//		user_arg_num++;
//
//		//report_info("Usao u ACTPARS "+ "broj: "+ user_arg_num, actPars);
//		Expr expr=actPars.getExpr();
//		Term term=null;
//		if(expr instanceof ExprNoMinus) 
//			term= ((ExprNoMinus)expr).getTerm();
//		else
//			term= ((ExprMinus)expr).getTerm();
//		
//		Factor fac=term.getFactor();
//		
//		if(fac instanceof FactorNum) {
//			user_arg_types.add("int");
//
//		}else if(fac instanceof FactorChar ) {
//			user_arg_types.add("char");
//
//		}else if(fac instanceof FactorBool) {
//			user_arg_types.add("bool");
//
//		}else if(fac instanceof FactorDesignator) {
//			
//
//			
//			Designator desi=((FactorDesignator)fac).getDesignator();
//
//			String desi_name=((DesignatorIDENT)desi).getDesigName();
//
//			
//			
//			Obj desi_obj= Tab.find(desi_name);
//			
//			
//			
//			
//			if(desi_obj==Tab.noObj)
//			report_error("Greska! Ne postoji promenljiva:" + desi_name, null);
//
//			if(desi_obj.getType().getKind()==Struct.Int)
//				user_arg_types.add("int");
//			else if(desi_obj.getType().getKind()==Struct.Char)
//				user_arg_types.add("char");
//			else if(desi_obj.getType().getKind()==Struct.Bool)
//				user_arg_types.add("bool");
//			else if(desi_obj.getKind() == Obj.Meth) {
//				
//				
//				
//			}
//			
//			
//		}else if( fac instanceof FactorDesignatorPars){ // ako je argument poziv funkcije
//			
//			
//			
//			//user_arg_num=0;
//			Designator desi=((FactorDesignatorPars)fac).getDesignator();
//			
//			
//			
//			String desi_name=((DesignatorIDENT)desi).getDesigName();
//			
//		//	System.out.println("Pronadjen poziv funkcije: "+ desi_name+" na liniji: "+actPars.getLine());
//			Obj desi_obj=Tab.find(desi_name);
//			if(desi_obj.getKind() != Obj.Meth)
//			report_error("Greska! Ne postoji funckija:" + desi_name, null);
//			
//		//	report_info("DESAVA SE POZIV FUNCKIJE:" + desi_name, actPars);
//			user_arg_num = user_arg_num - get_func_arg(desi_name);
//			
//			if(desi_obj.getType().getKind()==Struct.Int)
//				user_arg_types.add("int");
//			else if(desi_obj.getType().getKind()==Struct.Char)
//				user_arg_types.add("char");
//			else if(desi_obj.getType().getKind()==Struct.Bool)
//				user_arg_types.add("bool");
//
//			
//			
//		}
//		
	}
	
	
	
	public void visit(ActParsListMore actParsMore) {
//		user_arg_num++;
//		
//		//report_info("Usao u ACTPARSLIST "+ "broj: "+ user_arg_num, actParsMore);
//
//		
//		Expr expr=actParsMore.getExpr();
//		Term term=null;
//		if(expr instanceof ExprNoMinus) 
//			term= ((ExprNoMinus)expr).getTerm();
//		else
//			term= ((ExprMinus)expr).getTerm();
//		
//		Factor fac=term.getFactor();
//		
//		if(fac instanceof FactorNum) {
//			user_arg_types.add("int");
//			
//		}else if(fac instanceof FactorChar ) {
//			user_arg_types.add("char");
//
//		}else if(fac instanceof FactorBool) {
//			user_arg_types.add("bool");
//
//		}else if(fac instanceof FactorDesignator) { //ako je argument promenljiva
//			
//			
//			
//			Designator desi=((FactorDesignator)fac).getDesignator();
//			
//			String desi_name=((DesignatorIDENT)desi).getDesigName();
//			Obj desi_obj= Tab.find(desi_name);
//			
//			if(desi_obj==Tab.noObj)
//			report_error("Greska! Ne postoji promenljiva:" + desi_name, null);
//
//			if(desi_obj.getType().getKind()==Struct.Int)
//				user_arg_types.add("int");
//			else if(desi_obj.getType().getKind()==Struct.Char)
//				user_arg_types.add("char");
//			else if(desi_obj.getType().getKind()==Struct.Bool)
//				user_arg_types.add("bool");
//
//			
//		}else if( fac instanceof FactorDesignatorPars){ // ako je argument poziv funkcije
//			
//			//user_arg_num=0;
//			
//			Designator desi=((FactorDesignatorPars)fac).getDesignator();
//
//			String desi_name=((DesignatorIDENT)desi).getDesigName();
//
//			Obj desi_obj=Tab.find(desi_name);
//			if(desi_obj.getKind() != Obj.Meth)
//			report_error("Greska! Ne postoji funckija:" + desi_name, null);
//			
//			if(desi_obj.getType().getKind()==Struct.Int)
//				user_arg_types.add("int");
//			else if(desi_obj.getType().getKind()==Struct.Char)
//				user_arg_types.add("char");
//			else if(desi_obj.getType().getKind()==Struct.Bool)
//				user_arg_types.add("bool");
//
//		}
//		
		
		
		
	}
	
	

	
	
	public void visit(FactorDesignatorPars fact_desig_pars) {
		Designator desi=fact_desig_pars.getDesignator();
		String func_name=((DesignatorIDENT)desi).getDesigName();
		
		if(desi.obj.getKind()==Obj.Meth) {
		
		report_info("Pronadjen poziv funkcije " + func_name + " na liniji " + fact_desig_pars.getLine(), null);
	
		ActPars act_pars=fact_desig_pars.getActPars();
		

		if(desi.obj.getLevel() ==1) { // 1 argument samo

			if((act_pars.getActParsList()) instanceof ActParsListNo){

				//provera jos tipa
			
					
				Expr expr=act_pars.getExpr();				
				Collection<Obj> coll=desi.obj.getLocalSymbols();
				ArrayList<Obj> col_list=new ArrayList<Obj>(coll);
				if(col_list.get(0).getType()==get_expr_type(expr)) { // sve ok
					//report_info("SVE OKEJ", fact_desig_pars);
					
				}else if(func_name.equals("len")) {
					//specijalno za len
				}
				else {
					//greska pogresan tip arg
					//System.out.println("TIP: "+ get_expr_type(expr).getKind());
					report_error("Pogresan tip argumenta u funkciji: "+func_name  , fact_desig_pars);
	
					
				}
					
					
				
				
				
				
				
			}else {

				//ima vise arg nego sto treba!
				report_error("Pogresan broj argumenta u funkciji: "+func_name  , fact_desig_pars);

			}
			
			
			
			
		}else {

			if((act_pars.getActParsList()) instanceof ActParsListNo){
				
				//Greska nema dovoljno arg
				report_error("Pogresan broj argumenta u funkciji: "+func_name  , fact_desig_pars);

			}else {
				
				//if(desi.obj.getLevel() == 1) {
					Expr expr=act_pars.getExpr();				
				Collection<Obj> coll=desi.obj.getLocalSymbols();
				ArrayList<Obj> col_list=new ArrayList<Obj>(coll);
				if(col_list.get(0).getType()==get_expr_type(expr)) { // sve ok
					//report_info("SVE OKEJ", fact_desig_pars);
					
				}
				else {
					//greska pogresan tip arg
					report_error("Pogresan tip argumenta u funkciji: "+func_name  , fact_desig_pars);
	
				}
					
				int brojac=1;
				ActParsList trenutni= act_pars.getActParsList();
				
//				while(brojac < desi.obj.getLevel() || !(trenutni instanceof ActParsListNo))
//				{
//					if(col_list.get(brojac).getType()==get_expr_type(((ActParsListMore)trenutni).getExpr()))
//					{
//						//report_info("SVE OKEJ", fact_desig_pars);
//					}else
//						report_error("Pogresan tip argumenta u funkciji: "+func_name  , fact_desig_pars);
//	
//					
//					brojac++;
//					trenutni= ((ActParsListMore)trenutni).getActParsList();
//				}
				for(brojac=1 ; brojac < desi.obj.getLevel() ; brojac++) {
					
					if(trenutni instanceof ActParsListNo)
						break;
					if(col_list.get(brojac).getType()==get_expr_type(((ActParsListMore)trenutni).getExpr())) {
						//sve je okej
					}else
						report_error("Pogresan tip argumenta u funkciji: "+desi.obj.getName()  , fact_desig_pars);
					trenutni= ((ActParsListMore)trenutni).getActParsList();

				}
				
				if(brojac != desi.obj.getLevel() || !(trenutni instanceof ActParsListNo))
					report_error("Pogresan broj argumenta u funkciji: "+func_name  , fact_desig_pars);
	
					
				//}
				
				
				
				
			}
			
			
			
		}
		
		
		
		}else {
			report_error("Greska na liniji " + fact_desig_pars.getLine() + " : ime " + func_name + " nije funkcija!",
					null);
		}
		
		
		
//		if((act_pars.getActParsList()) instanceof ActParsListNo) {
//			//samo 1 arg
//			if(desi.obj.getLevel() == 1) {
//				Expr expr=act_pars.getExpr();				
//			Collection<Obj> coll=desi.obj.getLocalSymbols();
//			ArrayList<Obj> col_list=new ArrayList<Obj>(coll);
//			if(col_list.get(0).getType()==get_expr_type(expr)) { // sve ok
//				report_info("SVE OKEJ", fact_desig_pars);
//				
//			}
//			else {
//				//greska pogresan tip arg
//				report_error("POGRESAN TIP ARGUMENTA!", fact_desig_pars);
//
//				
//			}
//				
//				
//			}
//			
//			
//		}else { // vise argumenata
//			
//			
//			if(desi.obj.getLevel() == 1) {
//				Expr expr=act_pars.getExpr();				
//			Collection<Obj> coll=desi.obj.getLocalSymbols();
//			ArrayList<Obj> col_list=new ArrayList<Obj>(coll);
//			if(col_list.get(0).getType()==get_expr_type(expr)) { // sve ok
//				report_info("SVE OKEJ", fact_desig_pars);
//				
//			}
//			else {
//				//greska pogresan tip arg
//				report_error("POGRESAN TIP ARGUMENTA!", fact_desig_pars);
//
//			}
//				
//			int brojac=1;
//			ActParsList trenutni= act_pars.getActParsList();
//			
//			while(brojac < desi.obj.getLevel() || !(trenutni instanceof ActParsListNo))
//			{
//				if(col_list.get(brojac).getType()==get_expr_type(((ActParsListMore)trenutni).getExpr()))
//				{
//					report_info("SVE OKEJ", fact_desig_pars);
//				}else
//					report_error("GRESKA! POGRESAN TIP!", fact_desig_pars);
//
//				
//				brojac++;
//				trenutni= ((ActParsListMore)trenutni).getActParsList();
//			}
//			
//			if(brojac != desi.obj.getLevel() || !(trenutni instanceof ActParsListNo))
//				report_error("GRESKA! POGRESAN BROJ ARGUMENATA!", fact_desig_pars);
//
//				
//			}
//			
//
//			
//		}
//			
//			
//			
//		}else {
//			report_error("GRESKA! NIJE FUNKCIJA", fact_desig_pars);
//		}
//		
//		
		
		
	}
	public void visit(FactorDesignatorNoPars fact_desig_no_pars) {
		
	}
	
	
	public boolean passed() {
		return !errorDetected;
	}

}

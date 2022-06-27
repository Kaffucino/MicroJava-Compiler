// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class ExprMinus extends Expr {

    private ExprMinusTerm ExprMinusTerm;
    private Term Term;
    private ExprList ExprList;

    public ExprMinus (ExprMinusTerm ExprMinusTerm, Term Term, ExprList ExprList) {
        this.ExprMinusTerm=ExprMinusTerm;
        if(ExprMinusTerm!=null) ExprMinusTerm.setParent(this);
        this.Term=Term;
        if(Term!=null) Term.setParent(this);
        this.ExprList=ExprList;
        if(ExprList!=null) ExprList.setParent(this);
    }

    public ExprMinusTerm getExprMinusTerm() {
        return ExprMinusTerm;
    }

    public void setExprMinusTerm(ExprMinusTerm ExprMinusTerm) {
        this.ExprMinusTerm=ExprMinusTerm;
    }

    public Term getTerm() {
        return Term;
    }

    public void setTerm(Term Term) {
        this.Term=Term;
    }

    public ExprList getExprList() {
        return ExprList;
    }

    public void setExprList(ExprList ExprList) {
        this.ExprList=ExprList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprMinusTerm!=null) ExprMinusTerm.accept(visitor);
        if(Term!=null) Term.accept(visitor);
        if(ExprList!=null) ExprList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprMinusTerm!=null) ExprMinusTerm.traverseTopDown(visitor);
        if(Term!=null) Term.traverseTopDown(visitor);
        if(ExprList!=null) ExprList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprMinusTerm!=null) ExprMinusTerm.traverseBottomUp(visitor);
        if(Term!=null) Term.traverseBottomUp(visitor);
        if(ExprList!=null) ExprList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ExprMinus(\n");

        if(ExprMinusTerm!=null)
            buffer.append(ExprMinusTerm.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Term!=null)
            buffer.append(Term.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprList!=null)
            buffer.append(ExprList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ExprMinus]");
        return buffer.toString();
    }
}

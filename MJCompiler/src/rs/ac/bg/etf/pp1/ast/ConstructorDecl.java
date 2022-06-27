// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class ConstructorDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private String I1;
    private MethodDeclVariables MethodDeclVariables;
    private StatementsList StatementsList;

    public ConstructorDecl (String I1, MethodDeclVariables MethodDeclVariables, StatementsList StatementsList) {
        this.I1=I1;
        this.MethodDeclVariables=MethodDeclVariables;
        if(MethodDeclVariables!=null) MethodDeclVariables.setParent(this);
        this.StatementsList=StatementsList;
        if(StatementsList!=null) StatementsList.setParent(this);
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String I1) {
        this.I1=I1;
    }

    public MethodDeclVariables getMethodDeclVariables() {
        return MethodDeclVariables;
    }

    public void setMethodDeclVariables(MethodDeclVariables MethodDeclVariables) {
        this.MethodDeclVariables=MethodDeclVariables;
    }

    public StatementsList getStatementsList() {
        return StatementsList;
    }

    public void setStatementsList(StatementsList StatementsList) {
        this.StatementsList=StatementsList;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodDeclVariables!=null) MethodDeclVariables.accept(visitor);
        if(StatementsList!=null) StatementsList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.traverseTopDown(visitor);
        if(StatementsList!=null) StatementsList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodDeclVariables!=null) MethodDeclVariables.traverseBottomUp(visitor);
        if(StatementsList!=null) StatementsList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstructorDecl(\n");

        buffer.append(" "+tab+I1);
        buffer.append("\n");

        if(MethodDeclVariables!=null)
            buffer.append(MethodDeclVariables.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementsList!=null)
            buffer.append(StatementsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstructorDecl]");
        return buffer.toString();
    }
}

// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclaration extends MethodDecl {

    private MethodTypeName MethodTypeName;
    private MethodDeclPars MethodDeclPars;
    private MethodDeclVariables MethodDeclVariables;
    private StatementsList StatementsList;

    public MethodDeclaration (MethodTypeName MethodTypeName, MethodDeclPars MethodDeclPars, MethodDeclVariables MethodDeclVariables, StatementsList StatementsList) {
        this.MethodTypeName=MethodTypeName;
        if(MethodTypeName!=null) MethodTypeName.setParent(this);
        this.MethodDeclPars=MethodDeclPars;
        if(MethodDeclPars!=null) MethodDeclPars.setParent(this);
        this.MethodDeclVariables=MethodDeclVariables;
        if(MethodDeclVariables!=null) MethodDeclVariables.setParent(this);
        this.StatementsList=StatementsList;
        if(StatementsList!=null) StatementsList.setParent(this);
    }

    public MethodTypeName getMethodTypeName() {
        return MethodTypeName;
    }

    public void setMethodTypeName(MethodTypeName MethodTypeName) {
        this.MethodTypeName=MethodTypeName;
    }

    public MethodDeclPars getMethodDeclPars() {
        return MethodDeclPars;
    }

    public void setMethodDeclPars(MethodDeclPars MethodDeclPars) {
        this.MethodDeclPars=MethodDeclPars;
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

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodTypeName!=null) MethodTypeName.accept(visitor);
        if(MethodDeclPars!=null) MethodDeclPars.accept(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.accept(visitor);
        if(StatementsList!=null) StatementsList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodTypeName!=null) MethodTypeName.traverseTopDown(visitor);
        if(MethodDeclPars!=null) MethodDeclPars.traverseTopDown(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.traverseTopDown(visitor);
        if(StatementsList!=null) StatementsList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodTypeName!=null) MethodTypeName.traverseBottomUp(visitor);
        if(MethodDeclPars!=null) MethodDeclPars.traverseBottomUp(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.traverseBottomUp(visitor);
        if(StatementsList!=null) StatementsList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDeclaration(\n");

        if(MethodTypeName!=null)
            buffer.append(MethodTypeName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodDeclPars!=null)
            buffer.append(MethodDeclPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
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
        buffer.append(") [MethodDeclaration]");
        return buffer.toString();
    }
}

// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclVariablesYes extends MethodDeclVariables {

    private VarDeclList VarDeclList;
    private MethodDeclVariables MethodDeclVariables;

    public MethodDeclVariablesYes (VarDeclList VarDeclList, MethodDeclVariables MethodDeclVariables) {
        this.VarDeclList=VarDeclList;
        if(VarDeclList!=null) VarDeclList.setParent(this);
        this.MethodDeclVariables=MethodDeclVariables;
        if(MethodDeclVariables!=null) MethodDeclVariables.setParent(this);
    }

    public VarDeclList getVarDeclList() {
        return VarDeclList;
    }

    public void setVarDeclList(VarDeclList VarDeclList) {
        this.VarDeclList=VarDeclList;
    }

    public MethodDeclVariables getMethodDeclVariables() {
        return MethodDeclVariables;
    }

    public void setMethodDeclVariables(MethodDeclVariables MethodDeclVariables) {
        this.MethodDeclVariables=MethodDeclVariables;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclList!=null) VarDeclList.accept(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclList!=null) VarDeclList.traverseTopDown(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclList!=null) VarDeclList.traverseBottomUp(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDeclVariablesYes(\n");

        if(VarDeclList!=null)
            buffer.append(VarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodDeclVariables!=null)
            buffer.append(MethodDeclVariables.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDeclVariablesYes]");
        return buffer.toString();
    }
}

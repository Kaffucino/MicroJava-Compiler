// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclMore extends ConstDecl {

    private String constName;
    private AnyConst AnyConst;
    private ConstDecl ConstDecl;

    public ConstDeclMore (String constName, AnyConst AnyConst, ConstDecl ConstDecl) {
        this.constName=constName;
        this.AnyConst=AnyConst;
        if(AnyConst!=null) AnyConst.setParent(this);
        this.ConstDecl=ConstDecl;
        if(ConstDecl!=null) ConstDecl.setParent(this);
    }

    public String getConstName() {
        return constName;
    }

    public void setConstName(String constName) {
        this.constName=constName;
    }

    public AnyConst getAnyConst() {
        return AnyConst;
    }

    public void setAnyConst(AnyConst AnyConst) {
        this.AnyConst=AnyConst;
    }

    public ConstDecl getConstDecl() {
        return ConstDecl;
    }

    public void setConstDecl(ConstDecl ConstDecl) {
        this.ConstDecl=ConstDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AnyConst!=null) AnyConst.accept(visitor);
        if(ConstDecl!=null) ConstDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AnyConst!=null) AnyConst.traverseTopDown(visitor);
        if(ConstDecl!=null) ConstDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AnyConst!=null) AnyConst.traverseBottomUp(visitor);
        if(ConstDecl!=null) ConstDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDeclMore(\n");

        buffer.append(" "+tab+constName);
        buffer.append("\n");

        if(AnyConst!=null)
            buffer.append(AnyConst.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstDecl!=null)
            buffer.append(ConstDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclMore]");
        return buffer.toString();
    }
}

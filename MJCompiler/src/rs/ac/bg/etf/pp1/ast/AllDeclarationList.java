// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class AllDeclarationList extends AllDeclList {

    private AllDecl AllDecl;
    private AllDeclList AllDeclList;

    public AllDeclarationList (AllDecl AllDecl, AllDeclList AllDeclList) {
        this.AllDecl=AllDecl;
        if(AllDecl!=null) AllDecl.setParent(this);
        this.AllDeclList=AllDeclList;
        if(AllDeclList!=null) AllDeclList.setParent(this);
    }

    public AllDecl getAllDecl() {
        return AllDecl;
    }

    public void setAllDecl(AllDecl AllDecl) {
        this.AllDecl=AllDecl;
    }

    public AllDeclList getAllDeclList() {
        return AllDeclList;
    }

    public void setAllDeclList(AllDeclList AllDeclList) {
        this.AllDeclList=AllDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AllDecl!=null) AllDecl.accept(visitor);
        if(AllDeclList!=null) AllDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AllDecl!=null) AllDecl.traverseTopDown(visitor);
        if(AllDeclList!=null) AllDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AllDecl!=null) AllDecl.traverseBottomUp(visitor);
        if(AllDeclList!=null) AllDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AllDeclarationList(\n");

        if(AllDecl!=null)
            buffer.append(AllDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AllDeclList!=null)
            buffer.append(AllDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AllDeclarationList]");
        return buffer.toString();
    }
}

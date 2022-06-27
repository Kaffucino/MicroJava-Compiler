// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class RecordDecls extends RecordDecl {

    private RecordName RecordName;
    private MoreVarDecl MoreVarDecl;

    public RecordDecls (RecordName RecordName, MoreVarDecl MoreVarDecl) {
        this.RecordName=RecordName;
        if(RecordName!=null) RecordName.setParent(this);
        this.MoreVarDecl=MoreVarDecl;
        if(MoreVarDecl!=null) MoreVarDecl.setParent(this);
    }

    public RecordName getRecordName() {
        return RecordName;
    }

    public void setRecordName(RecordName RecordName) {
        this.RecordName=RecordName;
    }

    public MoreVarDecl getMoreVarDecl() {
        return MoreVarDecl;
    }

    public void setMoreVarDecl(MoreVarDecl MoreVarDecl) {
        this.MoreVarDecl=MoreVarDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(RecordName!=null) RecordName.accept(visitor);
        if(MoreVarDecl!=null) MoreVarDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(RecordName!=null) RecordName.traverseTopDown(visitor);
        if(MoreVarDecl!=null) MoreVarDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(RecordName!=null) RecordName.traverseBottomUp(visitor);
        if(MoreVarDecl!=null) MoreVarDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("RecordDecls(\n");

        if(RecordName!=null)
            buffer.append(RecordName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MoreVarDecl!=null)
            buffer.append(MoreVarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [RecordDecls]");
        return buffer.toString();
    }
}

// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class ElseNo extends Else {

    private ElseNoNo ElseNoNo;

    public ElseNo (ElseNoNo ElseNoNo) {
        this.ElseNoNo=ElseNoNo;
        if(ElseNoNo!=null) ElseNoNo.setParent(this);
    }

    public ElseNoNo getElseNoNo() {
        return ElseNoNo;
    }

    public void setElseNoNo(ElseNoNo ElseNoNo) {
        this.ElseNoNo=ElseNoNo;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ElseNoNo!=null) ElseNoNo.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ElseNoNo!=null) ElseNoNo.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ElseNoNo!=null) ElseNoNo.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ElseNo(\n");

        if(ElseNoNo!=null)
            buffer.append(ElseNoNo.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ElseNo]");
        return buffer.toString();
    }
}

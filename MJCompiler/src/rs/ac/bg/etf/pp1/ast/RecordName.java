// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class RecordName implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Obj obj = null;

    private String RName;

    public RecordName (String RName) {
        this.RName=RName;
    }

    public String getRName() {
        return RName;
    }

    public void setRName(String RName) {
        this.RName=RName;
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
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("RecordName(\n");

        buffer.append(" "+tab+RName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [RecordName]");
        return buffer.toString();
    }
}
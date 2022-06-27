// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class SingleStatementDesignatorPRINTGOTO extends SingleStatement {

    private Goto Goto;
    private Label Label;

    public SingleStatementDesignatorPRINTGOTO (Goto Goto, Label Label) {
        this.Goto=Goto;
        if(Goto!=null) Goto.setParent(this);
        this.Label=Label;
        if(Label!=null) Label.setParent(this);
    }

    public Goto getGoto() {
        return Goto;
    }

    public void setGoto(Goto Goto) {
        this.Goto=Goto;
    }

    public Label getLabel() {
        return Label;
    }

    public void setLabel(Label Label) {
        this.Label=Label;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Goto!=null) Goto.accept(visitor);
        if(Label!=null) Label.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Goto!=null) Goto.traverseTopDown(visitor);
        if(Label!=null) Label.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Goto!=null) Goto.traverseBottomUp(visitor);
        if(Label!=null) Label.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleStatementDesignatorPRINTGOTO(\n");

        if(Goto!=null)
            buffer.append(Goto.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Label!=null)
            buffer.append(Label.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleStatementDesignatorPRINTGOTO]");
        return buffer.toString();
    }
}

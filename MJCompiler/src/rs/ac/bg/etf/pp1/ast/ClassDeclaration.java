// generated with ast extension for cup
// version 0.8
// 10/1/2022 23:6:55


package rs.ac.bg.etf.pp1.ast;

public class ClassDeclaration extends ClassDecl {

    private String ClassName;
    private ClassDeclExtends ClassDeclExtends;
    private MethodDeclVariables MethodDeclVariables;
    private ClassDeclBody ClassDeclBody;

    public ClassDeclaration (String ClassName, ClassDeclExtends ClassDeclExtends, MethodDeclVariables MethodDeclVariables, ClassDeclBody ClassDeclBody) {
        this.ClassName=ClassName;
        this.ClassDeclExtends=ClassDeclExtends;
        if(ClassDeclExtends!=null) ClassDeclExtends.setParent(this);
        this.MethodDeclVariables=MethodDeclVariables;
        if(MethodDeclVariables!=null) MethodDeclVariables.setParent(this);
        this.ClassDeclBody=ClassDeclBody;
        if(ClassDeclBody!=null) ClassDeclBody.setParent(this);
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName=ClassName;
    }

    public ClassDeclExtends getClassDeclExtends() {
        return ClassDeclExtends;
    }

    public void setClassDeclExtends(ClassDeclExtends ClassDeclExtends) {
        this.ClassDeclExtends=ClassDeclExtends;
    }

    public MethodDeclVariables getMethodDeclVariables() {
        return MethodDeclVariables;
    }

    public void setMethodDeclVariables(MethodDeclVariables MethodDeclVariables) {
        this.MethodDeclVariables=MethodDeclVariables;
    }

    public ClassDeclBody getClassDeclBody() {
        return ClassDeclBody;
    }

    public void setClassDeclBody(ClassDeclBody ClassDeclBody) {
        this.ClassDeclBody=ClassDeclBody;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ClassDeclExtends!=null) ClassDeclExtends.accept(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.accept(visitor);
        if(ClassDeclBody!=null) ClassDeclBody.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassDeclExtends!=null) ClassDeclExtends.traverseTopDown(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.traverseTopDown(visitor);
        if(ClassDeclBody!=null) ClassDeclBody.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassDeclExtends!=null) ClassDeclExtends.traverseBottomUp(visitor);
        if(MethodDeclVariables!=null) MethodDeclVariables.traverseBottomUp(visitor);
        if(ClassDeclBody!=null) ClassDeclBody.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDeclaration(\n");

        buffer.append(" "+tab+ClassName);
        buffer.append("\n");

        if(ClassDeclExtends!=null)
            buffer.append(ClassDeclExtends.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodDeclVariables!=null)
            buffer.append(MethodDeclVariables.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassDeclBody!=null)
            buffer.append(ClassDeclBody.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassDeclaration]");
        return buffer.toString();
    }
}

import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a C-- program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Children
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of children, or
// internal nodes with a fixed number of children:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of children:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

class StructDeclTable
{
    public static SymTable structDeclTable = new SymTable();
}





abstract class ASTnode {
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void addIndentation(PrintWriter p, int indent) {
        for (int k = 0; k < indent; k++) p.print(" ");
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

//done
class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    public void nameAnalyzer(SymTable symTable)
    {
        
        myDeclList.nameAnalyzer(symTable);
    }

    private DeclListNode myDeclList;
}

//done
class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    public void nameAnalyzer(SymTable symTable)
    {
        for(int i = 0; i < myDecls.size(); i++)
        {
            myDecls.get(i).nameAnalyzer(symTable);
        }
    }

    public List<DeclNode> getDecList()
    {
        return myDecls;
    }

    private List<DeclNode> myDecls;
}

//done
class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    public void nameAnalyzer(SymTable symTable)
    {
        for(int i = 0; i < myFormals.size(); i++)
        {
            myFormals.get(i).nameAnalyzer(symTable);
        }
    }

    public List<FormalDeclNode> getMyFormals()
    {
        return myFormals;
    }



    private List<FormalDeclNode> myFormals;
}


//done
class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    private DeclListNode myDeclList;
    private StmtListNode myStmtList;


    public void nameAnalyzer(SymTable symTable)
    {
        myDeclList.nameAnalyzer(symTable);
        myStmtList.nameAnalyzer(symTable);
    }


}


//done
class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }


    public void nameAnalyzer(SymTable symTable)
    {
        for(int i = 0; i < myStmts.size(); i++)
        {
            myStmts.get(i).nameAnalyzer(symTable);
        }
    }

    private List<StmtNode> myStmts;
}


//done
class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    public void nameAnalyzer(SymTable symTable)
    {
        for(int i = 0; i < myExps.size(); i++)
        {
            myExps.get(i).nameAnalyzer(symTable);
        }
    }

    private List<ExpNode> myExps;
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    public void nameAnalyzer(SymTable symTable)
    {
        //we dont ref the node
    }
}
//need to take a look back later
class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        boolean error = false;
        myId.setIsDecl(true);
        if(myType instanceof VoidNode)  //case: void variable
        {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
            error = true;
        }

        try{

            if(mySize != NOT_STRUCT)    //its a struct
            {
                //check if the struct name exists
                String structName = ((StructNode)myType).getId();
                
                TSym structDefSym = StructDeclTable.structDeclTable.lookupGlobal(structName);//proto
                if(structDefSym == null)  //either struct name DNE or its not a struct
                {
                    ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Invalid name of struct type");
                    error = true;
                }
                
                ((StructNode)myType).getIdNode().setIsDecl(true);
    
            }

            if(symTable.lookupLocal(myId.getStrVal()) != null) //case: multiple declaration
            {
                ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
                error = true;
            }

            if(error) return;
            if(mySize == NOT_STRUCT)symTable.addDecl(myId.getStrVal(),new TSym(myType));
            else{
                String structName = ((StructNode)myType).getId();
               // System.out.println("Struct Name :"+structName + " "+ myId.getLineNum() + " "+ myId.getCharNum() );
                symTable.addDecl(myId.getStrVal(), new StructSym(StructDeclTable.structDeclTable.lookupGlobal(structName), structName));
            }

        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }

        
        


    }


    public TypeNode getType()
    {
        return myType;
    }

    public boolean isStruct()
    {
        return mySize != NOT_STRUCT;
    }

    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    //done

    public void nameAnalyzer(SymTable symTable)
    {
        myId.setIsDecl(true);
        boolean error = false;


        try{

            if(symTable.lookupLocal(myId.getStrVal()) != null) //case: multiple declaration
            {
                ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
                error = true;
            }

        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }
        
        

        try{
            FnSym functionSym = new FnSym();
            functionSym.setReturnType(myType);
            List<FormalDeclNode> formals = myFormalsList.getMyFormals();
            List<TypeNode> formalsType = new ArrayList<>();
            
            for(int i = 0; i< formals.size(); i++)
            {
                formalsType.add(formals.get(i).getType());
            }
            functionSym.setParamTypeList(formalsType);

           if(!error) symTable.addDecl(myId.getStrVal(),functionSym);
           symTable.addScope();

           myFormalsList.nameAnalyzer(symTable);
           myBody.nameAnalyzer(symTable);
           symTable.removeScope();
        }catch(Exception e){
            System.err.println("Symbol Entry Insertion error.");
        }


        





    }

    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}


//done
class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }


    public void nameAnalyzer(SymTable symTable)
    {
        myId.setIsDecl(true);
        boolean error = false;
        if(myType instanceof VoidNode)  //case: void variable
        {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
            error = true;
        }

        try{

            if(symTable.lookupLocal(myId.getStrVal()) != null) //case: multiple declaration
            {
                ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
                error = true;
            }

        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }
    //for here, we assume if there's an error for variables, we just stop.
        if(error) return;
        
        

        try{
            symTable.addDecl(myId.getStrVal(),new TSym(myType));
        }catch(Exception e){
            System.err.println("Symbol Entry Insertion error.");
        }



    }


    public TypeNode getType()
    {
        return myType;
    }

    private TypeNode myType;
    private IdNode myId;
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("struct ");
        myId.unparse(p, 0);
        p.println("{");
        myDeclList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("};\n");

    }

//done
    public void nameAnalyzer(SymTable symTable)
    {
        myId.setIsDecl(true);
        boolean error = false;


        try{

            if(symTable.lookupLocal(myId.getStrVal()) != null) //case: multiple declaration
            {
                ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
                error = true;
            }


            if(error) return;
        
            SymTable tmp = new SymTable();
    
            
    
            StructDefSym sym = new StructDefSym(tmp);

            symTable.addDecl(myId.getStrVal(),sym);
            StructDeclTable.structDeclTable.addDecl(myId.getStrVal(), sym);
            myDeclList.nameAnalyzer(tmp);

        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }
    //for here, we assume if there's an error for variables, we just stop.



        


    }

    private IdNode myId;
    private DeclListNode myDeclList;
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {

}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }

    public String getId()
    {
        return myId.getStrVal();
    }

    public IdNode getIdNode()
    {
        return myId;
    }

    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
    public void nameAnalyzer(SymTable symTable)
    {

    }
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myAssign.nameAnalyzer(symTable);
    }

    private AssignNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
    }



    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
    }

    private ExpNode myExp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
    }

    // 1 child (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
    }

    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
        symTable.addScope();
        myDeclList.nameAnalyzer(symTable);
        myStmtList.nameAnalyzer(symTable);
        try{
            symTable.removeScope();
        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
        addIndentation(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
        symTable.addScope();
        myThenDeclList.nameAnalyzer(symTable);
        myThenStmtList.nameAnalyzer(symTable);
        try{
            symTable.removeScope();
        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }

        symTable.addScope();
        myElseDeclList.nameAnalyzer(symTable);
        myElseStmtList.nameAnalyzer(symTable);
        try{
            symTable.removeScope();
        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }
    }

    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
        symTable.addScope();
        myDeclList.nameAnalyzer(symTable);
        myStmtList.nameAnalyzer(symTable);
        try{
            symTable.removeScope();
        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
	    addIndentation(p, indent);
        p.print("repeat (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
        symTable.addScope();
        myDeclList.nameAnalyzer(symTable);
        myStmtList.nameAnalyzer(symTable);
        try{
            symTable.removeScope();
        }catch(Exception e){
            System.err.println("The Symbol Table is Empty");
        }
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myCall.nameAnalyzer(symTable);
    }

    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        if(myExp != null) myExp.nameAnalyzer(symTable);
    }

    private ExpNode myExp; // possibly null
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {

    public void nameAnalyzer(SymTable symTable)
    {
        
    }
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
        isDecl = false;
    }

    public void unparse(PrintWriter p, int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(myStrVal);
        if(!isDecl) 
        {
           // if(mySym == null) System.out.println(myLineNum+" "+ myCharNum+"   :"+myStrVal);
            sb.append("(");
            if(mySym instanceof FnSym) sb.append(((FnSym)mySym).toString());
            else if(mySym instanceof StructSym) sb.append(((StructSym)mySym).toString());
            else sb.append(mySym.toString());
            sb.append(")");
        }
        p.print(sb.toString());
    }

    public TSym getSym()
    {
        return mySym;
    }

    public int getLineNum()
    {
        return myLineNum;
    }

    public int getCharNum()
    {
        return myCharNum;
    }

    public String getStrVal()
    {
        return myStrVal;
    }

    public void setSym(TSym sym)
    { 
        this.mySym = sym; 
    }

    public void setIsStruct(boolean isStruct)
    {
        this.isStruct = isStruct;
    }

    public void setIsDecl(boolean isDecl)
    {
        this.isDecl = isDecl;
    }

    public void nameAnalyzer(SymTable symTable)
    {
        if(!isDecl) //case: not declaration, ref to a symbol
        {
            
            try{
            TSym ref = symTable.lookupGlobal(myStrVal);
            if(ref == null) //undeclared id
            {
                ErrMsg.fatal(myLineNum, myCharNum, "Undeclared identifier");
            }else{  //otherwise ref to it
                mySym = ref;
            }

            }catch(Exception e){

            }


        }




       
    }



    private TSym mySym;
    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private boolean isStruct;
    private boolean isDecl;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparse(p, 0);
        p.print(").");
        myId.unparse(p, 0);
    }


    public void nameAnalyzer(SymTable symTable)
    {




        LinkedList<IdNode> idList = new LinkedList<>();

        ExpNode prev = myLoc;
        IdNode curr = myId;
        //step 1: fetch all IdNodes in a list
        //System.out.println("dotelement");
        idList.addFirst(curr);
        while(prev instanceof DotAccessExpNode)
        {
            curr = ((DotAccessExpNode)prev).getPostDotNode();
            prev =((DotAccessExpNode)prev).getFirstDotNode();
          //  System.out.println(curr.getStrVal());
            idList.addFirst(curr); 
        }

        idList.addFirst((IdNode)prev);

        //step 2: iterate over and check.
        SymTable currTable = symTable;
        for(int i = 0; i < idList.size()-1; i++)
        {
            curr = idList.get(i);
            IdNode next = idList.get(i+1);
            //System.out.println(curr.getStrVal());
            //System.out.println(next.getStrVal());

            try {
                TSym ref = currTable.lookupGlobal(curr.getStrVal());
                if(ref == null) 
                {
                   // System.out.println(curr.getStrVal());
                    ErrMsg.fatal(curr.getLineNum(), curr.getCharNum(), "Undeclared identifier");
                    return;
                }

                if(!(ref instanceof StructSym))
                {
                    ErrMsg.fatal(curr.getLineNum(), curr.getCharNum(), "Dot-access of non-struct type");
                    return; 
                }
              //  System.out.println(((StructSym)ref).toString());
                curr.setSym(ref);
                currTable = ((StructSym)ref).getSymTable();
                
                //check the second param
                
                ref = currTable.lookupGlobal(next.getStrVal());
                
                if(ref == null) 
                {
                    ErrMsg.fatal(next.getLineNum(), next.getCharNum(), "Invalid struct field name");
                    return;
                }
                //System.out.println(ref.toString());
                next.setSym(ref);
                //currTable = ((StructSym)ref).getSymTable();
                
                


            } catch (Exception e) {
                // TODO: handle exception
            }

            

        }
                
        
    }

    public IdNode getPostDotNode()
    {
        return myId;
    }

    public ExpNode getFirstDotNode()
    {
        return myLoc;
    }


    private ExpNode myLoc;
    private IdNode myId;
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)  p.print(")");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myLhs.nameAnalyzer(symTable);
        myExp.nameAnalyzer(symTable);
    }

    private ExpNode myLhs;
    private ExpNode myExp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    public void nameAnalyzer(SymTable symTable)
    {
        try{
                //check if its undeclared 
            if(symTable.lookupGlobal(myId.getStrVal()) == null)
            {
                ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Undeclared identifier");
            }

            TSym tmp = symTable.lookupGlobal(myId.getStrVal());
            myId.setSym(tmp);

            
        }catch(Exception e){
            
        }
        

        if(myExpList != null) myExpList.nameAnalyzer(symTable);
    }

    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp.nameAnalyzer(symTable);
    }
    protected ExpNode myExp;

}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    public void nameAnalyzer(SymTable symTable)
    {
        myExp1.nameAnalyzer(symTable);
        myExp2.nameAnalyzer(symTable);
    }

    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" != ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

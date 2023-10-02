import java.util.*;
public class TSym {
    private TypeNode type;

    public TSym()
    {

    }
    
    public TSym(TypeNode type) {
        this.type = type;
    }

    public TypeNode getType() {
        return type;
    }



    public String toString() {
        if(type instanceof IntNode) return "int";
        return "bool";
    }
}

class FnSym extends TSym{
    TypeNode returnType;
    List<TypeNode> paramTypeList;

    public void setReturnType(TypeNode returnType) {
        this.returnType = returnType;
    }

    public void setParamTypeList(List<TypeNode> paramTypeList)
    {
        this.paramTypeList = paramTypeList;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for(int i = 0 ; i < paramTypeList.size(); i++)
        {
            TypeNode curr = paramTypeList.get(i);

            if(curr instanceof IntNode) sb.append("int");
            else if(curr instanceof BoolNode) sb.append("bool");
            else if(curr instanceof StructNode) sb.append(((StructNode)curr).getId());

            if(i != paramTypeList.size() -1) sb.append(", ");
        }

        sb.append(" -> ");

        if(returnType instanceof IntNode) sb.append("int");
        else if(returnType instanceof BoolNode) sb.append("bool");
        else if(returnType instanceof StructNode) sb.append(((StructNode)returnType).getId());
        else if(returnType instanceof VoidNode) sb.append("void");

        return sb.toString();
        
    }
    

}

class StructDefSym extends TSym{
//contains its own symbol table
    SymTable symTable;

    public StructDefSym(SymTable symTable)
    {
        this.symTable = symTable;
    }

    public SymTable getSymTable()
    {
        return symTable;
    }
}

class StructSym extends TSym{
    StructDefSym blueprint;
    String bluePrintName;

    public StructSym(TSym blueprint, String name)
    {
        this.blueprint = (StructDefSym)blueprint;
        this.bluePrintName = name;
    }
 

    public SymTable getSymTable()
    {

        return blueprint.getSymTable();
    }

    public String toString() {
        return bluePrintName;
    }

}

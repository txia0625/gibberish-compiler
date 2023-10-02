///////////////////////////////////////////////////////////////////////////////
// Title:            Project 1
// Files:            SymTable.java
// Semester:         CS536 Spring 2023
//
// Author:           Tong Xia
// Email:            txia38@wisc.edu
// CS Login:         txia
import java.util.*;

/**
 * Represents a SymTable.
 *
 * Bugs: none known
 *
 * @author       Tong Xia
 * @version      1.0
 */
public class SymTable {
    /** The list that represent the symTable */
    private LinkedList<HashMap<String, Sym>> symTable;

    /**
     * Constructor: Generate a new SymTable.
     */
    public SymTable()
    {
        this.symTable = new LinkedList<>();
        this.symTable.addFirst( new HashMap<>());
    }
    /**
     * If this SymTable's list is empty, throw an EmptySymTableException.
     * Otherwise, if the first HashMap in the list contains name as a key,
     * return the associated Sym; otherwise, return null.
     *
     * @param (name) (the key of the symTable)
     * @param (sym) (the associated Sym Object)
     */
    public void addDecl(String name, Sym sym) throws DuplicateSymException,
            EmptySymTableException
    {
        //table is empty
        if(symTable.size() == 0) throw new EmptySymTableException();
        //arguments error
        if(name == null || sym == null) throw new IllegalArgumentException();

        HashMap<String, Sym> firstMap = symTable.getFirst();

        //first map contains key already
        if(firstMap.containsKey(name)) throw new DuplicateSymException();

        //success added
        firstMap.put(name, sym);

    }

    /**
     * Add a new, empty HashMap to the front of the list.
     */
    public void addScope()
    {
        symTable.addFirst(new HashMap<>()); //just add a new map
    }


    /**
     * If this SymTable's list is empty, throw an EmptySymTableException.
     * Otherwise, if the first HashMap in the list contains name as a key,
     * return the associated Sym; otherwise, return null.
     *
     * @param (name) (the key of the symTable)
     * @return (the associated Sym object)
     */
    public Sym lookupLocal(String name) throws EmptySymTableException
    {
        if(symTable.size() == 0) throw new EmptySymTableException();

        if(symTable.getFirst().containsKey(name))
        {
            return symTable.getFirst().get(name);
        }
        return null;
    }


    /**
     * If this SymTable's list is empty, throw an EmptySymTableException.
     * If any HashMap in the list contains name as a key,
     * return the first associated Sym (i.e., the one from the HashMap that is
     * closest to the front of the list); otherwise, return null.
     *
     * @param (name) (the key of the symTable)
     * @return (the associated Sym object)
     */
    public Sym lookupGlobal(String name) throws EmptySymTableException
    {
        if(symTable.size() == 0) throw new EmptySymTableException();
        for(int i = 0; i < symTable.size(); i++)    //simple iteration
        {
            HashMap<String, Sym> currMap = symTable.get(i);
            if(currMap.containsKey(name)) return currMap.get(name);
        }

        return null;
    }

    /**
     * If this SymTable's list is empty, throw an EmptySymTableException;
     * otherwise, remove the HashMap from the front of the list. To clarify,
     * throw an exception only if before attempting to remove,
     * the list is empty (i.e. there are no HashMaps to remove).
     *
     */
    public void removeScope() throws EmptySymTableException
    {
        if(symTable.size() == 0) throw new EmptySymTableException();
        symTable.pollFirst();   //just remove the head
    }

    /**
     * This method is for debugging. First, print “\nSym Table\n”.
     * Then, for each HashMap M in the list, print M.toString() followed by
     * a newline. Finally, print one more newline.
     * All output should go to System.out.
     *
     */
    public void print()
    {
        System.out.print( "\nSym Table\n");
        for(int i = 0; i < symTable.size(); i++)    //simple iteration
        {
            HashMap<String, Sym> currMap = symTable.get(i);
            System.out.println(currMap.toString());
        }
        System.out.println();

    }

}

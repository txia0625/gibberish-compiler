///////////////////////////////////////////////////////////////////////////////
// Title:            Project 1
// Files:            Sym.java
// Semester:         CS536 Spring 2023
//
// Author:           Tong Xia
// Email:            txia38@wisc.edu
// CS Login:         txia

/**
 * Represents a Sym Object.
 *
 * Bugs: none known
 *
 * @author       Tong Xia
 * @version      1.0
 */
public class Sym {
    /** The type of the Sym */
    private String type;

    /**
     * Constructor: Generate a new Sym.
     */
    public Sym(String type)
    {
        this.type = type;
    }

    /**
     * Return the type of the Sym Object
     *
     * @return (the type of the Sym object)
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * Return this Sym's type
     *
     * @return Return this Sym's type
     */

    public String toString()
    {
        return this.type;
    }
}
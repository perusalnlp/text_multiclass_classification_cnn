import java.io.*;
import java.util.*;
 
abstract class References
{
 
    abstract public References toNnf();
 
    abstract public References nnfToCnf();
 
    public References simplifyCnf()  {
        return (new AND(this, T.VALUE)).simplifyCnf();
    }
 
   abstract protected References neg();
 
    abstract protected Vector toClause() ;;
 
    abstract public String toString();
}
 
abstract class Symbol {
	static final int del = 8;
	static final int ab   = 7;
	static final int ff  = 6;
	static final int tt   = 5;
	static final int rightP = 4;
	static final int leftP = 3;
	static final int no    = 2;
	static final int andd   = 1;
	static final int orr   = 0;
 
	abstract int value();
	abstract public String toString();
}
 
abstract class Val extends Symbol {}
 
abstract class Literal extends References {
 
 
    public References toNnf() {
        return this;
    }
 
    public References nnfToCnf() {
        return this;
    }
 
    public References simplifyCnf() {
        return this;
    }
 
    protected Vector toClause() {
        Vector V = new Vector();
        V.addElement(this);
        return V;
    }
 
	abstract protected boolean isNeg(References formula);
 
}
 
class T extends Literal {
	public static final T VALUE = new T();
 
	private T() {}
	protected boolean isNeg(References formula) {
        	return formula == F.VALUE;
	}
 
	protected References neg() {
		return F.VALUE;
	}
 
	public String toString() {
		return "TRUE";
	}
}
 
class F extends Literal {
 
	public static final F VALUE = new F();
 
	private F() {}
 
	protected boolean isNeg(References formula) {
		return formula == T.VALUE;
	}
 
	protected References neg() {
        	return T.VALUE;
	}
 
	public String toString() {
		return "FALSE";
	}
}
 
class True extends Val {
 
	final int value() {
		return Symbol.tt;
	}
 
	public final String toString() {
		return "TRUE";
	}
}
 
class False extends Val {
	final int value() {
	        return Symbol.ff;
	}
 
	public final String toString() {
		return "FALSE";
	}
}
 
class Andd extends Symbol {
	final int value() {
		return Symbol.andd;
    	}
 
	public final String toString() {
		return "CONJ";
    }
}
 
class Orr extends Symbol {
	final int value() {
        	return Symbol.orr;
	}
 
	public final String toString() {
		return "DISJ";
	}
}
 
class Negation extends Symbol {
 
    final int value() {
        return Symbol.no;
    }
 
    public final String toString() {
        return "NEG";
    }
}
 
class RightPara extends Symbol {
 
    final int value() {
        return Symbol.rightP;
    }
 
    public final String toString() {
        return ")";
    }
}
 
class LeftPara extends Symbol {
 
    final int value() {
        return Symbol.leftP;
    }
 
    public final String toString() {
        return "(";
    }
}
 
//ATOM is varaible in given expression
class Atom extends Val {
 
    final String literal;
 
    final int value() {
        return Symbol.ab;
    }
 
    Atom(final String literal) {
        this.literal = literal;
    }
 
    public final String toString() {
        return "Atom(" + this.literal +")";
    }
}
 
class Dollar extends Symbol {
 
    final int value() {
        return Symbol.del;
    }
 
    public final String toString() {
        return "$";
    }
}
 
 
class Lexer {
 
	public  StringReader reader;
	public char lex_char;
	public String lex_text = null;
	public boolean lex_start = true;
	boolean isEol = false;
 
	Lexer(StringReader str) {
		reader = str;
        	lex_start = true;
        	isEol = false;
	}
 
	void advance() throws IOException {
       		int next_char = reader.read();
		if (next_char == -1) {
			isEol = true;
            		return;
 		}
		lex_char = (char)next_char;
		return;
       }
 
	Symbol lex() throws IOException {
 
	lex_text = null;
 
        // If we're at the start of the input stream,
        // advance and reset lex_start.
        if (lex_start) {
            advance();
            lex_start = false;
        }
 
        // If we're at the end of the input stream, return
        // the Eol Token.
        if (isEol) {
            return new Dollar();
        }
 
        // Skip whitespace.
        while (Character.isWhitespace(lex_char) && !isEol) {
            advance();
        }
 
        // Build lex_text.
        if (lex_char == '(') {
            lex_text = "(";
            advance();
        } else if (lex_char == ')' ) {
            lex_text = ")";
            advance();
	} else if(lex_char == '|') {
		lex_text = "OR";
        	advance();
	} else if( lex_char == '&') {
		lex_text = "AND";
		advance();
	} else if (lex_char == '!') {
		lex_text = "NOT";
		advance();
	} else if (Character.isLetter(lex_char)) {
            lex_text = (new Character(lex_char)).toString();
            advance();
        } else if (isEol) { // Catches whitespace immediately before an Eof.
            if (lex_text == null) {
                return new Dollar();
            }
	} else {
		System.out.println("not machting");
	 } 
 
		return textToToken();	
	}
 
 Symbol textToToken() {
 
	if (lex_text.equals("(")) {
            return new LeftPara();
        } else if (lex_text.equals(")")) {
            return new RightPara();
        } else if (lex_text.equals("NOT")) {
            return new Negation();
        } else if (lex_text.equals("AND")) {
            return new Andd();
        } else if (lex_text.equals("OR")) {
            return new Orr();
        } else {
            return new Atom(lex_text);
        }
 
    }
}
 
	// Some deductive formulas
	//P OR NOT P    == TRUE
   	// P AND (P OR Q)   == P
     	//(P OR Q) AND (NOT P OR Q) == Q
 
 class AND extends References {
 
    private final References left;
    private final References right;
 
    public AND(References left, References right) {
        this.left = left;
        this.right = right;
    }
 
    protected References getLeft() {
        return this.left;
    }
 
    protected References getRight() {
        return this.right;
    }
 
    public References toNnf()  {
        return new AND(this.left.toNnf(), this.right.toNnf());
    }
 
    public References nnfToCnf()  {
        return new AND(this.left.nnfToCnf(), this.right.nnfToCnf());
    }	
 
   public References simplifyCnf() {
	Vector clausal = this.toClause();
 
        // Remove any clause containing a Literal and its negation.
        for (int i = 0; i < clausal.size(); i++) {
            Vector curr_disj = (Vector)clausal.elementAt(i);
            loop: for (int j = 0; j < curr_disj.size(); j++) {
                if (curr_disj.elementAt(j) instanceof Literal) {
                    Literal at_j = (Literal)curr_disj.elementAt(j);
                    for (int k = 0; k < curr_disj.size(); k++) {
                        References at_k = (References)curr_disj.elementAt(k);
                        if (at_j.isNeg(at_k)) {
                            clausal.removeElementAt(i--);
                            break loop;
                        }
                    }
                }
            }
        }
 
        // Remove any clause that is a superset of another clause.
        for (int i = 0; i < clausal.size() - 1; i++) {
            for (int j = i+1; j < clausal.size(); j++) {
                if (isSubset((Vector)clausal.elementAt(i),
                             (Vector)clausal.elementAt(j))) {
                    clausal.removeElementAt(j);
                } else if (isSubset((Vector)clausal.elementAt(j),
                                    (Vector)clausal.elementAt(i))) {
                    clausal.removeElementAt(i);
                }
            }
        }
 
        // Return a Formula.
        return fromClause(clausal);
	}
 
 	 private static boolean isSubset(Vector v1, Vector v2) {
        	for (int i = 0; i < v1.size(); i++) {
            		if (!v2.contains(v1.elementAt(i))) {
               	 return false;
            }
        }
        return true;
    }
 
  private static References fromClause(Vector clausal) {
        References conj = T.VALUE;
 
        for (int i = clausal.size(); i > 0; i--) {
            References disj = F.VALUE;
            Vector curr_disj = (Vector)clausal.elementAt(i-1);
 
            for (int j = curr_disj.size(); j > 0; j--) {
                References curr_lit = (References)curr_disj.elementAt(j-1);
                if (disj == F.VALUE) {
                    disj = curr_lit;
                } else {
                    disj = new OR(disj, curr_lit);
                }
            }
 
            if (conj == T.VALUE) {
                conj = disj;
            } else {
                conj = new AND(disj, conj);
            }
        }
 
        return conj;
    }
 
protected Vector toClause()  {
        Vector clause  = new Vector();
        Vector c_left;
        Vector c_right;
 
        if (this.left instanceof AND) {
            c_left = this.left.toClause();
            for (int i = 0; i < c_left.size(); i++) {
                clause.addElement(c_left.elementAt(i));
            }
        } else {
            c_left = this.left.toClause();
            clause.addElement(c_left);
        }
 
        if (this.right instanceof AND) {
            c_right = this.right.toClause();
            for (int i = 0; i < c_right.size(); i++) {
                clause.addElement(c_right.elementAt(i));
            }
        } else {
            c_right = this.right.toClause();
            clause.addElement(c_right);
        }
        return clause;
 }
 
protected References neg() {
        return new OR(this.left.neg(), this.right.neg());
    }
 
    public String toString() {
        return ("(" + this.left.toString() + " & "
                + this.right.toString() + ")");
    }
 
}
 
class OR extends References {
 
    private final References left;
    private final References right;
 
    public OR(References left, References right) {
        this.left = left;
        this.right = right;
    }
 
    protected References getLeft() {
        return this.left;
    }
 
    protected References getRight() {
        return this.right;
    }
 
   public References toNnf()  {
        return new OR(this.left.toNnf(), this.right.toNnf());
    }
 
    public References nnfToCnf()  {
        References left = this.left.nnfToCnf();
        References right = this.right.nnfToCnf();
 
        if (left instanceof AND) {
            AND conj = (AND)left;
            return new AND(new OR(conj.getLeft(), right).nnfToCnf(),
                            new OR(conj.getRight(), right).nnfToCnf());
        }
        else if (right instanceof AND) {
            AND conj = (AND)right;
            return new AND(new OR(left, conj.getLeft()).nnfToCnf(),
                            new OR(left, conj.getRight()).nnfToCnf());
        }
        return new OR(left, right);
    }
 
    protected Vector toClause() {
        Vector clause  = new Vector();
        Vector c_left  = this.left.toClause();
        Vector c_right = this.right.toClause();
 
        for (int i = 0; i < c_left.size(); i++) {
            clause.addElement(c_left.elementAt(i));
        }
 
        for (int i = 0; i < c_right.size(); i++) {
            clause.addElement(c_right.elementAt(i));
        }
 
        return clause;
    }
 
    protected References neg() {
        return new AND(this.left.neg(), this.right.neg());
    }
 
    public String toString() {
        return ("(" + this.left.toString() + " | "
                + this.right.toString() + ")");
    }
 
}
 
 class NOT extends References {
 
    private final References term;
 
    public NOT(References term) {
        this.term = term;
    } 
 
    protected References getTerm() {
        return this.term;
    }
 
    public References toNnf()  {
        if (this.term instanceof AND ||
            this.term instanceof OR ||
            this.term instanceof NOT) {
 
            return this.term.neg().toNnf();
 
        } else if (this.term instanceof Literal) {
            return this;
        }
	return null; 
    }
 
	public References nnfToCnf()  {
 
        if (this.term instanceof Literal) {
            return this;
        }
	return null;
    }
 
    public References simplifyCnf()  {
        if (this.term instanceof Literal) {
            return this;
        }
	return null;
    }
 
    protected Vector toClause() {
        Vector V = new Vector();
        V.addElement(this);
        return V;
    }
 
    protected References neg() {
        return this.term;
    }
 
    public String toString() {
        return "!" + term.toString();
    }
 
} 
 
class Variable extends Literal {
 
    private final String name;
 
    public Variable(String name) {
        this.name = name;
    }
 
    protected String getName() {
        return name; 
    }     
 
    protected boolean isNeg(References formula) {
        if (formula instanceof NOT) {
            References term = ((NOT)formula).getTerm();
            if (term instanceof Variable) {
                return ((Variable)term).getName().equals(this.name);
            }
        }
        return false; 
    }
 
    protected References neg() {
        return new NOT(this);
    }
 
    public String toString() {
        return this.name;
    }
}
 
class Parser 
{
    private static Lexer lexer;	
    private static Symbol token = new Dollar();
 
    private static References absyn = null;
 
    private static Stack op_stack  = new Stack();
    private static Stack val_stack  = new Stack();
 
    private static final int S = 0;
   private static final int R = 1;
 
 
    private static final int E1 = 2;
 
    private static final int E2 = 3;
 
    private static final int E3 = 4;
 
    private static final int E4 = 5;
 
	  private static final int[][] o_p_table = {
        /*  ----------------------input token--------------------- */
        /*  -op_stk-    OR  AND NOT (   )   Val Val Val  $  */
        /*  ----------------------input token--------------------- */
        /*      OR */{   R,  S,  S,  S,  R,  S,  S,  S,  R },
        /*     AND */{   R,  R,  S,  S,  R,  S,  S,  S,  R },
        /*     NOT */{   R,  R,  S,  S,  R,  S,  S,  S,  R },
        /*       ( */{   S,  S,  S,  S,  S,  S,  S,  S,  E1},
        /*       ) */{   R,  R,  R,  E4, R,  S,  S,  S,  R },
        /*         */{ /* Vals aren't held on the op_stack but */},
        /*         */{ /* blank lines here mean we can look up */},
        /*         */{ /* Token.DOLLAR tokens correctly.       */},
        /*       $ */{  S,  S,  S,  S,  E2, S,  S,  S,  E3},
    };
 
  public Parser(StringReader input) throws IOException {
        lexer = new Lexer(input);
        op_stack.push(new Dollar());
       absyn = parse();
}
 
public References getAST() {
        return absyn;
    }
 
 public   References parse()
        throws IOException {
 
	token = lexer.lex();
 
	while (true) {
         // Check if we should accept the sentence.
        if (token.value() == Symbol.del &&
            ((Symbol)op_stack.peek()).value() == Symbol.del) {
 
	    return (References)val_stack.pop();
 
	}
 
 
	switch(o_p_table[((Symbol)op_stack.peek()).value()][token.value()]) {
        case S :
          shift();
          token = lexer.lex();
         break;
        case R :
         reduce();
          break;
	default :
        	break;
	}
 
	}
 
    }
 
 public  void shift() {
 
        // Input token is a value (True, False or Atom).
        if (token instanceof Val) {
	  if (token.value() == Symbol.ab) {
                val_stack.push(new Variable(((Atom)token).literal));
            }
        } else {
            // Input token is an operator (Conj, Disj, Not, (, ), $).
            op_stack.push(token);
        }
 
        return;
    }
 
public  void reduce() {
 
      switch (((Symbol)op_stack.pop()).value()) {
      case Symbol.no :
        val_stack.push(new NOT((References)val_stack.pop()));
        break;
      case Symbol.rightP :
        op_stack.pop(); // Pop an LParen
        break;
      case Symbol.andd :
        {
          References temp = (References)val_stack.pop();
          val_stack.push(new AND((References)val_stack.pop(),
                                              temp));
        }
        break;
      case Symbol.orr :
        {
           References temp = (References)val_stack.pop();
	   val_stack.push(new OR((References)val_stack.pop(), temp));
 
	}
       default:
		 break;
      }
 
      return;
   }
}
 
public class Tautology
{
 
public static void main(String ar[]) throws IOException
{
	int i = 0;
	Parser myParser;
	References  formula;	
	StringReader reader; 
 
	String  expr[] = {"(!a | (a & a))", "(!a | (b & !a))","(!a | a)","( a & ( !b | b)) | ( !a & ( !b |b ))"};
 
	for ( i = 0; i < expr.length ; i++)
        {
		reader = new StringReader(expr[i]);
		myParser = new Parser(reader);
		formula = myParser.getAST();	
 
		System.out.println("Formula = "+ formula.toString());
 
		formula = formula.toNnf();
		System.out.println("toNnf Formula = "+ formula.toString());
 
		// Convert to CNF
        	formula = formula.nnfToCnf();
            	System.out.println("to nntoCNF ="+formula.toString());
 
        	// Simplify
        	formula = formula.simplifyCnf();
            	System.out.println("simplified ="+formula.toString());
 
		if(T.VALUE == formula)
			System.out.println("Tautology");
		else
			System.out.println("not Tautology");
 
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
	}
 }
}

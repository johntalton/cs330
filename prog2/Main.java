import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;

class HashTableValue {
    HashTableValue(int knd, int lev, int val) {
        kind = knd; level = lev; value = val; size = 1;}

    HashTableValue(int knd, int lev, int val, int siz){
        kind = knd; level = lev; value = val; size = siz; }        
        
    int kind;
    int level;
    int value;
    int size;  // the size of the data type   
    
}

class getInputName extends Frame {
    TextField fileName = new TextField(70);
    static String OldFileName = new String("");
    
    getInputName() {
        super("Enter file name (.pl0 will be added)");
        add("North",fileName);
        pack();
        fileName.setText(OldFileName);
        show();
    }
    public boolean action (Event evt, Object arg) {
        if (evt.target == fileName) {
            String name = fileName.getText();
            OldFileName = name;
            name = name + ".pl0";
            Parser.load(name);
            this.hide();
            return true;
        }
        else return false;
    } // action
} // getInputName

class getOutputName extends Frame {
    TextField fileName = new TextField(70);
    getOutputName() {
        super("Enter file name (.pl0 will be added)");
        add("North",fileName);
        pack();
        show();
    }
    public boolean action (Event evt, Object arg) {
        if (evt.target == fileName) {
            String name = fileName.getText();
            name = name + ".pl0";
            Parser.save(name);
            this.hide();
            return true;
        }
        else return false;
    } // action
} // getOutputName

class Parser {
    Hashtable symbolTable = new Hashtable();
    boolean errorFound = false;

    final int constant = 0;
    final int variable = 1;
    final int procedure = 2;
    final int array = 3;
    final int function = 4;

static final private int lit = 0;  // put literal on top of stack
static final private int opr = 1;  // perform an operation
static final private int lod = 2;  // put a variable on top of stack
static final private int sto = 3;  // put a value from top of stack to a variable
static final private int cal = 4;  // call a procedure
static final private int inc = 5;  // increment the stack pointer
static final private int jmp = 6;  // unconditional jump to specified code address
static final private int jpc = 7;  // conditional jump, requires 0 on top of stack
static final private int win = 8;  // write integer
static final private int wst = 9;  // write string
static final private int wln = 10; // write line
static final private int rin = 11; // read integer
static final private int sta = 12; // read array value
static final private int lda = 13; // Load the array
static final private int ret = 14; // return a value from a func

    Vector statBeginSys = new Vector(50);
    Vector declBeginSys = new Vector(50);
    Vector programSet = new Vector(50);
    Vector emptySet = new Vector(1);
    Vector compareSet = new Vector(3);
    Vector keywords = new Vector(25);

    Parser() {
        String[] S = {"var","constant","procedure","function"};
        declBeginSys = makeSet(S,4);
        String[] S1 = {"if","begin","call","while"};
        statBeginSys = makeSet(S1,4);
        programSet = union(declBeginSys, statBeginSys);
        programSet.addElement(".");
        String[] S2 = {"<",">","="};
        compareSet = makeSet(S2,3);
        String[] keywrds = {"begin","call","constant","do","end","if","odd",
                             "procedure","then","var","while","writeint",
                             "writestr", "writeln", "readint","for","function"};
        keywords = makeSet(keywrds,17); // this is the number of keywrds
    }


    boolean errorPresent() {
        return errorFound;
    };

    boolean isIdentifier(StreamTokenizer p) {
        if (p.ttype == StreamTokenizer.TT_WORD)
          return !keywords.contains(p.sval);
        else return false;
    }

    void error(int errorNum) {
        String errors[] = {
            /* 00 */ "use = instead of :=",
            /* 01 */ "= must be followed by number",
            /* 02 */ "identifier must be followed by :=",
            /* 03 */ "CONST, VAR, PROCEDURE followed by identifier",
            /* 04 */ "  ;  or   ,   missing",
            /* 05 */ "incorect symbol after procedure declaration",
            /* 06 */ "statement expected",
            /* 07 */ "incorrect symbol after statemnet",
            /* 08 */ "period expected",
            /* 09 */ "  ;  missing",
            /* 10 */ "undeclared identifier",
            /* 11 */ "illegal assignment ",
            /* 12 */ ":= expected",
            /* 13 */ "call requires indetifier",
            /* 14 */ "illegal call",
            /* 15 */ "THEN expected",
            /* 16 */ "  ;   or END expected",
            /* 17 */ "DO expected",
            /* 18 */ "incorrect symbol after statement",
            /* 19 */ "relational operator expected",
            /* 20 */ "expression cannot contain  procedure identifier",
            /* 21 */ "  )  missing",
            /* 22 */ "factor cannot be followed by this symbol",
            /* 23 */ "expression cannot begin with this symbol",
            /* 24 */ "each program must start with keyword 'program'",
            /* 25 */ "string literal expected",
            /* 26 */ " ( missing",
            /* 27 */ " [ missing",
            /* 28 */ " ] missing",
            /* 29 */ " expecting literal or const",
            /* 30 */ " , missing",
            /* 31 */ " can not call processes inside of factor"
            };

        Main.loadText.appendText("\nError[" + errorNum + "]: "+errors[errorNum]+"\n");
        errorFound = true;
        }

    Vector union(Vector V1, Vector V2) {
        int count;
        Vector V3 = new Vector(50);
        for (count = 0; count < V1.size(); count++)
            V3.addElement(V1.elementAt(count));
        if (V2.size() > 0)
            for (count = 0; count < V2.size(); count++)
                 V3.addElement(V2.elementAt(count));
        return V3;
    } // union

    Vector makeSet(String[] Str, int numElements) {
        int count;
        Vector V = new Vector(20);
        for (count = 0; count < numElements; count++)
           V.addElement(Str[count]);
        return V;
    }

    void test(StreamTokenizer p, Vector V1, Vector V2, int errorNum) {
            int sym, count;
            String currentSym = "";
            if (p.ttype >= 0) currentSym= getToken(p);
            else if (p.ttype == StreamTokenizer.TT_WORD) currentSym = p.sval;
out:
          if (!V1.contains(currentSym)) {
                error(errorNum);
                Vector V3 = new Vector(50);
                V3 = union(V1,V2);
                while (!V3.contains(currentSym)) {
                    sym = getSym(p);
                    if (p.ttype >= 0) currentSym= getToken(p);
                    else if (p.ttype == StreamTokenizer.TT_WORD) currentSym = p.sval;
                    else break out;
                }
            }
    } //test


        public static void load(String fileName) {
        try {
              FileInputStream fs = new FileInputStream(fileName);
              byte buf1[] = new byte[5000];
              Main.loadText.setText("");
              try {
                fs.read(buf1,0,fs.available());
              } // try
              catch (Exception e) {
                  Main.loadText.appendText("Error: " + e.toString());
              } // catch

              String program = new String(buf1,0);
              Main.loadText.appendText(program);
        } // try
        catch (Exception e) {
              Main.loadText.appendText("Error: " + e.toString());
        } // catch
    } // load

    public static void save(String fileName) {
        try {
              FileOutputStream fOut = new FileOutputStream(fileName);
              byte buf1[] = new byte[5000];
              String outString = Main.loadText.getText();
              outString.getBytes(0,outString.length(),buf1,0);
              try {
                fOut.write(buf1);
                fOut.close();
              } // try
              catch (Exception e) {
                  Main.loadText.appendText("Error: " + e.toString());
              } // catch

        } // try
        catch (Exception e) {
              Main.loadText.appendText("Error: " + e.toString());
        } // catch
    } // load

String getToken(StreamTokenizer p) {
    if (p.ttype >= 0)
        return String.valueOf((char)p.ttype);
    else return "token not a single character";
    }

int getSym(StreamTokenizer p) {
    int sym;
    Main.loadText.appendText(" ");
    do {
        try{sym=p.nextToken();} catch (Exception e)
            {Main.loadText.appendText("failed to retrieve token\n");sym=0;};
        if (sym==StreamTokenizer.TT_EOL) Main.loadText.appendText("\n");
    } while (sym==StreamTokenizer.TT_EOL);
    switch (sym) {
        case StreamTokenizer.TT_EOF:
                Main.loadText.appendText("End of file encountered\n");
                sym = (int)'.';
                break;
        case StreamTokenizer.TT_EOL:
                Main.loadText.appendText("\n"); break;
        case StreamTokenizer.TT_NUMBER:
                Main.loadText.appendText(""+ p.nval);
                break;
        case StreamTokenizer.TT_WORD:
                Main.loadText.appendText(p.sval);
                break;
        default:
                String charToken = getToken(p);
                Main.loadText.appendText(charToken);
                if (p.ttype == (int)'"') {
                    Main.loadText.appendText(p.sval);
                    Main.loadText.appendText(charToken);
                }
                break;
        } // switch
    return sym;
}

void lookingFor(StreamTokenizer p, String token, /*String notFoundMsg,*/ int errorNum){
    int sym;
    if ((p.ttype < 0) && (!token.equals(p.sval))) error(errorNum);
    else if ((p.ttype >= 0) && (!token.equals(getToken(p)))) error(errorNum);
    else sym = getSym(p);
}

int isFactorBeginSymbol (StreamTokenizer p, int sym) {
    if (isIdentifier(p)) return 1;
    else if (sym == StreamTokenizer.TT_NUMBER) return 2;
    else if (getToken(p).equals("(") ) return 3;
    else {
        error(23);
        return 0; }
}

char preceding (char ch) {
    int digit = Character.digit(ch,10);
    return Character.forDigit(digit-1,10);
}

HashTableValue searchTable(String hashKey) {
    char level;
    // try locally first
    HashTableValue entry = (HashTableValue)symbolTable.get(hashKey);
    if ( entry == null ) { // search outer levels
      level = hashKey.charAt(hashKey.length()-1);
      // assumes single digit levels
      while ((entry == null) && (level > '0')) {
        level = preceding(level);
        hashKey = hashKey.substring(0,hashKey.length()-1) + String.valueOf(level);
        entry = (HashTableValue)symbolTable.get(hashKey);
        level = hashKey.charAt(hashKey.length()-1);
      }
    }
    return entry;
}

void factor (StreamTokenizer p, int level, Vector followingSymbols) {
    int sym, nextSym,numPram = 0;
    String token;
    if (Main.trace.getState()) Main.loadText.appendText("entering factor\n");
/*    // get next token
    sym=getSym(p);*/ sym = p.ttype;
    nextSym = isFactorBeginSymbol(p, sym);
        switch (nextSym) {
        case 1 : { // it is an identifier
            HashTableValue entry;
            String hashKey = p.sval;
            sym = getSym(p);
            token = getToken(p);

            if(token.equals("(")){
               processCallFunc(p,level,hashKey);
               entry = null;
            } else {
               hashKey = hashKey +"_" + Integer.toString(level);            
               entry = searchTable(hashKey);
               if (entry == null) Main.loadText.appendText(hashKey + "not found\n");   
            }
            if(entry != null) switch (entry.kind) {
            case procedure : Main.loadText.appendText(hashKey + ":procedure name in factor\n"); /*sym=getSym(p);*/break;
            case constant : Main.PL0.gen(lit,0,entry.value); /*sym=getSym(p);*/break;
            case variable : Main.PL0.gen(lod,level - entry.level,entry.value); /*sym=getSym(p);*/break;
            case array : 
               Main.PL0.gen(lit,0,entry.size); 
               //sym=getSym(p);
               lookingFor(p,"[",/* [ missing */27);
               String S[] = { "]" };
               expression(p, level, union(followingSymbols,makeSet(S,1)));
               lookingFor(p, "]",/* ] missing */28);
               Main.PL0.gen(lda,level - entry.level,entry.value);
               break;
            }  break;
        }
        case 2 : { // it is a literal number
            Main.PL0.gen(lit,0,(int)p.nval);sym=getSym(p);
            break;
        }
        case 3 : { // it is a left parenthesis
            sym=getSym(p);
            String[] S = {")"};
            expression(p, level,union(followingSymbols,makeSet(S,1)));
            token = getToken(p);
            // if (token.equals(")")) sym = getSym(p);
            // else error(21);
            lookingFor(p,")",/*"mismatched parentheses",*/21);
            break;
        }
        } // switch
    if (Main.trace.getState()) Main.loadText.appendText("leaving factor\n");
}

void term (StreamTokenizer p, int level, Vector followingSymbols) {
    int sym;
    String[] S = {"*","/"};
    if (Main.trace.getState()) Main.loadText.appendText("entering term\n");
    factor(p, level,union(followingSymbols,makeSet(S,2)));
    // get next token to see if multiplication or division
    /*sym=getSym(p);*/
    String token = getToken(p);
    while ((token.equals("*")) || (token.equals("/")) ){
        sym=getSym(p);
        factor(p, level,union(followingSymbols,makeSet(S,2)));
        if (token.equals("*")) Main.PL0.gen(opr,0,4);
        else Main.PL0.gen(opr,0,5);
        /*sym=getSym(p);*/
        token = getToken(p);
    }
    if (Main.trace.getState()) Main.loadText.appendText("leaving term\n");
}

void expression (StreamTokenizer p, int level, Vector followingSymbols) {
    int sym;
    String token;
    String[] S = {"+","-"};
    if (Main.trace.getState()) Main.loadText.appendText("entering expression\n");
    /*sym=getSym(p); */ // look for leading + (positive) or - (negative)
    token = getToken(p);
    if ((token.equals("+")) || (token.equals("-")) ) { // leading sign character
      sym=getSym(p);
      term(p, level,union(followingSymbols,makeSet(S,2)));
      if (token.equals("-")) Main.PL0.gen(opr,0,1);
    }
    else {
      term(p, level,union(followingSymbols,makeSet(S,2)));
    }
    /* sym=getSym(p); */
    token = getToken(p);
    while ((token.equals("+")) || (token.equals("-")) ){
        sym=getSym(p);
        term(p, level,union(followingSymbols,makeSet(S,2)));
        if (token.equals("+")) Main.PL0.gen(opr,0,2);
        else Main.PL0.gen(opr,0,3);
        /* sym=getSym(p); */
        token = getToken(p);
    }
    if (Main.trace.getState()) Main.loadText.appendText("leaving expression\n");
}

    int getRelationalOperator (StreamTokenizer p) {
        String equalToken = "=";
        // get next token, should be <, =,  or >
        int sym;
        sym= p.ttype; /* getSym(p); */
        String firstToken = getToken(p);
        if (firstToken.equals("=")) return 7;
        else if (firstToken.equals(">")) {
            // get next token
            sym=getSym(p);
            if ((p.ttype > 0) || (equalToken.equals(getToken(p)))) {
                sym=getSym(p);return 10;}
            else {return 11;}
        } else if (firstToken.equals("<")) {
            // get next token
            sym=getSym(p);
            String secondToken = getToken(p);
            if (secondToken.equals("=")) {
                sym=getSym(p);return 12;}
            else if (secondToken.equals(">")) {
                sym=getSym(p);return 8;}
            else { return 9;}
        }
        return 0; //error
    } // getRelationalOperator

void condition (StreamTokenizer p, int level, Vector followingSymbols) {
    int sym,opCode;
    if (Main.trace.getState()) Main.loadText.appendText("entering condition\n");
    // assume token was pushed back
    /* sym=getSym(p); */ // look ahead to see if "odd" is called
    sym = p.ttype;
    if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("odd"))) {
        sym=getSym(p);expression(p, level,followingSymbols);
        Main.PL0.gen(opr,0,6);
    } else {
        expression(p, level,union(followingSymbols,compareSet));  // compile left side of expression
        String token = getToken(p);
        if (compareSet.contains(token)) {
            opCode = getRelationalOperator(p);
            expression(p, level,followingSymbols);  // compile right side of expression
            Main.PL0.gen(opr,0,opCode);
        } else error(19);
    }
    if (Main.trace.getState()) Main.loadText.appendText("leaving condition\n");
}

void processAssign (StreamTokenizer p, int level, Vector followingSymbols) {
    int sym;
    if (Main.trace.getState()) Main.loadText.appendText("entering processAssign\n");
    sym=p.ttype; /* getSym(p);  // get target of assignment */
    String hashKey = p.sval+"_" + Integer.toString(level);
    HashTableValue entry;
    entry = searchTable(hashKey);
    if (entry == null) {
        error(10);
        Main.loadText.appendText("\n"+hashKey + "not found\n");
    } else if ((entry.kind != variable) && (entry.kind != array)) {
        error(11);
        Main.loadText.appendText("\n"+hashKey + "not a variable\n");
    } else {
        // skip over : and =
        sym = getSym(p);
        if(entry.kind == array){
           lookingFor(p,"[",/* [ missing */27);
           Main.PL0.gen(lit,0,entry.size);
           String S[] = { "]" };
           expression(p, level, union(followingSymbols,makeSet(S,1)));
           lookingFor(p, "]",/* ] missing */28);
        }
        lookingFor(p,":",12);
        lookingFor(p,"=",12);
        expression(p, level,followingSymbols);
        if(entry.kind == variable){
           Main.PL0.gen(sto, level-entry.level, entry.value);
        }else{
           Main.PL0.gen(sta,level - entry.level,entry.value);
        }
        
    }
    if (Main.trace.getState()) Main.loadText.appendText("leaving processAssign\n");
}




void processCall (StreamTokenizer p, int level) {
    int sym;
    int numPram = 0;
    if (Main.trace.getState()) Main.loadText.appendText("entering processCall\n");
    // get the procedure name
    sym=getSym(p);
    String ProcName = p.sval;
    sym=getSym(p);
    String token = getToken(p);
    Main.PL0.gen(inc,level,3);
    if(token.equals("(")){
      sym=getSym(p);
      token = getToken(p);
      while(!token.equals(")")){
               
         String S[] = { ",",")" };
         expression(p, level,makeSet(S,2));
               
         //sym = getSym(p);

         token = getToken(p);
         if(token.equals(")")){ numPram++; break;}
         lookingFor(p,",",30);
         numPram++;
         token = getToken(p);
      }
      lookingFor(p,")",21);      
   }

    String hashKey = ProcName + "_" + Integer.toString(numPram) +"_" + Integer.toString(level);
    HashTableValue entry = searchTable(hashKey);
    if (entry == null) {
        error(10);
        Main.loadText.appendText(hashKey + "not found\n");
    } else if (entry.kind != procedure) {
        error(14);
        Main.loadText.appendText(hashKey + "not a procedure\n");
    } else {
        Main.PL0.gen(inc,level,0-(3+numPram));
        Main.PL0.gen(cal,level-entry.level,entry.value);
        //sym = getSym(p);
    }
    if (Main.trace.getState()) Main.loadText.appendText("leaving processCall\n");
}



void processCallFunc (StreamTokenizer p, int level,String funcName) {
   if (Main.trace.getState()) Main.loadText.appendText("entering processCallFunc\n");
   int sym;
   int numPram = 0;
     // get the procedure name
   //sym=getSym(p);
   //sym=getSym(p);
   String token = getToken(p);
   Main.PL0.gen(inc,level,4);
   if(token.equals("(")){
      sym=getSym(p);
      token = getToken(p);
      while(!token.equals(")")){
         String S[] = { ",",")" };
         expression(p, level,makeSet(S,2));
         //sym = getSym(p);
         token = getToken(p);
         if(token.equals(")")){ numPram++; break;}
         lookingFor(p,",",30);
         numPram++;
         token = getToken(p);
      }
      lookingFor(p,")",21);      
   }
   String hashKey = funcName + "_" + Integer.toString(numPram) +"_" + Integer.toString(level);
   HashTableValue entry = searchTable(hashKey);
    if (entry == null) {
        error(10);
        Main.loadText.appendText(hashKey + " not found\n");
    } else if (entry.kind == procedure){
        error(32);
    } else if (entry.kind != function) {
        error(14);
        Main.loadText.appendText(hashKey + " not a function\n");
    } else {
        Main.PL0.gen(inc,level,0-(3+numPram));
        Main.PL0.gen(cal,level-entry.level,entry.value);
        //sym = getSym(p);
    }
   if (Main.trace.getState()) Main.loadText.appendText("leaving processCallFunc\n");
}

void processIf (StreamTokenizer p, int level, Vector followingSymbols) {
    if (Main.trace.getState()) Main.loadText.appendText("entering processIf\n");
    int sym, codeIndex1;
    sym = getSym(p);
    String[] S = {"then"};
    condition(p, level,union(followingSymbols,makeSet(S,1)));
    lookingFor(p,"then",/*"then not found after condition",*/15);
    codeIndex1 = Main.PL0.codeindex();
    Main.PL0.gen(jpc,0,0);
    statement(p, level,followingSymbols);
    Main.PL0.BackPatch(codeIndex1);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processIf\n");
}

void processBeginEnd (StreamTokenizer p, int level, Vector followingSymbols) {
    int sym;
    if (Main.trace.getState()) Main.loadText.appendText("entering processBeginEnd\n");
    sym = getSym(p);
    statement(p, level, followingSymbols);
    sym=p.ttype; // getSym(p); // look for semicolon or end
    String token = getToken(p);
    String[] S1 = {";"};
    Vector test = union(statBeginSys,makeSet(S1,1));
    while (test.contains(token)) {
        lookingFor(p,";",/*"semicolon expected",*/9);
        String[] S = {";","end"};
        statement(p, level,union(followingSymbols,makeSet(S,2)));
        sym=p.ttype; // getSym(p);
        token = getToken(p);
    }
    lookingFor(p,"end",/*"end not found in begin..end",*/16);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processBeginEnd\n");
}

void processWhile (StreamTokenizer p, int level, Vector followingSymbols) {
    if (Main.trace.getState()) Main.loadText.appendText("entering processWhile\n");
    int sym, codeIndex1, codeIndex2;
    codeIndex1 = Main.PL0.codeindex();
    sym = getSym(p);
    String[] S = {"do"};
    condition(p, level,union(followingSymbols,makeSet(S,1)));
    codeIndex2 = Main.PL0.codeindex();
    Main.PL0.gen(jpc,0,0);
    lookingFor(p,"do",/*"'do' not found after condition in while",*/17);
    statement(p, level,followingSymbols);
    Main.PL0.gen(jmp,0,codeIndex1);
    Main.PL0.BackPatch(codeIndex2);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processWhile\n");
}

/****************************************
* for(S1; C1; S2) S3
*
*     S1
* L1: C1
*     jmc L4
*     jmp L3
* L2: S2
*     jmp L1
* L3: S3
*     jmp L2
* L4: 
*
*****************************************/
void processFor (StreamTokenizer p, int level, Vector followingSymbols) {
    if (Main.trace.getState()) Main.loadText.appendText("entering processFor\n");
    int sym, codeIndex1, codeIndex2, codeIndex3, codeIndex4;
    sym = getSym(p);
    
    lookingFor(p,"(",/* ( missing*/26);
    String[] S = {";"};
/*S1*/statement(p, level,union(followingSymbols,makeSet(S,1)));
    lookingFor(p,";",/* ; missing*/9);
/*L1*/codeIndex1 = Main.PL0.codeindex();
    //S = ";";
/*C1*/condition(p, level,union(followingSymbols,makeSet(S,1)));
lookingFor(p,";",/* ; missing*/9);
/*jpc L4*/codeIndex4 = Main.PL0.codeindex();Main.PL0.gen(jpc,0,0);
/*jmp L3*/codeIndex3 = Main.PL0.codeindex();Main.PL0.gen(jmp,0,0);
/*L2*/codeIndex2 = Main.PL0.codeindex();
    String[] S1 = {")"};
/*S2*/statement(p, level,union(followingSymbols,makeSet(S1,1)));
    lookingFor(p,")",/* ) missing*/21);
/*jmp L1*/Main.PL0.gen(jmp,0,codeIndex1);
/*L3*/Main.PL0.BackPatch(codeIndex3);
/*S3*/statement(p, level, followingSymbols);
/*jmp L2*/Main.PL0.gen(jmp,0,codeIndex2);
/*L4*/Main.PL0.BackPatch(codeIndex4);
   
   if (Main.trace.getState()) Main.loadText.appendText("leaving processFor\n");
}


void processRet (StreamTokenizer p, int level, Vector followingSymbols) {
   if (Main.trace.getState()) Main.loadText.appendText("entering processRet\n");
   int sym;
   sym = getSym(p);
   String[] S = { ";","end" };
   expression(p, level, union(followingSymbols,makeSet(S,2)));
   Main.PL0.gen(ret,0,0);
   Main.PL0.gen(opr,0,0);
   if (Main.trace.getState()) Main.loadText.appendText("leaving processRet\n");
}

void processWriteInt (StreamTokenizer p, int level, Vector followingSymbols) {
    if (Main.trace.getState()) Main.loadText.appendText("entering processWriteInt\n");
    int sym = getSym(p);
    sym=p.ttype; /* getSym(p);  // get target of assignment */
    String hashKey = p.sval+"_" + Integer.toString(level);
    HashTableValue entry;
    entry = searchTable(hashKey);
    if (entry == null) {
        error(10);
        Main.loadText.appendText("\n"+hashKey + "not found\n");
    } else if (entry.kind != variable) {
        error(11);
        Main.loadText.appendText("\n"+hashKey + "not a variable\n");
    } else {
        Main.PL0.gen(win, level-entry.level, entry.value);
    }
    sym = getSym(p);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processWriteInt\n");
}

void processWriteStr (StreamTokenizer p, int level, Vector followingSymbols) {
    if (Main.trace.getState()) Main.loadText.appendText("entering processWriteStr\n");
    int sym = getSym(p);
    if (p.ttype == (int)'"') {
        Main.PL0.storeLiteral(p.sval);
        Main.PL0.gen(wst,0,Main.PL0.getLiteralIndex());
        sym = getSym(p);
    }
    else error(25);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processWriteStr\n");
}

void processWriteLn (StreamTokenizer p, int level, Vector followingSymbols) {
    if (Main.trace.getState()) Main.loadText.appendText("entering processWriteLn\n");
    Main.PL0.gen(wln,0,0);
    int sym = getSym(p);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processWriteLn\n");
}

void processReadInt (StreamTokenizer p, int level, Vector followingSymbols) {
    int sym;
    if (Main.trace.getState()) Main.loadText.appendText("entering processReadInt\n");
    sym= getSym(p);  // get target of read*/
    String hashKey = p.sval+"_" + Integer.toString(level);
    HashTableValue entry;
    entry = searchTable(hashKey);
    if (entry == null) {
        error(10);
        Main.loadText.appendText("\n"+hashKey + "not found\n");
    } else if (entry.kind != variable) {
        error(11);
        Main.loadText.appendText("\n"+hashKey + "not a variable\n");
    } else {
        Main.PL0.gen(rin, level-entry.level, entry.value);
        sym = getSym(p);
    }
    if (Main.trace.getState()) Main.loadText.appendText("leaving processReadInt\n");
}

void statement (StreamTokenizer p, int level, Vector followingSymbols) {
    int sym;
    if (Main.trace.getState()) Main.loadText.appendText("entering statement\n");
    /* sym=getSym(p); */ sym = p.ttype;
    if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("call")))
        processCall(p, level);
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("if")))
        processIf(p, level, followingSymbols);
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("begin")))
        processBeginEnd(p, level, followingSymbols);
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("while")))
        processWhile(p, level, followingSymbols);
    
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("for")))
        processFor(p, level, followingSymbols);
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("return")))
        processRet(p, level, followingSymbols);
     
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("writeint")))
        processWriteInt(p, level, followingSymbols);
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("writestr")))
        processWriteStr(p, level, followingSymbols);
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("writeln")))
        processWriteLn(p, level, followingSymbols);
    else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("readint")))
        processReadInt(p, level, followingSymbols);
    else if (sym == StreamTokenizer.TT_WORD)
        processAssign(p, level,followingSymbols);
    else {Main.loadText.appendText("Not at start of statement\n");}
    test(p, followingSymbols, emptySet, 18);
    if (Main.trace.getState()) Main.loadText.appendText("leaving statement\n");
}

int processConst (StreamTokenizer p, int level, int baseOffset) {
    int sym;
    String hashKey, token;
    HashTableValue entry;
    if (Main.trace.getState()) Main.loadText.appendText("entering processConst\n");
    sym=getSym(p);  // get first constant name
    hashKey = p.sval+"_" + Integer.toString(level);
    sym=getSym(p); // skip to =
    lookingFor(p,"=",/*"no = after constant name",*/ 0);
    if(p.ttype == StreamTokenizer.TT_NUMBER)
        symbolTable.put(hashKey, new HashTableValue(constant,level,(int)p.nval));
    else error(1);
    sym=getSym(p);
    token = getToken(p); //should be a semicolon or a comma
    while (token.equals(",")) {
        sym=getSym(p);  // get constant name
        hashKey = p.sval+"_" + Integer.toString(level);
        sym=getSym(p); // skip to =
        lookingFor(p,"=",/*"no = after constant name",*/0);
        symbolTable.put(hashKey, new HashTableValue(constant,level,(int)p.nval));
        sym=getSym(p);
        token = getToken(p);
    };
    lookingFor(p,";",/*"semicolon expected after const declaration",*/4);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processConst\n");
    return baseOffset;
}

int processVar (StreamTokenizer p, int level, int baseOffset) {
    int sym;
    String hashKey,hashKey2, token;
    HashTableValue entry,entry2;
    int arrayMaxSize = 0;
    if (Main.trace.getState()) Main.loadText.appendText("entering processVar\n");
    sym= getSym(p);  /* get first variable name */
    // get next token
    
    if (isIdentifier(p)) {
       hashKey = p.sval+"_" + Integer.toString(level);
       sym=getSym(p);
       token = getToken(p); //should be a semicolon or a comma or bracket
       if(token.equals("[")){
          sym = getSym(p);
          if(p.ttype == StreamTokenizer.TT_NUMBER){
             arrayMaxSize = (int)p.nval;
          }else{ 
             if(p.ttype == StreamTokenizer.TT_WORD){
                hashKey2 = p.sval + "_" + Integer.toString(level);
                entry2 = searchTable(hashKey2);
                if(entry2 == null){
                  error(10);
                } else {  
                   if(entry2.kind == constant){
                      arrayMaxSize = entry2.value;
                   } else {
                      error(29);
                   }
                }
             } else {
                error(29);
             }
         }
         symbolTable.put(hashKey, new HashTableValue(array,level,baseOffset, arrayMaxSize));
         baseOffset += arrayMaxSize;
         sym = getSym(p);
         lookingFor(p,"]",/* ] missing */28);
         
       } else {
          symbolTable.put(hashKey, new HashTableValue(variable,level,baseOffset++));
       }
    }
    token = getToken(p); //should be a semicolon or a comma or bracket 
    while (token.equals(",")) {
        // get token
         sym=getSym(p);  // get variable name
        if (isIdentifier(p)) {
            hashKey = p.sval+"_" + Integer.toString(level);
            sym=getSym(p);
            token = getToken(p); //should be a semicolon or a comma or bracket
            if(token.equals("[")){
               sym = getSym(p);
               if(p.ttype == StreamTokenizer.TT_NUMBER){
                  arrayMaxSize = (int)p.nval;
               }else{ 
                  if(p.ttype == StreamTokenizer.TT_WORD){
                     hashKey2 = p.sval + "_" + Integer.toString(level);
                     entry2 = searchTable(hashKey2);
                     if(entry2 == null){
                        error(10);
                     } else {  
                        if(entry2.kind == constant){
                           arrayMaxSize = entry2.value;
                        } else {
                           error(29);
                        }
                     }
                  } else {
                     error(29);
                  }
               }
               symbolTable.put(hashKey, new HashTableValue(array,level,baseOffset, arrayMaxSize));
               baseOffset += arrayMaxSize;
               sym = getSym(p);
               lookingFor(p,"]",/* ] missing */28);
            } else {
               symbolTable.put(hashKey, new HashTableValue(variable,level,baseOffset++));
            }
        } else error(3);
    //    sym=getSym(p);
        token = getToken(p);
    };
    lookingFor(p,";",/*"semicolon expected after var declaration",*/4);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processVar\n");
    return baseOffset;
}

void setBlockAddress(String hashKey, int val) {
    HashTableValue entry = (HashTableValue)symbolTable.get(hashKey);
    entry.value = val;
    symbolTable.put(hashKey,entry);
} // setBlockAddress


int processProc (StreamTokenizer p, int level, int baseOffset, Vector followingSymbols) {
    int sym;
    String hashKey;
    String ProcName;
    int numPram = 0;
    int MyBase = 3;
    if (Main.trace.getState()) Main.loadText.appendText("entering processProc\n");
    // get procedure name
    sym=getSym(p);
    ProcName = p.sval;
    sym=getSym(p);
    String token = getToken(p);
    if(token.equals("(")){
       sym=getSym(p);
       token = getToken(p);
       while(!token.equals(")")){
          if (isIdentifier(p)) { 
             hashKey = p.sval + "_" + Integer.toString(level + 1);
             symbolTable.put(hashKey, new HashTableValue(variable ,level + 1,MyBase));
             sym = getSym(p);
             token = getToken(p);
             if(token.equals(")")){ numPram++;MyBase++; break;}
             lookingFor(p,",",30);
             numPram++;
             MyBase++;
          }else { error(3); }
          //sym=getSym(p);
          token = getToken(p);
       }  
       lookingFor(p,")",21);
    }    
    ProcName = ProcName + "_" + Integer.toString(numPram) + "_" + Integer.toString(level);
    symbolTable.put(ProcName, new HashTableValue(procedure,level,baseOffset));
    lookingFor(p,";",/*"expected semicolon after procedure name",*/4);
    String[] S = {";"};

    block(p, level+1,ProcName, union(programSet,makeSet(S,1)),MyBase);
    Main.PL0.gen(opr,0,0);

    lookingFor(p,";",4);
    test(p,union(statBeginSys,declBeginSys), followingSymbols, 5);
    if (Main.trace.getState()) Main.loadText.appendText("leaving processProc\n");
    return baseOffset;
}

int processFunc (StreamTokenizer p, int level, int baseOffset, Vector followingSymbols) {
    int sym;
    String hashKey;
    String ProcName;
    int numPram = 0;
    int MyBase = 3;
    if (Main.trace.getState()) Main.loadText.appendText("entering processFunc\n");
    // get procedure name
    sym=getSym(p);
    ProcName = p.sval;
    sym=getSym(p);
    String token = getToken(p);
    if(token.equals("(")){
       sym=getSym(p);
       token = getToken(p);
       while(!token.equals(")")){
          if (isIdentifier(p)) { 
             hashKey = p.sval + "_" + Integer.toString(level + 1);
             symbolTable.put(hashKey, new HashTableValue(variable ,level + 1,MyBase));
             sym = getSym(p);
             token = getToken(p);
             if(token.equals(")")){ numPram++;MyBase++; break;}
             lookingFor(p,",",30);
             numPram++;
             MyBase++;
          }else { error(3); }
          //sym=getSym(p);
          token = getToken(p);
       }  
       lookingFor(p,")",21);
    } else { error(26); }
    ProcName = ProcName + "_" + Integer.toString(numPram) + "_" + Integer.toString(level);
    symbolTable.put(ProcName, new HashTableValue(function,level,baseOffset));
    lookingFor(p,";",/*"expected semicolon after procedure name",*/4);
    String[] S = {";"};
    block(p, level+1,ProcName, union(programSet,makeSet(S,1)),MyBase);
    Main.PL0.gen(opr,0,13);
    
    lookingFor(p,";",4);
    test(p,union(statBeginSys,declBeginSys), followingSymbols, 5);
    
    if (Main.trace.getState()) Main.loadText.appendText("leaving processFunc\n");
    return baseOffset;
}


void block (StreamTokenizer p, int level, String blockName, Vector followingSymbols) {
   block(p,level,blockName,followingSymbols,3);
}

void block (StreamTokenizer p, int level, String blockName, Vector followingSymbols, int baseOffset) {
    int sym;
    boolean moreDecl = true;
    if (Main.trace.getState()) Main.loadText.appendText("entering block\n");
    int backpatchJump = Main.PL0.codeindex();
    Main.PL0.gen(jmp,0,0);
    do { // process all declarations
        // get token
        sym = p.ttype; /* getSym(p); */
        if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("constant")))
            baseOffset = processConst(p, level,baseOffset);
        else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("var")))
            baseOffset = processVar(p, level,baseOffset);
        else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("procedure")))
            baseOffset = processProc(p,level,baseOffset,followingSymbols);
        else if ((sym == StreamTokenizer.TT_WORD) && (p.sval.equals("function")))
            baseOffset = processFunc(p,level,baseOffset,followingSymbols);
        else moreDecl = false;
    } while (moreDecl);
    setBlockAddress(blockName,Main.PL0.codeindex());
    Main.PL0.BackPatch(backpatchJump);
    Main.PL0.gen(inc,0,baseOffset);
    String[] S = {";","end"};
    statement(p, level,union(followingSymbols,makeSet(S,2)));
  //  Main.PL0.gen(opr,0,0);
    test(p, followingSymbols,emptySet,7);
    if (Main.trace.getState()) Main.loadText.appendText("leaving block\n");
}

 public void compile() {
    String program = Main.loadText.getText();
    StringBufferInputStream in = new StringBufferInputStream(program);
    StreamTokenizer p = new StreamTokenizer(in);
    Main.loadText.setText(""); // clear the editor

    p.lowerCaseMode(true);
    p.eolIsSignificant(true);
    p.whitespaceChars(0,32);
    p.ordinaryChar('/');
    p.slashStarComments(true);
    p.slashSlashComments(true);
    p.ordinaryChar('.');
    p.quoteChar('"');

    int sym=getSym(p);
    lookingFor(p,"program",24);
    String hashKey = p.sval+"_0";
    symbolTable.put(hashKey, new HashTableValue(procedure,0,0));
    // get semicolon and throw away
    sym=getSym(p);
    lookingFor(p,";",9);

    block(p,0,hashKey,programSet);
    Main.PL0.gen(opr,0,0);
    lookingFor(p,".",8);

     }// compile
} // class Parser

class instruction {
  instruction(int op, int lev, int val) { oper = op; level = lev; value = val;};
  int oper, level, value;
  }


public class Main extends Frame {

static final private int lit = 0;  // put literal on top of stack
static final private int opr = 1;  // perform an operation
static final private int lod = 2;  // put a variable on top of stack
static final private int sto = 3;  // put a value from top of stack to a variable
static final private int cal = 4;  // call a procedure
static final private int inc = 5;  // increment the stack pointer
static final private int jmp = 6;  // unconditional jump to specified code address
static final private int jpc = 7;  // conditional jump, requires 0 on top of stack
static final private int win = 8;  // write integer
static final private int wst = 9;  // write string
static final private int wln = 10; // write line
static final private int rin = 11; // read integer
static final private int sta = 12; // store array
static final private int lda = 13; // load array value
static final private int ret = 14; // return a value

static String operations[] = { "lit ", "opr ", "lod ", "sto ", "cal ", "inc ", "jmp ", "jpc ","win ","wst ","wln ","rin ","sta ","lda ","ret "};
static int runtime[] = new int[200];
static int TOS, CurrentBase, CodePointer;
static Vector literals = new Vector(100);

static instruction code[] = new instruction[300];

public int codeindex (){
  return CodePointer;
}

void storeLiteral(String litString) {
  literals.addElement(litString);
}

int getLiteralIndex() {
  return literals.size()-1;
}

void BackPatch(int index){
//  output.appendText("Back patching code at "+index+" with address "+CodePointer+"\n");
  code[index].value = CodePointer;
}

void Restart(){
  TOS= -1; CurrentBase= 0; CodePointer= 0;
  runtime[0]= 0; runtime[1]= 0; runtime[2]= 0;
}

void gen (int op, int lev, int val) {
  int index = CodePointer++;
  code[index]= new instruction(op, lev, val);
  if (block.getState()) printInstruction(code[CodePointer-1]);
}

void printInstruction(instruction inst) {
    output.appendText(operations[inst.oper] + "  "
         + String.valueOf(inst.level) + ", "
         + String.valueOf(inst.value) + "\n");
}

void printStack(int TOS) {
    int count;
    if (TOS == -1) output.appendText("empty");
    else for (count = TOS; count >= 0; count--)
            output.appendText(runtime[TOS - count] + " ");
    output.appendText("\n");
}

void processoperator(int oper) {
switch (oper) {
  case 0 : // return
        TOS = CurrentBase-1; CodePointer = runtime[TOS+3];
        CurrentBase = runtime[TOS+2];
        break;
  case 1  : runtime[TOS] = - runtime[TOS];break;
  case 2  : TOS = TOS-1; runtime[TOS] = runtime[TOS] + runtime[TOS+1];break;
  case 3  : TOS= TOS-1; runtime[TOS]= runtime[TOS] - runtime[TOS+1]; break;
  case 4  : TOS= TOS-1; runtime[TOS]= runtime[TOS] * runtime[TOS+1]; break;
  case 5  : TOS= TOS-1; runtime[TOS]= runtime[TOS] / runtime[TOS+1]; break;
  case 6  : runtime[TOS]= (runtime[TOS]%2 == 0) ? 0:1; break;
  case 7  : TOS= TOS-1;runtime[TOS]= (runtime[TOS] == runtime[TOS+1])?1:0;break;
  case 8  : TOS= TOS-1; runtime[TOS]= (runtime[TOS] != runtime[TOS+1])?1:0;break;
  case 9  : TOS= TOS-1; runtime[TOS]= (runtime[TOS] < runtime[TOS+1])?1:0;break;
  case 10 : TOS= TOS-1; runtime[TOS]= (runtime[TOS] >= runtime[TOS+1])?1:0; break;
  case 11 : TOS= TOS-1; runtime[TOS]= (runtime[TOS] > runtime[TOS+1])?1:0; break;
  case 12 : TOS= TOS-1; runtime[TOS]= (runtime[TOS] <= runtime[TOS+1])?1:0; break;
  case 13 : CodePointer = 0; output.appendText("RUNTIME ERROR : function must exit through return statemt"); break;

  }
}

int base (int NestingLevel) {
// find base NestingLevel levels down
int b1 = CurrentBase;
  while (NestingLevel > 0) {
    b1 = runtime[b1]; NestingLevel= NestingLevel-1;
  };
  return (b1);
}

void interpret(boolean afterRead, int readValue) {
  if (!afterRead) Restart();
  do {
    instruction Inst= code[CodePointer++];
    switch(Inst.oper) {
      case lit: TOS= TOS+1; runtime[TOS]= Inst.value; break;
      case opr: processoperator(Inst.value); break;
      case lod: TOS= TOS+1; runtime[TOS]= runtime[base(Inst.level)+Inst.value]; break;
      case sto: runtime[base(Inst.level)+Inst.value]= runtime[TOS]; TOS= TOS-1;
                if (store.getState()) {
                    output.appendText("storing " + runtime[TOS+1] + " at offset "
                    + Inst.value + " and link " + Inst.level + "\n");
                }
           break;
      case cal: runtime[TOS+1]= base(Inst.level); runtime[TOS+2]= CurrentBase; runtime[TOS+3]= CodePointer;
           CurrentBase= TOS+1; CodePointer= Inst.value; break;
      case inc: TOS= TOS+Inst.value;break;
      case jmp: CodePointer= Inst.value; break;
      case jpc: if (runtime[TOS] == 0) CodePointer = Inst.value; TOS= TOS-1; break;
      case win: runtime[TOS+1]= runtime[base(Inst.level)+Inst.value];
                output.appendText("" + runtime[TOS+1]); break;
      case wst: output.appendText((String)literals.elementAt(Inst.value)); break;
      case wln: output.appendText("\n"); break;
      case rin: if ((!afterRead)||(readValue == -7777)) {
                    output.appendText(">");
                    CodePointer--;  // back up to re-execute read again
                    afterRead = true;
                }
                else {
                    runtime[base(Inst.level)+Inst.value]= readValue;
                    afterRead = false;
                    if (store.getState()) {
                        output.appendText("storing " + runtime[TOS+1] + " at offset "
                        + Inst.value + " and link " + Inst.level + "\n");
                    }
                }
                break;
       case sta: 
          if((runtime[TOS-1] >= runtime[TOS-2]) || (runtime[TOS-1] < 0)){
             //Error ... Out of bounds
             //error(30);
             output.appendText("RUNTIME ERROR : Array index out of bounds.. HAULT");
             CodePointer = 0; // This knocks us out of the loop
          }else {
             runtime[base(Inst.level) + Inst.value + runtime[TOS-1]] = runtime[TOS];
             TOS = TOS- 3;
          }
          break;
       case lda: 
          if((runtime[TOS] >= runtime[TOS-1]) || (runtime[TOS] < 0)){
             //Error ... Out of bounds
             //error(30);
             output.appendText("RUNTIME ERROR : Array index out of bounds.. HAULT");
             CodePointer = 0; // This knocks us out of the loop
          }else {
             TOS--;
             runtime[TOS] = runtime[base(Inst.level) + Inst.value + runtime[TOS+1]];
          }
          break;
       case ret: 
          runtime[CurrentBase - 1] = runtime[TOS];
          break;
    }
    if (interp.getState()) printInstruction (Inst);
    if (stack.getState()) printStack(TOS);
   } while ((!afterRead) && (CodePointer != 0));
}


static public Main PL0;

static public Checkbox trace = new Checkbox("trace block");
static Checkbox block = new Checkbox("block code");
static Checkbox interp = new Checkbox("interp code");
static Checkbox store = new Checkbox("ztore only");
static Checkbox stack = new Checkbox("runtime ztack");
static Button load = new Button("Load file");
static Button save = new Button("Zave file");
static Button clear = new Button("Clear program");
static Button compile = new Button("Compile");
static Button execute = new Button("Execute");
static public TextArea loadText = new TextArea(20,40);
static public TextArea output = new TextArea(20,40);

static void makeButton(Container cont, Object arg,
       int x, int y, int w, int h, double weightx, double weighty) {
  GridBagLayout gbl = (GridBagLayout)cont.getLayout();
  GridBagConstraints c = new GridBagConstraints();
  Component comp;
  c.fill = GridBagConstraints.BOTH;
  c.gridx = x;
  c.gridy = y;
  c.gridwidth = w;
  c.gridheight = h;
  c.weightx = weightx;
  c.weighty = weighty;
  if (arg instanceof String) {
    comp = new Button((String)arg);
  } else {
    comp = (Component)arg;
  };
  cont.add(comp);
  gbl.setConstraints(comp, c);
}

Main() {
  super("PL/0 Compiler");
}

static void Layout(Frame f) {
  f.setLayout(new GridBagLayout());
  makeButton(f, loadText,0,0,4,14,0.0,0.0);
  makeButton(f, trace,4,0,1,1,0.0,1.0);
  makeButton(f, block,4,1,1,1,0.0,1.0);
  makeButton(f, interp,4,2,1,1,0.0,1.0);
  makeButton(f, store,4,4,1,1,0.0,1.0);
  makeButton(f, stack,4,5,1,1,0.0,1.0);
  makeButton(f, load,4,6,1,1,0.0,2.0);
  makeButton(f, save,4,7,1,1,0.0,2.0);
  makeButton(f, clear,4,8,1,1,0.0,2.0);
  makeButton(f, compile,4,9,1,1,0.0,2.0);
  makeButton(f, execute,4,10,1,1,0.0,2.0);
  makeButton(f, output,5,0,4,14,0.0,0.0);
  // This makes the little X in the right hand corner work
  f.addWindowListener(new WindowAdapter() {
     public void windowClosing(WindowEvent e) {System.exit(0);}
  });
  
  f.pack();
  f.show();
}

public boolean keyDown(Event evt, int key) {
    if ((evt.target == output) && (key == 10)) {
      evt.id = Event.ACTION_EVENT;
      evt.target = output;
      postEvent(evt);
      return true;
    } else return false;
} // keyUp

// need to find ways to change focus properly for file load/save
// and for interactive reading of integer values

public boolean action (Event evt, Object arg) {
if (evt.target == execute) {
     output.setText("");
     if (block.getState()) {
       int count;
       output.appendText("--- Block Code ---\n");
       for (count = 0; count < CodePointer; count++)
         printInstruction(code[count]);
     }
     interpret(false,0);
     return true;
  } else if (evt.target == load) {
    getInputName file = new getInputName();
    return true;
  } else if (evt.target == save) {
    getOutputName file = new getOutputName();
    return true;
  } else if (evt.target == clear) {
    Main.loadText.setText("");
    return true;
  } else if (evt.target == compile) {
    Parser P = new Parser();
    P.compile();
    if (P.errorPresent()) execute.disable();
    else execute.enable();
    return true;
  } else if (evt.target == output) {
    int number=0;
    String currentText = output.getText();
    String numText = currentText.substring(currentText.lastIndexOf('>')+1);
    try { number = Integer.parseInt(numText); }
    catch (Exception e) {
            output.appendText("\nbad input, try again");
            number = -7777;
            }; // catch
    interpret(true, number);
    return true;
  }
 return false;
 }

static public void main(String[] args) {
  PL0 = new Main();
  Layout(PL0);
}

}

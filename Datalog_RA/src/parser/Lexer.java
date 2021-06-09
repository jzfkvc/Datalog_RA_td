/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 *
 * @author jozef
 */
public class Lexer {
    private final PushbackInputStream input;
    private Token currToken;
    private String identifierStr;
    private int currChar;
    private int line, column;
    public Lexer(InputStream input){
        this.input = new PushbackInputStream(input);
        this.line = 1;
        this.column = 0;
        currToken = Token.unknown;
        identifierStr = "";
    }
    
    public int getLine(){
        return this.line;
    }
    public int getColumn(){
        return this.column;
    }
    public char getChar(){
        return (char) currChar;
    }
    public String getIdentifier(){
        return this.identifierStr;
    }
    public Token getToken(){
        return currToken;
    }
    /*
    akceptuje Tokeny v lubovolnom poradi alebo vracia unknown:
    predicate:=[a-zA-Z0-9_] + [(]
    literal:=[a-z]+[a-zA-Z-0-9_] or [0-9]
    variable:=[A-Z]+[a-zA-Z0-9_]
    lparen:= '('
    rparen:= ')'
    dot:= '.'
    coma:=','
    not:="\+"
    implication:=":-"
    //equals:= '='
    unknown := chyba
    //eof:= '~' pre ucel testovania pri citani so standardneho vstupu
    */
    
    public Token next(){
        identifierStr =  "";
        try{
            skipBlank();            
            nextChar();
            switch(currChar){
                case '(':
                    currToken = Token.lparen; 
                    identifierStr = "(";
                    break;
                case ')':
                    currToken = Token.rparen; 
                    identifierStr = ")";
                    break;       
                case ',':
                    currToken = Token.coma; 
                    identifierStr = ",";
                    break;
                case '.':
                    currToken = Token.dot; 
                    identifierStr = ".";
                    break; 
                /*case '=':
                    currToken = Token.equals; 
                    identifierStr = "=";
                    break;*/
                case 255 :
                    currToken = Token.eof; 
                    identifierStr = "-1";                    
                    return Token.eof;
                case '_':
                    currToken = Token.variable;
                    identifierStr = "_";
                    break;
                case ':':
                    find(':'); // hlada'-' ak sa nachadza ako bezprostrerdne 
                    break;     //dalsie vrati implikaciu
                case '\\' :
                    find('\\');  // hlada'+' ak sa nachadza ako bezprostrerdne 
                    break;       //dalsie vrati not
                /*case '~':  // eof pre ucely testovania ked cita standardny vstup
                    currToken = Token.eof;
                    break;*/
                default:
                    identifier(); // pokusi sa najst meno predikatu/premennej/konstanty
                    break;
                
            }
            
        } catch(IOException e){
            currToken = Token.eof;            
        }
        return currToken;
    }
    private void nextChar() throws IOException {
        currChar = input.read(); 
        if (currChar == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }       
               
    }
    /*
     skipne prazdne znaky a komentare typu '%' + "..." + '\n'
    */
    private void skipBlank() throws IOException{
        nextChar();
        while (currChar == ' ' || currChar == '\t' || currChar == '\n' || currChar == '%') {
            if (currChar == '%') {
                // skip comments
                while (currChar != '\n' && currChar != -1) {
                    nextChar();
                }
            } else {
                nextChar();
            }
        }
        input.unread(currChar);
        column--;
    }
    
    /*
    hlada meno predikatu/premennej/konstanty
    predikat ak nasleduje '('
    konstanta ak je cislo alebo zacina malym pismenom
    premenna zacina valkym pismenom
    */
    private void identifier() throws IOException{
        //moze byt identifikator
        if(!canBeIdentifier(currChar)){
            currToken = Token.unknown;
            identifierStr = "";
            identifierStr += (char) currChar;
            return;
        }
        //ak zacina cislom nacita iba cislice
        if(currChar >= '0' && currChar <= '9'){
            isNum();
            return;
        }
        //cita identifikator kym moze potom vrati posledny znak na vstup
        identifierStr = "";
        identifierStr += (char) currChar;
        nextChar();
        while(canBeIdentifier(currChar)){
            identifierStr += (char) currChar;
            nextChar();
        }
        // ak nasleduje '(' vrati predikat
        if(currChar == '('){
            currToken = Token.predicate;
            // podla prveho pismena vrati bud konstantu alebo premennu
        }else if(identifierStr.charAt(0) >= 'a' && identifierStr.charAt(0) <= 'z'){
            currToken = Token.literal;
        }else if(identifierStr.charAt(0) >= 'A' && identifierStr.charAt(0) <= 'Z'){
            currToken = Token.variable;
        }else{
            currToken = Token.unknown;
            identifierStr = "";
            identifierStr += (char) currChar;
            
        }
        // posledny precitany znak vrati na vstup
        input.unread(currChar);
        column--;
    }
    /*
    kontroluje ci znak moze byt sucastou identifikatora
    */
    private Boolean canBeIdentifier(int a){        
       return (a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z')
               || (a == '_') || (a >= '0' && a <= '9');
    }
    
    
    /*
    pre ciselne konstanty:
    ak nico zacina cislicou, 
    precita vsetky cislice co nasleduju a vrati konstantu
    */
    private void isNum() throws IOException{
        identifierStr = "";
        identifierStr += (char) currChar;
        nextChar();
        while(currChar >= '0' && currChar <= '9'){
            identifierStr += (char) currChar;
            nextChar();
        }
        currToken = Token.literal;
        input.unread(currChar);
        column--;
    }
    
    /*
    kontroluje spravnost implikacie a not
    */
    private void find(char a) throws IOException{
        nextChar();
        switch (a) {
            case ':':
                if(currChar == '-') {
                    currToken = Token.implication;
                    identifierStr = ":-";
                }
                       else {
                    currToken = Token.unknown;
                    identifierStr = ":";
                    identifierStr += (char) currChar;
                    input.unread(currChar);
                    column--;
                    //input.close();
                }
                
                break;
            case '\\':
                if(currChar == '+') {
                    currToken = Token.not;
                    identifierStr = "\\+";
                }
                else{
                    currToken = Token.unknown;
                    identifierStr = "\\";
                    identifierStr += (char) currChar;
                    input.unread(currChar);
                    column--;
                    //input.close();
                }
                break;
            default:
                currToken = Token.unknown;
                identifierStr = "";
                identifierStr += (char) currChar;
                input.unread(currChar);
                column--;
                break;
        }
        
        
    }
    
}

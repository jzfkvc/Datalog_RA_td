/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dataStructures.*;

/**
 *
 * @author jozef
 */
public class Parser {
    private final Lexer lexer;
    private final InputStream input;
    Token token;
    private final Map<String, List<List<String>>> facts;
    private Map<String, Relation> relations;
    
    public Parser(InputStream input){
        this.input = input;
        lexer = new Lexer(input);
        this.token = null;
        facts = new HashMap<>();
    }
    
    public Map<String, Relation> getFacts(){
        return relations;
    }
    
    public void parse() throws ParseException{
        // <program> : <fact> {’,’ <fact>} | <fact> {’,’<fact>}  <rule> {’,’<rule>}  ;
        while(this.token != Token.eof){
            fact_head();
        }
    }
    
    private void fact_head() throws ParseException{
        //hlava : predicate lparen {arguments} rparen
        String predicate;
        token = lexer.next();
        if(this.token == Token.eof){
            relations = new HashMap<>();
            for(String p : facts.keySet()){
                Relation r = new Relation(facts.get(p));
                relations.put(p, r);
            }//TO DO generuj kod
            
            return;
        }
        if(!isExpected(Token.predicate)){// predicate
            expect(Token.predicate);
        }
        predicate = lexer.getIdentifier();//TO DO uloz identifikator        
        token = lexer.next();
        if(!isExpected(Token.lparen)){//lparen
            expect(Token.lparen);
        }
        
        List<String> arg = arguments();
        
        token = lexer.next();
        switch (token) {
            case dot:
                if(facts.containsKey(predicate)){
                    List<List<String>>f = facts.get(predicate); 
                    f.add(arg);
                    facts.replace(predicate, f);
                }else {
                    List<List<String>> a = new ArrayList<>();
                    a.add(arg);
                    facts.put(predicate, a);
                }
                return;
            case implication:
                body();
                break;
            default:
                expect(Token.dot, Token.implication);
                break;
        }
        
    }
    
    private List<String> arguments() throws ParseException{
        //<argument> : <literal> | <variable> {',' <litral> | <variable>};
        List<String> arg = new ArrayList<>();        
        while(this.token != Token.rparen){
            token = lexer.next();
            switch (token) {
                case literal:
                    arg.add(lexer.getIdentifier());//TO DO uloz identifikator
                    break;
                case variable:
                    arg.add(lexer.getIdentifier());//TO DO uloz identifikator
                    break;
                case rparen:
                    return arg;
                default:
                    expect(Token.literal, Token.variable, Token.rparen);
                    break;
            }
            token = lexer.next();
            switch (token) {
                case coma:
                    break;
                case rparen:
                    return arg;
                default:
                    expect(Token.coma, Token.rparen);
                    break;
            }
        }
        return null;
    }
    
    private void body() throws ParseException{
        //<telo> : <atom>{’,’<atom>}
        //<atom> : <predicate>  ’(’ [<argument>{’,’<argument>}] ’)’  | ’/+’ <atom>\
        String predicate;
        Boolean not = false;
        token = lexer.next();
            if(!isExpected(Token.predicate)){
                expect(Token.predicate);
            }
            predicate = lexer.getIdentifier();//TO DO uloz identifikator
            token = lexer.next();
            if(!isExpected(Token.lparen)){
                expect(Token.lparen);
            }
        while(this.token != Token.dot){          
          
            List<String> arg = arguments();
            
            token = lexer.next();
            switch (token) {
                case dot:
                    return;
                case coma:
                    break;
                default:
                    expect(Token.dot, Token.coma);
                    break;
            }
            token = lexer.next();
            switch (token) {
                case predicate:
                    predicate = lexer.getIdentifier();//TO DO uloz identifikator
                    token = lexer.next();
                    if(!isExpected(Token.lparen)){
                        expect(Token.lparen);
                    }   
                    break;
                case not:
                    not = true;
                    token = lexer.next();
                    if(!isExpected(Token.predicate)){
                        expect(Token.predicate);
                    }   
                    predicate = lexer.getIdentifier();//TO DO uloz identifikator
                    token = lexer.next();
                    if(!isExpected(Token.lparen)){
                        expect(Token.lparen);
                    }   
                    break;
                default:
                    expect(Token.not, Token.predicate);
                    break;
            }
            
        }
    }
    
    private Boolean isExpected(Token t){
        return t == this.token;
    }
    
    public class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
    
    private void expect(Token... tokens) throws ParseException {
        /*for (Token t : tokens) {
            if (lexer.getToken() == t) {
                return t;
            }
        }*/
        StringBuilder errMsg = new StringBuilder();
        errMsg.append(lexer.getLine());
        errMsg.append(':');
        errMsg.append(lexer.getColumn());
        errMsg.append(": unexpected ");
        errMsg.append(lexer.getIdentifier());
        errMsg.append(", expected ");
        for (int i = 0; i < tokens.length; i++) {
            if (i == 0) {
                //
            } else if (i == tokens.length - 1) {
                errMsg.append(" or ");
            } else {
                errMsg.append(", ");
            }
            errMsg.append(tokens[i].toString());
        }
        throw new ParseException(errMsg.toString());
    }
    /*
    <program> : <fact> {’,’ <fact>} | <fact> {’,’<fact>}  <rule> {’,’<rule>}  ;

    <fact> : <predicate>  ’(’  [<literal> {’,’<literal>}]  ’)’ ’.’  ;

    <rule> : <hlava> ’:-’  <telo> ’.’ ;

    <telo> : <atom>{’,’<atom>}

    <hlava> : <predicate> ’(’ [<argument> {’,’<argument>}] ’)’

    <atom> : <predicate>  ’(’ [<argument>{’,’<argument>}] ’)’  | ’/+’ <atom>

    <argument> : <literal> | <variable> ;

    tokens = {’(’, ’)’, ’variable’, ’literal’, ’predicate’, ’/+’, ’:-’, ’,’ ’.’, ’eof’, ’unknown’, ’=’}
    // pre nularne predikaty :  <predicate> ’(’ ’)’ 
    */
}

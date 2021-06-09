/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

/**
 *
 * @author jozef
 */
public enum Token {
    predicate,   // [a-zA-Z]+[a-zA-Z0-9_] + [(]
    literal,     // [a-z]+[a-zA-Z-0-9_] or [0-9]
    variable,    // [A-Z]+[a-zA-Z0-9_]
    lparen,      // '('
    rparen,      // ')'
    dot,         // '.'
    coma,        // ','
    not,         // "\+"
    implication, // ":-"
    //equals,      // '='
    unknown,     // 
    eof          
    
}

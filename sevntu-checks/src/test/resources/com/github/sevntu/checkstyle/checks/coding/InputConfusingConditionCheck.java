package com.github.sevntu.checkstyle.checks.coding;
import java.io.IOException;

public class InputConfusingConditionCheck
{
  //all with "else" block and negation within an "if"
    void Main(){
        boolean a = false, b = false, c = false, d = false;
                
        if (!a) {b=a;}
        else{a=b;}
        
        if (!a && !b){b=a;}
        else{a=b;}
        
        if (a != b) {b=a;}
        else{a=b;}
        
        if (a != b && c!= d) {b=a;}
        else{a=b;}
        
        if (!a || !b) {b=a;}
        else{a=b;}
    }
  //all with "else" block and one negation within an "if"
    void Second(){
        boolean a = false, b = false, c = false, d = false;
        
        if (!a && b){b=a;}
        else{a=b;}
        
        if (a != b && c == d) {b=a;}
        else{a=b;}
        
        if (!a || b) {b=a;}
        else{a=b;}
    }
  //all with "else" block but without negation within an "if"
    void withoutNegation(){
        boolean a = false, b = false, c = false, d = false;

        if (a) {b=a;}
        else{a=b;}
        
        if (a && b){b=a;}
        else{a=b;}
        
        if (a == b) {b=a;}
        else{a=b;}
        
        if (a == b && c== d) {b=a;}
        else{a=b;}
        
        if (a || b) {b=a;}
        else{a=b;}
    }
    
  //all without "else" block and without negation within an "if"
    void withoutElse(){
        boolean a = false, b = false, c = false, d = false;

        if (a) {b=a;}
        
        if (a && b){b=a;}
        
        if (a == b) {b=a;}
        
        if (a == b && c== d) {b=a;} 
        
        if (a || b) {b=a;}
    }
    
  //all without "else" block and with negation within an "if"
    void withoutElse1(){
        boolean a = false, b = false, c = false, d = false;

        if (!a) {b=a;}
        
        if (!a && !b){b=a;}
        
        if (!a == !b) {b=a;}
        
        if (a != b && c != d) {b=a;} 
        
        if (!a || !b) {b=a;}
    }
  //Hard cases
    void hardIf() throws IOException {
        boolean a = false, b = false, c = false, d = false; Object r = null;
        if(!a || (!b && !a) && b || a){}
        else{}
        
        if(!a && b && !a ){}
        else{}
        
        if(!a || b || !a ){}
        else{}
        
        if(!a && b || !a ){}
        else{}
        
        if((a != b) && b || !a ){}
        else{}
        
        if((a != b) && (a != b) || (a != b)){}
        else{}
        
        if((a != b) && (a != b) && (a != b)){}
        else{ }
        
        if((a != b) || (a != b) || (a != b)){}
        else{}
        
        if(!a || !a || !b){
            if(a){
            }
            else{
                
            }
        }
        //
        if (a) {
            for (int i : new int[3]) {
                if (a) {}
                else if (a) 
                { 
                    if (!b) {}
                    else if (b){}   
                }
            }
        }
        
        // The if-then-else Statement       
        if (!a) {}
        else if (a) {} 
        
        //if () {} else { throw ;} case
        if (!a) {}
        else 
        {
            throw new IOException();
        }
        
        //null with in a if
        if (r != null){}
        else {}
        
        
        if (a != a){}
        else 
        {
            if(a){}
        }
        
        
        //
        if (r != null) {}
        else {}
        
        if (r != null) {}
        else {}
        if (r != null) {}
        else {}
        
        //
        if (!a) 
        {
            a = true;
        }
        else {
            a = true;
            a = true;
        }
        
       ///
        if (a) {}
        else if (!a) {}
        else {}
        
        ///
        if (!a) 
        {
            a = true;
        }
        else {
            if (a) {
                a = true;

                if (b) {
                    a = true;
                }
            } else {
                a = true;
                a = true;
            }
        }
        /////
        
        if (!a) { 
            if (!a) { /// WARNING HERE IS OK
                a = true;
                } else {
                    a = true;
                    }
        } else {
            a = true;
            if (a) {
                a = true;
                } else {
                    a = true;
                    }
            
        }
        
        if (!a)
        {
            a = true;
        }
        else
        {
            
            
            
            a = true;
            }
        ////////////////
        if (r != null) {
            a = true;
        }
        else {
            if (!a) { 
                // pop from stack and recurse
                a = true;
                a = true;
            }
            else {
                a = true;
            }
        }
        
        if (!(!a && !b)) { 
        }
        else {
            a = true;
        }
        
        if (!a) {
            //
        }
        else if (!b) {
            a = true;
        }
        else {
            //
        }
        
        if (!a) 
            ;
        else if (!b) 
            ;
        else 
            ;
        
    }
}
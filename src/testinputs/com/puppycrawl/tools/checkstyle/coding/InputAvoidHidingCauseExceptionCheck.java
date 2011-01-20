package com.puppycrawl.tools.checkstyle.coding;

import java.rmi.AccessException;

public class InputAvoidHidingCauseExceptionCheck
{
    public void Simple()
    {
        RuntimeException r;
        try {
        }
        catch (ClassCastException  e) {
            //your code
        }

        catch (IndexOutOfBoundsException e) {
            //your code
            throw new RuntimeException();  // !
        }
        
        catch (IllegalStateException e) {
            //your code
            throw new RuntimeException("Runtime Ecxeption!"); // !
        }
        
        catch (java.lang.ArithmeticException e) {
            //your code
            throw new RuntimeException("Runtime Ecxeption!",e);
        }

        catch (RuntimeException e) {
            //your code
            throw e;
        }
        
        catch (java.lang.Exception e) {
            //your code
            throw r;  // !
        } 
               
    }
    
    public void Stronger()
    { 
        boolean x=false;
        RuntimeException r;
        try {       
        }
        catch (ClassCastException  e) {
            if (x) {
                while (x) {
                    do {
                    int k = Integer.numberOfLeadingZeros(100000);                        
                    } while(x);
                    }
                }
        }

        catch (IndexOutOfBoundsException e) { 
            //your code
            if(x&x|!x){
                while(!!!!!!!!x){
                    for(int ee=0;ee<10;ee++)
            throw new RuntimeException(); // !
                }
            }
        }
        
        catch (IllegalStateException e) {
            while(!!!!!!!!x){
                x=!!!!!!!!false & !!!!!!!!true;
            double kkk = Math.pow(5, 25555555);
            int ee=(int)kkk;
            throw new RuntimeException("Runtime Ecxeption!"); // !            
            }
        }
        
        catch (java.lang.ArithmeticException e) {
            int []err=new int [50];
            if(err[51]==0){ err[999]++; }
            throw new RuntimeException("Runtime Ecxeption!",e);
        }

        catch (RuntimeException e) {
        for(int a=0, b=3; a<6*a+b; a+=a-2){
            throw e;
        }            
        }
        
        catch (java.lang.Exception e) { 
            int []err=new int [50];
            int []err2=new int [50];
            for(int m:err2){
            throw r; // !
            }
        }
       
        
    }

}
package com.github.sevntu.checkstyle.checks.coding;

public class InputUselessSuperCtorCall7
{
    class Base
    {
        public Base() {}
        
        public Base(int i) { }
    }
    
    class DerivedOne extends Base
    {
        public DerivedOne() {
            super();
        }
        
        public DerivedOne(int i) {
            super(i);
        }
    }
    
    class DerivedTwo extends Base
    {
        public DerivedTwo() {
            super();
        }
        
        protected DerivedTwo(int i) {
            super(i);
        }
    }
}

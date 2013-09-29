
    /**  
     * All coordinates which appear as arguments to the methods of this
     * Graphics object are considered relative to the translation origin
     * of this Graphics object prior to the invocation of the method.
     * All rendering operations modify only pixels which lie within the
     * area bounded by both the current clip of the graphics context
     * and the extents of the Component used to create the Graphics object.
     * 
     * @author      Sami Shaio
     * @author      Arthur van Hoff
     * @version     %I%, %G%
     * @since       1.0
     */                                                                 //true
    public abstract class Graphics {

                                                                        //fail
        public abstract boolean drawImage(Image img, int x, int y, 
                                          ImageObserver observer);


        /**
         * Graphics objects which are provided as arguments to the paint
         * and update methods of Components are automatically disposed
         * by the system when those methods return.  Programmers should,
         * for efficiency, call the dispose method when finished using
         * a Graphics object only if it was created directly from a
         * Component or another Graphics object.
         *
         * @see       #create(int, int, int, int)
         * @see       #finalize()
         * @see       Component#getGraphics()
         * @see       Component#paint(Graphics)
         * @see       Component#update(Graphics)
         * @since     1.0
         */
        public abstract void dispose();

        /**
         * Disposes of this graphics context once it is no longer 
         * referenced.
         *
         * @see       #dispose()
         * @since     1.0
         */
        public void finalize() {
            dispose();
        }
    }
    
    
    public class Animal {
        
        public void speak() {
        }    
    }
     
    public class Cat extends Animal {
     
        @Override // true
        public void speak() { 
           System.out.println("Meow."); 
        }       
    }
    
    public class dog extends Animal {
        
        public void speak() { // wrong
            System.out.println("WooF.")
        }
    }
    
    public class Maths{
        /**
         * Disposes of this graphics context once it is no longer 
         * referenced.
         *
         * @see       #dispose()
         * @since     1.0
         */         //true
        public static final double x = 2.998E8;
        }
    
    public class Maths{
                // fail
        public static final double x = 2.998E8;
        }
    
    


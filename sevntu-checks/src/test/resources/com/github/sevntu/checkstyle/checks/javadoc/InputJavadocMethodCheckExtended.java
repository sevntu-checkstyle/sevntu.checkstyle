import main.Animal;




public class ImportJavadocMethodCheckExtended
{
    public abstract class Graphics
    {
        public abstract boolean drawImage(Image img, int x, int y,
                ImageObserver observer);

        public abstract void dispose();

        /**
         * Disposes of this graphics context once it is no longer referenced.
         * @see #dispose()
         * @since 1.0
         */
        public void finalize()
        {
            dispose();
        }
    }
    

    public class Animal
    {

        public void speak()
        {
        }
    }

    public class Cat extends Animal
    {
        @Override
        public void speak()
        {
            System.out.println("Meow.");
        }
    }

    public class dog extends Animal
    {
        
        public void speak() { // fail
                System.out.println("WooF.");
            }
    }

        public final class MaClass {
            public final double PI = 3.14159; 
            public final double[] tailles = {50.2, 60.8};
         
            public void uneMethode() {
                tailles[0] = 99; 
            }
        }
}

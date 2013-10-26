package com.github.sevntu.checkstyle.checks.coding;

class Ok_1
{
    private int field;
    private double x;
    private boolean visible;
    
    public int getField() {
        return field;
    }

    public void setField(int field){
        this.field = field;
    }
    
    public double getX() {
        return x;
    }    
    
    public void setX(double x) {
        this.x = x;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void method(){
    }
}
//=========================================================================
//=========================================================================
//=========================================================================
class Errors_1
{
    private int field;
    private double x;
    private boolean visible;
    
    public void method(){
    }
    
    // wrong order
    public int getField() {
        System.out.println(); return field;
    }
    
    // wrong order
    public void setField(int field){
        this.field = field;
    }
    
    // wrong order
    public double getX() {
        return x;
    }    

    // wrong order
    public void setX(double x) {
        this.x = x;
    }
    
    // wrong order
    public boolean isVisible() {
        return visible;
    }
    
    // wrong order
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
//=========================================================================
//=========================================================================
//=========================================================================
class Errors_2
{
    private int field;
    private double x;
    private boolean visible;

    //wrong order
    public void setField(int field){
        this.field = field;
    }
    
    public int getField() {
        return field;
    }
    
    public double getX() {
        return x;
    }    
    
    public void setX(double x) {
        this.x = x;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void method(){
    }
}
//=========================================================================
//=========================================================================
//=========================================================================
class Errors_3
{
    private int field;
    private double x;
    private boolean visible;

    // wrong order
    public void setField(int field){
        this.field = field;
    }
    
    // wrong order
    public void setX(double x) {
        this.x = x;
    }
    
    // wrong order
    public void setVisible(boolean visible) {
        log.info("Visible is " + visible); this.visible = visible;
    }
    
    public double getX() {
        return x;
    }    
    
    public boolean isVisible() {
        return visible;
    }
    
    public int getField() {
        return field;
    }
    
    public void method(){
    }
}
//=========================================================================
//=========================================================================
//=========================================================================
class Errors_4
{
  private int field;
  private double x;
  private boolean visible;

  // wrong order
  public void setField(int field){
      this.field = field;
  }
  
  public void method(){
  }
  
  // wrong order
  public void setX(double x) {
      this.x = x;
  }
  
  public int getField() {
      return field;
  }
}
//=========================================================================
//=========================================================================
//=========================================================================
interface SetterI {
    void setValue(Object value);
    Object getValue();
}
abstract class Setter<T> {
    abstract void setValue(T value);
    abstract T getValue();
}
//=========================================================================
//=========================================================================
//=========================================================================
// There are no getters or setters.
class ASD{
    private int asd;
    
    public int getItIsNotGetter() {
        if (true) {
            System.out.println("Inside");
        } else {
            System.out.println();
        }
        return asd;
    }
    
    public void simpleMethod(){}
    
    public void setItIsNotSetter(int asd) {
        this.asd = asd;
        System.out.println("asd: " + asd);
    }
    
}
//=========================================================================
//=========================================================================
//=========================================================================
class Check {
    private void findGetterSetter(DetailAST aMethodDefAst){
        
    }
    
    private boolean isInFoundGetters (String aMethodName) {
        boolean result = false;
        for (DetailAST methodAst: mGetters.peek()) {
            String methodName = getIdentifier(methodAst);
            if (methodName.equals(aMethodName)) {
                result = true;
            }
        }
        return result;
    }
}
//=========================================================================
//=========================================================================
//=========================================================================
class LocalVariable {

	public void checkDate() {
	}
	
	// it is not a getter
	public Comparator getComparator() {
		Comparator comparator = new Comparator();
		return comparator;
	}
	
	// it is not a setter
	public void setComparator(Comparator newComparator) {
		Comparator comparator;
		comparator = newComparator;
	}
	
	public void main(){
	}
}
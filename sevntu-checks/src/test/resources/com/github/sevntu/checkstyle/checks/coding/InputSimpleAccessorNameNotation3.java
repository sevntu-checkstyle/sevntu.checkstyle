public class InputSimpleAccessorNameNotation3
{
    private String field1;
    private int mField2;

    public void setFiel1(String aField1) // !! in both case
    {
        field1 = aField1;
    }

    public String getFiel1() // !! in both case
    {
        return field1;
    }

    public void setFie2(String aField2) // !! in both case
    {
        mField2 = aField2;
    }

    public int getFiel2() // !! in both case
    {
        return mField2;
    }

}

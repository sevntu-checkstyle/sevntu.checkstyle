package com.github.sevntu.checkstyle.checks.coding;

import java.util.LinkedList;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;

/**<p> This check prevents calls of overridable methods in constructor body.
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class OverridableMethodInConstructorCheck extends Check {

    /**
     * A key to search the warning message text in "messages.properties" file.
     * */
    private final String mKey = "overridable.method.in.constructor";

    /**
     * A list contains all names of operands, which are used in the current
     * expression, which calculates with using "|", "&", "|=", "&=" operators.
     * */
    private LinkedList<DetailAST> mOverridableMethodsList = new LinkedList<DetailAST>();
    
    /**
     * A list contains all names of operands, which are used in the current
     * expression, which calculates with using "|", "&", "|=", "&=" operators.
     * */
    private LinkedList<MethodCalledFromCtor> mMethodCalledInConstructorList = new LinkedList<MethodCalledFromCtor>();

    @Override
    public final int[] getDefaultTokens() {
        return new int[] { TokenTypes.METHOD_CALL, TokenTypes.METHOD_DEF };
    }

    @Override
    public final void visitToken(final DetailAST aDetailAST) {
//        System.out.println(aDetailAST);

        if (aDetailAST.getType() == TokenTypes.METHOD_CALL) { // for METHOD_CALL nodes

            if (aDetailAST.getFirstChild() != null
                    && aDetailAST.getFirstChild().getType() != TokenTypes.DOT) {

                DetailAST currentNode = aDetailAST;

                while (currentNode != null
                        && currentNode.getType() != TokenTypes.METHOD_DEF
                        && currentNode.getType() != TokenTypes.CTOR_DEF
                        && currentNode.getType() != TokenTypes.CLASS_DEF) {
                    currentNode = currentNode.getParent();
                }

                if (currentNode.getType() == TokenTypes.CTOR_DEF) {
                    DetailAST curClass = currentNode.getParent().getParent();
                    mMethodCalledInConstructorList
                            .add(new MethodCalledFromCtor(aDetailAST,
                                    currentNode, curClass));
                }

            }
        }

        else { // for METHOD_DEF nodes

            DetailAST modifiers = aDetailAST
                    .findFirstToken(TokenTypes.MODIFIERS);

            boolean hasPrivateModifier = false;
            if (modifiers != null && modifiers.getChildCount() != 0) {

                for (DetailAST curNode : getChildren(modifiers)) {

                    if (curNode.getType() == TokenTypes.LITERAL_PRIVATE) {
                        hasPrivateModifier = true;
                    }

                }

            }

            if (!hasPrivateModifier) {
                mOverridableMethodsList.add(aDetailAST);
            }

        }

    }

    @Override
    public void finishTree(DetailAST aRootAST) {

        //  System.out.println("Overridable Methods List: " + mOverridableMethodsList);
        //  System.out.println();

        //  System.out.println("Methods are Called In C-tor: ");
//        int o=0;
//        for(MethodCalledFromCtor next: mMethodCalledInConstructorList){
//            o++;
//            System.out.println(
//                    "Method " + o + ": Method node: " + next.methodNode.toString() + ", ctor node: " +
//                    next.ctorNode.toString()  + ", classNode: " + next.classNode.toString());
//        }

        for (MethodCalledFromCtor curMethod : mMethodCalledInConstructorList) {

            String curMethodName = curMethod.methodNode.findFirstToken(
                    TokenTypes.IDENT).getText();

            for (DetailAST curOverridableMethod : mOverridableMethodsList) {

                String curOverridableMethodName = curOverridableMethod
                        .findFirstToken(TokenTypes.IDENT).getText();

                String a = curMethod.classNode.toString();
                String b = getClass(curOverridableMethod).toString();
                
                if (curMethodName.equals(curOverridableMethodName)
                        && a.equals(b)) {
                    log(curMethod.methodNode, mKey, curMethodName);
//                    System.out.println("Error!");
                }

            }

        }

//        for(DetailAST curNode: mOverridableMethodsList) {
//            
//            DetailAST currentNode = curNode;
//            
//            while (currentNode != null
//                    && currentNode.getType() != TokenTypes.CLASS_DEF)
//            {
//                currentNode = currentNode.getParent();
//            }
//
////            if(currentNode.getType() != null
////                    && 
////                    ){
//
//           // }
//            
//        }

    }

    public DetailAST getClass(DetailAST aMethodNode) {
        DetailAST result = null;
        DetailAST currentNode = aMethodNode;

        while (currentNode != null
                && currentNode.getType() != TokenTypes.CLASS_DEF) {
            currentNode = currentNode.getParent();
        }

        if (currentNode != null
                && currentNode.getType() == TokenTypes.CLASS_DEF) {
            result = currentNode;
        }

        return result;
    }

    public class MethodCalledFromCtor {

        protected DetailAST methodNode;
        protected DetailAST ctorNode;
        protected DetailAST classNode;

        MethodCalledFromCtor(DetailAST aMethodNode, DetailAST aCtorNode,
                DetailAST aClassNode) {
            this.methodNode = aMethodNode;
            this.ctorNode = aCtorNode;
            this.classNode = aClassNode;
        }

    }

    /**
     * Gets all the children one level below on the current top node.
     * 
     * @param aNode - current parent node.
     * @return an array of children one level below on the current parent node
     *         aNode.
     */
    public final LinkedList<DetailAST> getChildren(final DetailAST aNode) {
        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();

        DetailAST currNode = aNode.getFirstChild();

        while (currNode != null) {
            result.add(currNode);
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}

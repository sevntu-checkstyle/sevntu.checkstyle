
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.sql.rowset.Predicate;
import javax.swing.tree.TreePath;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.ShadowMatch;
import org.springframework.beans.factory.BeanFactory;

import testforbid.Classifier;
import testforbid.JointClassification;

public class InputDiamondOperatorForVariableDefinitionCheck {
    static Map<String, List<String>> myMap1 = new TreeMap<String, List<String>>();
    List<Integer> list =
            new ArrayList<Integer>();
    Map<String, List<String>> myMap = new HashMap<>();
    List<Character> abc = new LinkedList<Character>();
    private TreePath[] suppressParentPaths(TreePath[] paths) {
        List<TreePath> selectedPaths = Arrays.asList(paths);
        Collections.sort(selectedPaths, new Comparator<TreePath>() {
            @Override
            public int compare(TreePath o1, TreePath o2) {
                int count1 = o1.getPathCount();
                int count2 = o2.getPathCount();
                if (count1 < count2) {
                    return -1;
                }
                if (count1 == count2) {
                    return 0;
                }
                return 1;
            }
        });
        return paths;
     }
    
    
    private static class DemoAttributeSet implements AttributeSet {
        public Enumeration<?> getAttributeNames() {
            return (Enumeration<?>) new ArrayList<String>();
        }

    }
    
    private TreePath[] suppressParentPaths1(TreePath[] paths) {
        List<TreePath> selectedPaths = Arrays.asList(paths);
        Collections.sort(selectedPaths, new Comparator<TreePath>() {
            @Override
            public int compare(TreePath o1, TreePath o2) {
                int count1 = o1.getPathCount();
                int count2 = o2.getPathCount();
                if (count1 < count2) {
                    return -1;
                }
                if (count1 == count2) {
                    return 0;
                }
                return 1;
            }
        });
     
    
    Object statusArray[] = null;
    Arrays.sort(statusArray, new Comparator<Object>() {
        public int compare(Object s1, Object s2) {
            return 1;
        }
    });
    return paths; }

    List<Number> list = new LinkedList<Integer>();
    
    public class XReqLib {
        private static final String SLASH = "/";

        private final String base;

        public XReqLib(String base) {
            if (base.endsWith(SLASH)) {
                this.base = base.substring(0, base.length() - 1);
            } else {
                this.base = base;
            }
        }

        public String contextPath(String path) {
            if (path.startsWith(SLASH)) {
                return base + SLASH + path.substring(1);
            } else {
                return base + SLASH + path;
            }
        }

    }
    
    private Classifier<CharSequence, JointClassification> classifier;
    
    public void MnaNewsClassifier() {
        // see AbstractExternalizable.readObject(File)

        InputStream in = getClass().getResourceAsStream("mna-model.bin");
        if (in == null) {
            throw new IllegalStateException("cannot find mna-model.bin in " + getClass().getPackage().getName());
        }
        try {
            try {
                ObjectInputStream objIn = new ObjectInputStream(in);
                Object obj = objIn.readObject();
                this.classifier = (Classifier<CharSequence, JointClassification>) obj;
            } finally {
                in.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException("failed to load classifier: " + e, e);
        }
    }
    
    public boolean isMna(CharSequence text) {
        JointClassification jc = this.classifier.classify(text);
        String bestCategory = jc.bestCategory();
        return "mna".equals(bestCategory);
    }
    
    private Class<?> pointcutDeclarationScope;

    private Class<?>[] pointcutParameterTypes = new Class<?>[0];

    private BeanFactory beanFactory;

    private transient ClassLoader pointcutClassLoader;

    private transient PointcutExpression pointcutExpression;

    private transient Map<Method, ShadowMatch> shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);
    
}
    
    

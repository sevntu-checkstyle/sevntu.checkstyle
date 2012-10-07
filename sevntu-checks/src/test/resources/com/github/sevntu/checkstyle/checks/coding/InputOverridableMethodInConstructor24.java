import java.lang.reflect.Method;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.tuple.Instantiator;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.property.Getter;
import org.hibernate.property.Setter;

public abstract class InputOverridableMethodInConstructor24 implements ComponentTuplizer {

	protected abstract Getter buildGetter(Component component, Property prop);

    protected InputOverridableMethodInConstructor24(Component component) {

          buildGetter(component, prop);

    }

}

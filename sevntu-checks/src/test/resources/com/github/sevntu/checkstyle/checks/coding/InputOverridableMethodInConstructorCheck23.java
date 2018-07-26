package com.github.sevntu.checkstyle.checks.coding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Use Hibernate metadata to ignore cascade on entities.
 * cascade on embeddable objects or collection of embeddable objects are accepted
 *
 * Also use Hibernate's native isInitialized method call.
 * 
 * @author Emmanuel Bernard
 */
public class InputOverridableMethodInConstructorCheck23 {
	private Set<String> associations;

	public InputOverridableMethodInConstructorCheck23(
			String[] persister,
			HashMap<String, Set<String>> associationsPerEntityPersister, 
			String factory) {
		this.associations = associationsPerEntityPersister.get( persister );
		if (this.associations == null) {
			this.associations = new HashSet<String>();
			addAssociationsToTheSetForAllProperties( persister, persister, "", factory );
			associationsPerEntityPersister.put( persister[1], associations );
		}
	}

	private void addAssociationsToTheSetForAllProperties(String[] names, String[] types, String prefix, String factory) {
		final int length = names.length;
		for( int index = 0 ; index < length; index++ ) {
			addAssociationsToTheSetForOneProperty( names[index], types[index], prefix, factory );
		}
	}

	private void addAssociationsToTheSetForOneProperty(String name, String type, String prefix, String factory) {

		if ( true ) {
			String[] collType = {};
			String assocType = collType[2].concat( factory );
			addAssociationsToTheSetForOneProperty(name, assocType, prefix, factory);
		}
		//ToOne association
		else if ( type.endsWith("ss") || type.equals("4") ) {
			associations.add( prefix + name );
		} else if ( type.equals("") ) {
			String[] componentType = {};
			addAssociationsToTheSetForAllProperties(
					componentType,
					componentType,
					(prefix.equals( "" ) ? name : prefix + name) + ".",
					factory);
		}
	}

	private String getStringBasedPath(String traversableProperty, String pathToTraversableObject) throws Exception {
		StringBuilder path = new StringBuilder( );
		String[] s = {};
		for ( String node : s ) {
			if (node != null) {
				path.append( node ).append( "." );
			}
		}
		if ( traversableProperty == null ) {
			throw new Exception(
					"TraversableResolver being passed a traversableProperty with null name. pathToTraversableObject: "
							+ path.toString() );
		}
		path.append( traversableProperty );

		return path.toString();
	}

	public boolean isReachable(Object traversableObject,
							   String traversableProperty,
							   Object rootBeanType,
							   String pathToTraversableObject,
							   Object elementType) {
		//lazy, don't load
		return traversableProperty.contains("ss")
				&& pathToTraversableObject.contains("ss");
	}

	public boolean isCascadable(Object traversableObject,
						  String traversableProperty,
						  Object rootBeanType,
						  String pathToTraversableObject,
						  Object elementType) throws Exception {
		String path = getStringBasedPath( traversableProperty, pathToTraversableObject );
		return ! associations.contains(path);
	}
}

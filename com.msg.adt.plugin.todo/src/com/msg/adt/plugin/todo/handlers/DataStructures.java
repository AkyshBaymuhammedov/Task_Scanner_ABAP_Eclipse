package com.msg.adt.plugin.todo.handlers;

import java.util.HashMap;
import java.util.HashSet;

public class DataStructures {

	private static DataStructures instance;

	HashSet<String> validObjectTypes;
	HashSet<String> validGenericObjectTypes;
	HashMap<String, String> markerPrefixes;
	HashMap<String, String> markerSuffixes;

	public static DataStructures getInstance() {

		if (instance == null)
			instance = new DataStructures();

		return instance;
	}

	public HashSet<String> getValidObjectTypes() {

		if (validObjectTypes == null)
			prepareValidObjectTypes();

		return validObjectTypes;
	}

	public HashSet<String> getValidGenericObjectTypes() {

		if (validGenericObjectTypes == null)
			prepareValidGenericObjectTypes();

		return validGenericObjectTypes;
	}

	private void prepareValidGenericObjectTypes() {

		validGenericObjectTypes = new HashSet<>();
		validGenericObjectTypes.add("PROG/P");
		validGenericObjectTypes.add("FUGR/F");
		validGenericObjectTypes.add("FUGR/FF");
	}

	private void prepareValidObjectTypes() {

		validObjectTypes = new HashSet<>();
		validObjectTypes.add("AbapRepositoryPackageNode");
		validObjectTypes.add("AbapRepositoryTempPackageNode");
		validObjectTypes.add("AbapRepositoryClassNode");
		validObjectTypes.add("AbapRepositoryInterfaceNode");
		validObjectTypes.add("AbapRepositoryObjectGenericLaunchableNode");
		validObjectTypes.add("VirtualFolderNode");
		validObjectTypes.add("IncludeNode");
		validObjectTypes.add("Request");
		validObjectTypes.add("Task");
	}

	public HashMap<String, String> getMarkerPrefixes() {

		if (markerPrefixes == null)
			prepareMarkerPrefixes();

		return markerPrefixes;
	}

	private void prepareMarkerPrefixes() {

		markerPrefixes = new HashMap<>();
		markerPrefixes.put("INTF", "/.adt/classlib/interfaces/");
		markerPrefixes.put("CLAS", "/.adt/classlib/classes/");
		markerPrefixes.put("FM", "/.adt/functions/groups/");
		markerPrefixes.put("PROG", "/.adt/programs/programs/");
		markerPrefixes.put("INCL", "/.adt/programs/includes/");
		markerPrefixes.put("TEST", "/.adt/classlib/classes/");
		markerPrefixes.put("LCL", "/.adt/classlib/classes/");
	}

	public HashMap<String, String> getMarkerSuffixes() {

		if (markerSuffixes == null)
			prepareMarkerSuffixes();

		return markerSuffixes;
	}

	private void prepareMarkerSuffixes() {

		markerSuffixes = new HashMap<>();
		markerSuffixes.put("INTF", ".aint");
		markerSuffixes.put("CLAS", ".aclass");
		markerSuffixes.put("FM", ".asfunc");
		markerSuffixes.put("PROG", ".asprog");
		markerSuffixes.put("INCL", ".asinc");
		markerSuffixes.put("TEST", "testclasses.acinc");
		markerSuffixes.put("LCL", "implementations.acinc");
	}

}

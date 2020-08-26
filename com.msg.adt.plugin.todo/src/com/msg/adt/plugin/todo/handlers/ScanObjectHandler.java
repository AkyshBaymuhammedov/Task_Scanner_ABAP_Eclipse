package com.msg.adt.plugin.todo.handlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sap.adt.communication.exceptions.CommunicationException;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.IRestResourceFactory;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.compatibility.discovery.AdtDiscoveryFactory;
import com.sap.adt.compatibility.discovery.IAdtDiscovery;
import com.sap.adt.compatibility.discovery.IAdtDiscoveryCollectionMember;
import com.sap.adt.compatibility.model.templatelink.IAdtTemplateLink;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.projectexplorer.ui.internal.node.AbapRepositoryObjectGenericLaunchableNode;
import com.sap.adt.projectexplorer.ui.internal.virtualfolders.VirtualFolderNode;
import com.sap.adt.ris.search.ui.virtualfolders.IFacet;
import com.sap.adt.tm.IRequest;
import com.sap.adt.tm.ITask;
import com.sap.adt.tools.core.markers.AdtMarkerContext;
import com.sap.adt.tools.core.markers.AdtMarkerServiceFactory;
import com.sap.adt.tools.core.markers.IAdtMarkerService;
import com.sap.adt.tools.core.project.IAbapProject;
import com.msg.adt.plugin.todo.Activator;
import com.msg.adt.plugin.todo.preferences.PreferenceConstants;

@SuppressWarnings({ "restriction" })
public class ScanObjectHandler extends AbstractHandler {

	private static final String TYPE_NOT_FOUND_ERROR = "TYPE_NOT_FOUND";
	private static final String LOCALPACKAGE = "$TMP";
	private static final String ASCII_SLASH = "%2f";
	private static final String TRANSPORT_REQUEST = "Request";
	private static final String TASK = "Task";
	private static final String GENERIC_OBJECT_TYPE = "AbapRepositoryObjectGenericLaunchableNode";
	private static final String VIRTUAL_FOLDER_NODE = "VirtualFolderNode";
	private static final String URI_PARAMETER_OBJECT_NAME = "OBJ_NAME";
	private static final String URI_PARAMETER_SCAN_TODO = "SCAN_TODO";
	private static final String URI_PARAMETER_SCAN_FIXME = "SCAN_FIXME";
	private static final String URI_PARAMETER_SCAN_XXX = "SCAN_XXX";
	private static final String URI_PARAMETER_DEEP_SCAN = "DEEP_SCAN";
	private static final String URI_PARAMETER_CREATED_BY_ME = "CREATED_BY_ME";
	private static final String URI_PARAMETER_CUSTOM_TEXT = "CUSTOM_TEXT";
	private static final String URI_PARAMETER_SCAN_SOURCE_CODE = "SCAN_SOURCE_CODE";
	private static final String MSG_DISCOVERY = "/msg/akysh/discovery";
	private static final String TODO_RELATION = "http://www.msg.com/adt/relations/ab/todos";
	private static final String SCHEME = "http://www.msg.com/adt/categories/ab/todos";
	private static final String TERM = "todos";
	String objectToScan;
	ArrayList<TodoData> previousMarkers = new ArrayList<TodoData>();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			openDialogWindow("Shit", "No Window");
		}

		ISelection selection = window.getSelectionService().getSelection();

		TreePath[] paths = null;
		try {
			ITreeSelection treesel = (TreeSelection) selection;
			paths = treesel.getPaths();
		} catch (ClassCastException e) {
			displaySelectCorrectObjectPopup();
		}

		Object classObj = paths[0].getLastSegment();

		if (!isValid(classObj)) {
			displaySelectCorrectObjectPopup();
			return null;
		}

		objectToScan = classObj.toString();
		String objectType = classObj.getClass().getSimpleName();

		IAdaptable adaptable = (IAdaptable) classObj;
		if (objectType.equals(TRANSPORT_REQUEST)) {
			IRequest request = (IRequest) adaptable.getAdapter(IRequest.class);
			objectToScan = request.getNumber();
		}

		if (objectType.equals(TASK)) {
			ITask task = (ITask) adaptable.getAdapter(ITask.class);
			objectToScan = task.getNumber();
		}

		if (objectToScan.contains(LOCALPACKAGE)) {
			objectToScan = objectToScan.substring(objectToScan.lastIndexOf(" ") + 1);
		}

		IRestResourceFactory restResourceFactory = AdtRestResourceFactory.createRestResourceFactory();

		IProject project = ProjectUtil.getActiveAdtCoreProject(selection, null, null,
				IAdtCoreProject.ABAP_PROJECT_NATURE);

		IAbapProject abapProject = (IAbapProject) project.getAdapter(IAbapProject.class);
		String destination = abapProject.getDestinationId();

		Job myRequestJob = new Job("Checking for TODOs...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					URI todoUri = getURI(destination, objectToScan, monitor);
					IRestResource todoResource = restResourceFactory.createResourceWithStatelessSession(todoUri,
							destination);
					ContentHandler contentHandler = new ContentHandler();
					todoResource.addContentHandler(contentHandler);
					TodoDataList todoDataList = todoResource.get(monitor, TodoDataList.class);

					IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
					boolean cleanExistingMarkers = preferenceStore
							.getBoolean(PreferenceConstants.CLEAN_EXISTING_MARKERS);

					if (!previousMarkers.isEmpty() && cleanExistingMarkers) {
						clearMarkers(previousMarkers);
						previousMarkers.clear();
					}

					ArrayList<TodoData> list = todoDataList.getList();
					previousMarkers.addAll(list);

					if (isTypeNotFoundError(list)) {
						displaySelectCorrectObjectPopup();
						return Status.OK_STATUS;
					}

					if (list.isEmpty()) {
						displayNoTODOsFound();
						return Status.OK_STATUS;
					}

					list = setResourcesToTodoObjects(project, list);
					clearMarkers(list);
					createMarkersFromAdtObjects(list);

				} catch (CommunicationException e) {
					return new Status(IStatus.ERROR, "TEST", e.getLocalizedMessage(), e);
				} catch (ResourceException e) {
					return new Status(IStatus.ERROR, "TEST", e.getLocalizedMessage(), e);
				} catch (OperationCanceledException e) {
					return Status.CANCEL_STATUS;
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};

		myRequestJob.setUser(true);
		myRequestJob.schedule();
		return null;
	}

	private boolean isTypeNotFoundError(ArrayList<TodoData> list) {

		if (list.isEmpty()) {
			return false;
		}

		TodoData tododata = list.get(0);
		if (tododata.getType().equals(TYPE_NOT_FOUND_ERROR)) {
			return true;
		}

		return false;
	}

	private boolean isValid(Object classObject) {
		String objectType = classObject.getClass().getSimpleName();
		DataStructures structures = DataStructures.getInstance();

		Set<String> validObjectTypes = structures.getValidObjectTypes();
		if (!validObjectTypes.contains(objectType)) {
			return false;
		}

		if (objectType.equals(VIRTUAL_FOLDER_NODE)) {
			VirtualFolderNode folder = (VirtualFolderNode) classObject;
			IFacet facet = folder.getFolder().getLabel().getFacet();
			String facetKey = facet.getKey();
			if (!facetKey.equals("package") && !facetKey.equals("owner"))
				return false;

		}

		if (objectType.equals(GENERIC_OBJECT_TYPE)) {
			IAdaptable adaptable = (IAdaptable) classObject;
			AbapRepositoryObjectGenericLaunchableNode node = (AbapRepositoryObjectGenericLaunchableNode) adaptable
					.getAdapter(AbapRepositoryObjectGenericLaunchableNode.class);

			String genericType = node.getParentType();
			Set<String> validGenericObjectTypes = structures.getValidGenericObjectTypes();
			if (!validGenericObjectTypes.contains(genericType))
				return false;
		}

		return true;
	}

	private void displaySelectCorrectObjectPopup() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				openDialogWindow(
						"Please select an object with one of the following ABAP Object types: \nPackage | Program | Include | Class | Interface | Function Group | Function Module | Transport Request",
						"Wrong Object type");
			}
		});
	}

	private URI getURI(String destination, String objectToScan, IProgressMonitor monitor) {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean scanTodos = preferenceStore.getBoolean(PreferenceConstants.CHECK_TODO);
		boolean scanFixmes = preferenceStore.getBoolean(PreferenceConstants.CHECK_FIXME);
		boolean scanXxxs = preferenceStore.getBoolean(PreferenceConstants.CHECK_XXX);
		boolean deepScan = preferenceStore.getBoolean(PreferenceConstants.DEEP_SCAN);
		boolean createdByMe = preferenceStore.getBoolean(PreferenceConstants.SCAN_CREATED_BY_ME);
		boolean scanSourceCode = preferenceStore.getBoolean(PreferenceConstants.SCAN_SOURCE_CODE);
		String customText = preferenceStore.getString(PreferenceConstants.CUSTOM_TEXT).toString();

		if (customText.isEmpty()) {
			customText = " ";
		}

		IAdtUriTemplate uriTemplate = getUriTemplate(destination, monitor);

		String uri = uriTemplate.set(URI_PARAMETER_OBJECT_NAME, objectToScan).set(URI_PARAMETER_SCAN_TODO, scanTodos)
				.set(URI_PARAMETER_SCAN_FIXME, scanFixmes).set(URI_PARAMETER_SCAN_XXX, scanXxxs)
				.set(URI_PARAMETER_DEEP_SCAN, deepScan).set(URI_PARAMETER_CREATED_BY_ME, createdByMe)
				.set(URI_PARAMETER_CUSTOM_TEXT, customText).set(URI_PARAMETER_SCAN_SOURCE_CODE, scanSourceCode)
				.expand();

		URI todoUri = URI.create(uri);
		return todoUri;
	}

	private IAdtUriTemplate getUriTemplate(String destination, IProgressMonitor monitor) {
		IAdtDiscovery discovery = AdtDiscoveryFactory.createDiscovery(destination, URI.create(MSG_DISCOVERY));
		IAdtDiscoveryCollectionMember collectionMember = discovery.getCollectionMember(SCHEME, TERM, monitor);
		IAdtTemplateLink templateLink = collectionMember.getTemplateLink(TODO_RELATION);
		IAdtUriTemplate uriTemplate = templateLink.getUriTemplate();

		return uriTemplate;
	}

	private ArrayList<TodoData> setResourcesToTodoObjects(IProject activeProject, ArrayList<TodoData> todoList) {
		DataStructures dataStructures = DataStructures.getInstance();
		HashMap<String, String> markerSuffixes = dataStructures.getMarkerSuffixes();
		HashMap<String, String> markerPrefixes = dataStructures.getMarkerPrefixes();

		for (TodoData todoData : todoList) {
			String todoObjectName = todoData.getObjectName().toLowerCase();
			todoObjectName = todoObjectName.replaceAll("/", ASCII_SLASH);
			String todoObjectType = todoData.getObjectType();

			String path;
			if (todoObjectType.equals("FM")) {
				String parentName = todoData.getParent().toLowerCase();
				path = markerPrefixes.get(todoObjectType) + parentName + "/fmodules/" + todoObjectName + "/"
						+ todoObjectName + markerSuffixes.get(todoObjectType);
			} else {
				path = markerPrefixes.get(todoObjectType) + todoObjectName + "/" + todoObjectName
						+ markerSuffixes.get(todoObjectType);
			}

			IResource resource = activeProject.getFile(path);
			todoData.setResource(resource);
		}

		return todoList;
	}

	private void clearMarkers(ArrayList<TodoData> todoList) {
		for (TodoData todoData : todoList) {
			try {
				todoData.getResource().deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void createMarkersFromAdtObjects(ArrayList<TodoData> todoList) {
		IAdtMarkerService markerService = AdtMarkerServiceFactory.createMarkerService();

		for (TodoData todoData : todoList) {
			AdtMarkerContext markerContext = new AdtMarkerContext(todoData.getResource());

			markerContext.setMessage(todoData.getDescription());
			markerContext.setLine(todoData.getLine());
			markerContext.setPriority(IMarker.PRIORITY_HIGH);
			try {
				markerService.createMarkerWithAdtAttributes(markerContext, IMarker.PROBLEM);
			} catch (CoreException e1) { // TODO Auto-generated catch block e1.printStackTrace();

			}
		}
	}

	private void displayNoTODOsFound() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				openDialogWindow("Scanned objects do not contain any specified comments!", "Nothing found!");
			}
		});

	}

	protected void openDialogWindow(String dialogText, String dialogTitle) {
		String[] DIALOG_BUTTON_LABELS = new String[] { IDialogConstants.OK_LABEL };
		MessageDialog dialog = new MessageDialog(getShell(), dialogTitle, null, dialogText, MessageDialog.INFORMATION,
				DIALOG_BUTTON_LABELS, 0);
		dialog.open();
	}

	protected Shell getShell() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		return shell;
	}

}

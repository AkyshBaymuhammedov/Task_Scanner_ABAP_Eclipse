package com.msg.adt.plugin.todo.handlers;

import java.nio.charset.Charset;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.ContentHandlerException;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.content.AdtStaxContentHandlerUtility;

@SuppressWarnings("restriction")
public class ContentHandler implements IContentHandler<TodoDataList> {
	private static final String TODO_ELEMENT = "todo";
	private static final String ATTR_OBJECTNAME = "object_name";
	private static final String ATTR_OBJECTTYPE = "object_type";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_DESCRIPTION = "description";
	private static final String ATTR_LINE = "line";
	private static final String ATTR_PARENT = "parent";
	private static final String ATTR_PACKAGENAME = "package_name";

	protected final AdtStaxContentHandlerUtility utility = new AdtStaxContentHandlerUtility();

	@Override
	public TodoDataList deserialize(IMessageBody body, Class<? extends TodoDataList> dataType) {
		XMLStreamReader xsr = null;
		try {
			xsr = this.utility.getXMLStreamReader(body);
			TodoDataList todoDataList = new TodoDataList();

			for (int event = xsr.next(); event != XMLStreamReader.END_DOCUMENT; event = xsr.next()) {

				switch (event) {
				case XMLStreamReader.START_ELEMENT:
					if (TODO_ELEMENT.contentEquals(xsr.getLocalName())) {
						TodoData todoData = new TodoData();
						todoData.setObjectName(getTodoAttribute(xsr, ATTR_OBJECTNAME));
						todoData.setObjectType(getTodoAttribute(xsr, ATTR_OBJECTTYPE));
						todoData.setType(getTodoAttribute(xsr, ATTR_TYPE));
						todoData.setDescription(getTodoAttribute(xsr, ATTR_DESCRIPTION));
						todoData.setLine(Integer.valueOf(getTodoAttribute(xsr, ATTR_LINE)));
						todoData.setParent(getTodoAttribute(xsr, ATTR_PARENT));
						todoData.setPackageName(getTodoAttribute(xsr, ATTR_PACKAGENAME));
						todoDataList.addTodo(todoData);
					}
					break;
				}
			}

			return todoDataList;

		} catch (XMLStreamException e) {
			throw new ContentHandlerException(e.getMessage(), e);
		} catch (NumberFormatException e) {
			throw new ContentHandlerException(e.getMessage(), e);
		} finally {
			if (xsr != null) {
				this.utility.closeXMLStreamReader(xsr);
			}
		}
	}

	private String getTodoAttribute(XMLStreamReader xsr, String attributeName) {
		String attributeValue = xsr.getAttributeValue(null, attributeName);
		if (attributeValue == null) {
			throw new ContentHandlerException("Attribute " + attributeName + "not set");
		}
		return attributeValue;
	}

	@Override
	public String getSupportedContentType() {
		// TODO Auto-generated method stub
		return AdtMediaType.APPLICATION_XML;
	}

	@Override
	public Class<TodoDataList> getSupportedDataType() {
		// TODO Auto-generated method stub
		return TodoDataList.class;
	}

	@Override
	public IMessageBody serialize(TodoDataList arg0, Charset arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.msg.adt.plugin.todo.handlers;

import java.util.ArrayList;

public class TodoDataList {

	ArrayList<TodoData> list = new ArrayList<TodoData>();

	public void addTodo(TodoData todo) {
		list.add(todo);
	}

	public ArrayList<TodoData> getList() {
		return list;
	}

}

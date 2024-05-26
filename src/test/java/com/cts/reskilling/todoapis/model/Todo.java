package com.cts.reskilling.todoapis.model;

import java.time.LocalDate;

public class Todo {

	private Long todoId;

	private String username;

	private String title;

	private String description;

	private LocalDate endDate;

	private Boolean isCompleted;

	private Long priority = 10L;

	public Long getPriority() {
		return priority;
	}
	
	

	public Todo(String username, String title, String description, LocalDate endDate, Boolean isCompleted, Long priority) {
		super();
		this.username = username;
		this.title = title;
		this.description = description;
		this.endDate = endDate;
		this.isCompleted = isCompleted;
		this.priority = priority;
	}



	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public String retrieveUser() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Long getTodoId() {
		return todoId;
	}

}

package com.ms.coco.bean;

public class Helloworld {

	String	message;
    String id;
	public Helloworld() {
		
	}
	
	public Helloworld(String message) {
		this.message = message;
	}

    public Helloworld(String id, String message) {
        this.id = id;
        this.message = message;
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

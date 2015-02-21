package com.mechwreck.wireless;

/**
 * The response to a server/client's name request.
 */
public class NameResponse {
	
	private int requestId;
	private int index;
	private String name;
	
	/**
	 * Creates a new name response.
	 */
	public NameResponse() {
	}
	
	/**
	 * Creates a new name response.
	 */
	public NameResponse(int requestId, int index, String name) {
		this.requestId = requestId;
		this.index = index;
		this.name = name;
	}

	/**
	 * Get's the request id for this response's request.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public int getRequestId() {
		return requestId;
	}

	/**
	 * Gets the index of this response's request.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the name of the server/client.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public String getName() {
		return name;
	}

}

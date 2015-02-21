package com.mechwreck.wireless;

/**
 * Request sent when asking for a server/client's name.
 */
public class NameRequest {

	private int requestId;
	private int index;
	
	/**
	 * Creates a new name request.
	 */
	public NameRequest() {
	}

	/**
	 * Creates a new name request.
	 */
	public NameRequest(int requestId, int index) {
		this.requestId = requestId;
		this.index = index;
	}
	
	/**
	 * Returns the request's id.
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
	 * Returns the request's index.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public int getIndex() {
		return index;
	}

}

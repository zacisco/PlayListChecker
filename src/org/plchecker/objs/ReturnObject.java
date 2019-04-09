package org.plchecker.objs;

public class ReturnObject {
	private EntryChannel channel;
	private String group;
	private String message;
	private Integer code;

	public ReturnObject(EntryChannel channel, String group, String message, Integer code) {
		this.channel = channel;
		this.group = group;
		this.message = message;
		this.code = code;
	}

	/**
	 * @return the channel
	 */
	public EntryChannel getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(EntryChannel channel) {
		this.channel = channel;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}
}

package com.jasofalcon.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jasofalcon.message.MessageType;
import com.jasofalcon.user.User;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable{

    private User from;
    private User to;
    private MessageType type;
    private String data;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public void setData(String data) {
        this.data = data;
    }

    public String getData(){
        return data;
    }
}

package cloudapp;

import javax.persistence.Id;

public class UserEntity {
    @Id String name;
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public enum Type { NORMAL, ADMIN };
    Type type;
    private UserEntity() {}
    public UserEntity(String name, Type type){
    	this.name = name;
    	this.type = type;
    }
}

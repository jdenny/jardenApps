package chat.swing;

public class User {
	private String name;
	private String host;
	private int port;
	private String status;
	
	public User(String name, String host, int port) {
		this.name = name;
		this.host = host;
		this.port = port;
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof User)) return false;
		User u = (User)o;
		return (u.getHost().equals(this.getHost()) && u.getPort() == this.getPort());
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}



package jarden.engspa;

import jarden.provider.engspa.EngSpaContract.QAStyle;

public class EngSpaUser {
	private int userId;
	private String userName;
	private int userLevel;
	private QAStyle qaStyle;
	public EngSpaUser(int userId, String userName, int userLevel,
			QAStyle qaStyle) {
		this(userName, userLevel, qaStyle);
		this.userId = userId;
	}
	public EngSpaUser(String userName, int userLevel,
			QAStyle qaStyle) {
		this.userName = userName;
		this.userLevel = userLevel;
		this.qaStyle = qaStyle;
	}
	@Override
	public String toString() {
		return "EngSpaUser [userId=" + userId + ", userName=" + userName
				+ ", userLevel=" + userLevel +
				", qaStyle=" + qaStyle + "]";
	}
	public int getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
	public int getUserLevel() {
		return userLevel;
	}
	public QAStyle getQAStyle() {
		return qaStyle;
	}
	public void setQAStyle(QAStyle qaStyle) {
		this.qaStyle = qaStyle;
	}
	public void setId(int userId) {
		this.userId = userId;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
}

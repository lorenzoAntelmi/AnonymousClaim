package com.claim.model;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Represents the Setting of a Player
 * @author Deborah Vanzin
*/

@Entity
public class Setting {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer accountId;
	private String settingKey;
	private Integer valueInt;
	private Integer valueString;
	private Blob valueBlob;
	public Setting(Integer id, Integer accountId, String settingKey, Integer valueInt, Integer valueString, Blob valueBlob) {
		super();
		this.id = id;
		this.accountId = accountId;
		this.settingKey = settingKey;
		this.valueInt = valueInt;
		this.valueString = valueString;
		this.valueBlob = valueBlob;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer settingId) {
		this.id = id;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getSettingKey() {
		return settingKey;
	}

	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}

	public Integer getValueInt() {
		return valueInt;
	}

	public void setValueInt(Integer valueInt) {
		this.valueInt = valueInt;
	}

	public Integer getValueString() {
		return valueString;
	}

	public void setValueString(Integer valueString) {
		this.valueString = valueString;
	}

	public Blob getValueBlob() {
		return valueBlob;
	}

	public void setValueBlob(Blob valueBlob) {
		this.valueBlob = valueBlob;
	}
}

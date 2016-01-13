package br.com.evertontrindade.bean;

import java.io.InputStream;

public class Bean {

	private String crm;
	private InputStream dataFile;

	public Bean(String crm, InputStream dataFile) {
		this.crm = crm;
		this.dataFile = dataFile;
	}
	
	public String getCrm() {
		return crm;
	}
	
	public InputStream getDataFile() {
		return dataFile;
	}

	@Override
	public String toString() {
		return "Bean [crm=" + crm + ", dataFile=" + dataFile + "]";
	}

}

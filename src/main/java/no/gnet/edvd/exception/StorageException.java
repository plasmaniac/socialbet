package no.gnet.edvd.exception;

import java.sql.SQLException;

public class StorageException extends RuntimeException {

	public StorageException(SQLException e) {
		super(e);
	}

	public StorageException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}

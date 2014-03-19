package il.ac.idc.jdt;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

public class Utils {

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	public static void closeQuietly(Writer writer) {
		closeQuietly((Closeable) writer);
	}

	public static boolean isNumeric(CharSequence cs) {
		if (cs == null || cs.length() == 0) {
			return false;
		}
		int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
}

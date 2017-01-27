package scripts;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.canteratech.restclient.annotation.RestAttribute;
import com.canteratech.restclient.annotation.RestObject;

public class Comparator {

	public void compare(String name, Object o, Object n, String... ignorePatterns) {
		boolean validateEmpty = true;
		for (String r : ignorePatterns) {
			boolean empty = false;
			if (r.startsWith("empty!")) {
				r = r.replaceFirst("empty!", "");
				empty = true;
			}
			Pattern pattern = Pattern.compile(r);
			Matcher matcher = pattern.matcher(name);
			if (matcher.matches()) {
				if (empty) {
					validateEmpty = false;
				} else {
					return;
				}
			}
		}
		if (o == null && n == null) {
			if (validateEmpty) {
				throw new RuntimeException(name + " :: " + "NULL");
			} else {
				return;
			}
		}
		if (o == null) {
			throw new RuntimeException(name + " :: " + "NULL old, new: " + n);
		}
		if (n == null) {
			throw new RuntimeException(name + " :: " + "NULL new, old: " + o);
		}
		if (o.getClass() != n.getClass()) {
			throw new RuntimeException(name + " :: " + "Different class: " + o.getClass() + " vs " + n.getClass());
		}
		if (o instanceof List) {
			List ol = (List) o;
			List nl = (List) n;
			if (ol.size() != nl.size()) {
				throw new RuntimeException(name + " :: " + "Different list size: " + ol.size() + " vs " + nl.size());
			}
			if (validateEmpty && ol.isEmpty()) {
				throw new RuntimeException(name + " :: " + "List empty");
			}
			int i = 0;
			while (i < ol.size()) {
				compare(name + "[" + i + "]", ol.get(i), nl.get(i), ignorePatterns);
				i++;
			}
		} else if (o.getClass().isAnnotationPresent(RestObject.class)) {
			List<Field> fields = new ArrayList<>();
			Class<?> clazzTmp = o.getClass();
			while (clazzTmp != null && !clazzTmp.equals(Object.class)) {
				Field[] declaredFields = clazzTmp.getDeclaredFields();
				for (Field declaredField : declaredFields) {
					if (declaredField.isAnnotationPresent(RestAttribute.class)) {
						if (declaredField.getAnnotation(RestAttribute.class).ignore()) {
							continue;
						}
					}
					if (!Modifier.isStatic(declaredField.getModifiers())) {
						declaredField.setAccessible(true);
						fields.add(declaredField);
					}
				}
				clazzTmp = clazzTmp.getSuperclass();
			}
			for (Field field : fields) {
				try {
					Object o1 = field.get(o);
					Object n1 = field.get(n);
					compare(name + "." + field.getName() ,o1, n1, ignorePatterns);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		} else if ((o instanceof Integer) || (o instanceof String) || (o instanceof Double) || (o instanceof Boolean) || (o instanceof Date)) {
			if (!o.equals(n)) {
				if ((o instanceof Date)) {
					System.out.println(((Date)o).getTime());
					System.out.println(((Date)n).getTime());
				}
				throw new RuntimeException(name + " :: " + "Different " + o.getClass() +  " value: " + o + " vs " + n);
			}
		} else {
			throw new RuntimeException(name + " :: " + "Unknown class: " + o.getClass());
		}
	}
}

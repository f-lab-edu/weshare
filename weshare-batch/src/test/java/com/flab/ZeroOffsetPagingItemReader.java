package com.flab;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.flab.core.entity.Card;

import jakarta.persistence.Id;

public class ZeroOffsetPagingItemReader {

	@ParameterizedTest
	@CsvSource(value = {
		"select * from Test t,select * from Test t WHERE t.id <= 10"
		, "select * from Test t where t.bb > 2,select * from Test t where t.bb > 2 AND t.id <= 10"
		,
		"select * from Test t where t.bb > 2 group by t.cc,select * from Test t where t.bb > 2 AND t.id <= 10 group by t.cc"
	})
	void where절추출테스트(String jpql, String expected) {
		String newClause = extractAlias(jpql) + ".id <= 10";
		String modifiedClause;

		String lowerJpql = jpql.toLowerCase();
		int whereIndex = lowerJpql.indexOf(" where ");
		int orderByIndex = lowerJpql.indexOf(" order by ");
		int groupByIndex = lowerJpql.indexOf(" group by ");
		int endIndex = jpql.length();

		if (groupByIndex != -1 && (orderByIndex == -1 || groupByIndex < orderByIndex)) {
			endIndex = groupByIndex;
		} else if (orderByIndex != -1) {
			endIndex = orderByIndex;
			//이땐, 따로 처리해햐함.
		}

		if (whereIndex == -1) {
			// `WHERE` 절이 없는 경우
			String fromClause = jpql.substring(0, endIndex);
			String remainingClause = jpql.substring(endIndex);
			modifiedClause = fromClause + " WHERE " + newClause + remainingClause;
		} else {
			// `WHERE` 절이 있는 경우
			String beforeWhere = jpql.substring(0, whereIndex + 7);
			String afterWhere = jpql.substring(whereIndex + 7, endIndex);
			String remainingClause = jpql.substring(endIndex);
			modifiedClause = beforeWhere + afterWhere + " AND " + newClause + remainingClause;
		}

		assertThat(modifiedClause).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource(value = {"select * from Test t,t"
		, "select * from Test tc,tc"
		, "select * from Test ta where ta.col > 22,ta"
		, "select * from Test tb group by tb.cc ,tb"})
	void extract_alias_test(String jpql, String alias) {
		assertThat(extractAlias(jpql)).isEqualTo(alias);
	}

	public String extractAlias(String jpql) {
		String lowerJpql = jpql.toLowerCase();
		int fromIndex = lowerJpql.indexOf(" from ");

		StringBuilder alias = new StringBuilder();
		int spaceCount = 0;
		for (int i = fromIndex; i < lowerJpql.length(); i++) {
			char c = jpql.charAt(i);
			if (c == ' ') {
				spaceCount++;
				if (spaceCount > 3) {
					break;
				}
				continue;
			}

			if (spaceCount == 3) {
				alias.append(c);
			}
		}
		return alias.toString();
	}

	@Test
	void find_pk_field() throws IllegalAccessException {
		Tester<Card> x = new Tester<>();
		//Class<?> actualTypeArgument1 = (Class<?>)((ParameterizedType)x.type).getActualTypeArguments()[0];
		//System.out.println(actualTypeArgument1.getName());
	}

	@Test
	void init_test() {
		Tester2<Integer> x = new Tester2<>();
		Type[] gTypes = ((ParameterizedType)x.getClass().getGenericSuperclass()).getActualTypeArguments();
		for (Type gType : gTypes) {
			System.out.println("Generic type:" + gType.getTypeName());
		}
	}

	public static class Tester<T> {
	}

	public static class Tester2<T> extends Tester<T> {
	}

	@Test
	void number_test() {
		if (Number.class.isAssignableFrom(Long.class)) {
			System.out.println("true");
		}
	}

	public void check(Class<?> clazz, Object object) throws IllegalAccessException {
		for (Field field : clazz.getDeclaredFields()) {
			// Check for MyAnnotation
			Id myAnnotation = field.getAnnotation(Id.class);
			if (myAnnotation == null) {
				System.out.println(
					"Field " + field.getName() + " - 이것은 id 필드가 아니다 ");
			} else {
				field.setAccessible(true);
				System.out.println("Fied " + field.getName() + "이것이 id 필드이다. value: " + field.get(object));
			}
		}
	}
}

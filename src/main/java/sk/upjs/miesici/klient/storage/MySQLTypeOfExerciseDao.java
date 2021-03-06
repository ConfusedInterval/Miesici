package sk.upjs.miesici.klient.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import sk.upjs.miesici.admin.storage.Customer;

public class MySQLTypeOfExerciseDao implements TypeOfExerciseDao {

	private JdbcTemplate jdbcTemplate;
	private Customer customer;

	public MySQLTypeOfExerciseDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public List<TypeOfExercise> getAll() {
		String sql = "SELECT * from typ_cviku";
		return jdbcTemplate.query(sql, new TypeOfExerciseResultSetExtractor());
	}

	@Override
	public List<TypeOfExercise> getAllByClientId(Long clientId) {
		String sql = "SELECT * from typ_cviku where klient_id = " + clientId;
		return jdbcTemplate.query(sql, new TypeOfExerciseResultSetExtractor());
	}

	@Override
	public TypeOfExercise save(TypeOfExercise typeOfExercise) {
		boolean name = true;
		if (customer != null) {
			List<TypeOfExercise> types = getAllByClientId(customer.getId());
			typeOfExercise.setClientId(customer.getId());
			for (TypeOfExercise type : types) {
				if (type.getName().equals(typeOfExercise.getName())) {
					name = false;
					break;
				}
			}
		}
		if (name) {
			SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
			insert.withTableName("typ_cviku").usingGeneratedKeyColumns("id");
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("nazov", typeOfExercise.getName());
			values.put("klient_id", typeOfExercise.getClientId());
			Number key = insert.executeAndReturnKey(new MapSqlParameterSource(values));
			typeOfExercise.setId(key.longValue());
			return typeOfExercise;
		}
		return null;
	}

}

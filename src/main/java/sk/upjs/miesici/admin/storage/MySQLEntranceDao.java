package sk.upjs.miesici.admin.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;

public class MySQLEntranceDao implements EntranceDao {
	private JdbcTemplate jdbcTemplate;

	public MySQLEntranceDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Entrance> getAll() {
		String sql = "SELECT id, klient_id, kt.meno, kt.priezvisko, prichod, odchod, skrinka, cas " + "FROM vstup "
				+ "JOIN klient AS kt " + "USING(klient_id)";
		List<Entrance> result = jdbcTemplate.query(sql, new EntranceResultSetExtractor());
		return result;
	}

	@Override
	public Entrance saveArrival(Entrance entrance) {
		if (entrance.getId() == null) {
			SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
			insert.withTableName("vstup").usingGeneratedKeyColumns("id");
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("klient_id", entrance.getKlient_id());
			values.put("meno", entrance.getName());
			values.put("priezvisko", entrance.getSurname());
			values.put("prichod", entrance.getArrival());
			values.put("skrinka", entrance.getLocker());
			Number key = insert.executeAndReturnKey(new MapSqlParameterSource(values));
			entrance.setId(key.longValue());
		}
		return entrance;
	}

	@Override
	public void saveExit(Entrance entrance) {
		if (entrance.getKlient_id() != null) {
			String sql = "UPDATE vstup SET odchod = ? , " + "cas = ? " + "WHERE id = ?;";
			jdbcTemplate.update(sql, entrance.getExit(), entrance.getTime(), entrance.getId());
		}
	}

	@Override
	public List<Entrance> getByCustomerId(long id) {
		List<Entrance> entries = getAll();
		List<Entrance> entriesById = new ArrayList<Entrance>();
		for (Entrance entry : entries) {
			if (entry.getKlient_id() == id) {
				entriesById.add(entry);
			}
		}
		return entriesById;
	}
}
